/**
 * 
 */
package storage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import loadbalancer.LoadBalancer;
import environment.Command;
import environment.Command.Type;
import environment.Constants;
import environment.KeyVersionReply;
import environment.KeyVersionRequest;
import environment.Mailman;
import environment.TaskCapsule;

/**
 * @author mtabara
 *
 */
public class StorageNodeRunner extends Thread {
	private StorageNode node;
	private Socket communicationSocket;
	private static final Object locker = new Object();
	private static int noOfReplicasToReceive;
	private static List<String> requestedVersions = new ArrayList<>();
	
	
	public StorageNodeRunner(StorageNode node, Socket clientSocket) {
		this.node = node;
		this.communicationSocket = clientSocket;
	}


	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			StorageNode.logger.info("I'm a node runner - I'll check on what's this about ...");
			ObjectInputStream inputStream = new ObjectInputStream(communicationSocket.getInputStream());
			
			try {
				TaskCapsule receivedTaskCapsule = (TaskCapsule)inputStream.readObject();
				Object content = receivedTaskCapsule.getContent();
				analyzeContent(content);
				
			} catch (ClassNotFoundException e) {
				StorageNode.logger.log(Level.SEVERE, e.getMessage(), e);
			} catch (Exception e) {
				StorageNode.logger.log(Level.SEVERE, e.getMessage(), e);
			}
			
			inputStream.close();
			communicationSocket.close();
			
		} catch (IOException e) {
			LoadBalancer.logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}


	private void analyzeContent(Object content) throws Exception {
		if (content instanceof ArrayList) {
			processMetadata(content);
		}
		else if (content instanceof Command) {
			processCommand(content);
		}
		else if (content instanceof KeyVersionRequest) {
			processKeyVersionRequest(content);
		}
		else if (content instanceof KeyVersionReply) {
			processKeyVersionReply(content);
		}
	}


	private void processKeyVersionReply(Object content) {
		KeyVersionReply receivedAnswer = (KeyVersionReply)content;
		StorageNode.logger.info("Received a KeyVersionReply " + receivedAnswer.toString() + " from port" + receivedAnswer.getSourcePort());
		
		requestedVersions.add(receivedAnswer.getReply());
		
		synchronized (locker) {
			noOfReplicasToReceive--;
			if (noOfReplicasToReceive == 0) {
				locker.notify();
			}
		}		
	}


	private void processKeyVersionRequest(Object content) throws UnknownHostException, IOException {
		KeyVersionRequest req = (KeyVersionRequest)content;
		StorageNode.logger.info("Received a KeyVersionRequest: " + req.toString());
		
		String key = req.getKey();
		String reply = key + "gigelcostel";
		
		KeyVersionReply k = new KeyVersionReply(reply, this.node.getMetadata().getPort(), req.getSourcePort());
		
		Mailman mailMan = new Mailman(Constants.GENERIC_HOST, req.getSourcePort());
		mailMan.composeMail(new TaskCapsule(k));
		mailMan.sendMail();
		
		StorageNode.logger.info("Replied a KeyVersionReply to port " + req.getSourcePort());
	}


	private void processCommand(Object content) throws UnknownHostException, IOException, Exception {
		StorageNode.logger.info("Received a (forwarded) Command from (load balancer) another node");
		
		Command command = (Command)content;
		String key = command.getkey();
		
		if (this.node.patternityTest(key)) {
			handleCommand(command, key);
		}
		else {
			forwardCapsule(command, key);
		}	
	}


	private void forwardCapsule(Command command, String key) throws Exception {
		StorageNode.logger.info("I will forward it to its right owner.");
		StorageNodeMetadataCapsule coordinator = this.node.getKeyCoordinator(key);
		
		Mailman mailMan = new Mailman(Constants.GENERIC_HOST, coordinator.getPort());
		mailMan.composeMail(new TaskCapsule(command));
		mailMan.sendMail();
		
		StorageNode.logger.info("Task forwarded to " + coordinator.toString());
	}


	private void handleCommand(Command command, String key) throws Exception {
		Type cmdType = command.getType();
		
		if (cmdType == Type.GET) {
			processGETCommand(command, key);
		}
		else if (cmdType == Type.PUT) {
			processPUTCommand(command, key);
		}
	}


	private void processPUTCommand(Command command, String key) {
		// TODO Auto-generated method stub
		
	}


	private void processGETCommand(Command command, String key) throws Exception {
		StorageNode.logger.info("I am part of the preference list for key <" + key + ">");
		requestedVersions.clear();
		requestedVersions.add(new String("mock"));
		
		List<StorageNodeMetadataCapsule> prefList = this.node.getPreferenceListForAKey(key);
		for (StorageNodeMetadataCapsule s : prefList) {
			if (!s.getNodeName().equals(this.node.getMetadata().getNodeName())) {
				KeyVersionRequest k = new KeyVersionRequest(key, this.node.getMetadata().getPort(), s.getPort());
				Mailman mailMan = new Mailman(Constants.GENERIC_HOST, s.getPort());
				mailMan.composeMail(new TaskCapsule(k));
				mailMan.sendMail();
			}
		}
		
		synchronized (locker) {
			try {
				noOfReplicasToReceive = Constants.DYNAMO_R;
				locker.wait();
			} catch (InterruptedException e) {
				StorageNode.logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		System.out.println("+++++");
		System.out.println("#<3# " + requestedVersions.toString());
		System.out.println("+++++");
		
	}


	private void processMetadata(Object content) {
		StorageNode.logger.info("Load balancer sent a broadcast!");
		this.node.allNodes = (ArrayList<StorageNodeMetadataCapsule>)content;
		
		Collections.sort(this.node.allNodes, new Comparator<StorageNodeMetadataCapsule>() {
			public int compare(StorageNodeMetadataCapsule a, StorageNodeMetadataCapsule b) {
				return Integer.valueOf(a.getPosition()).compareTo(Integer.valueOf(b.getPosition()));
			}
		});
		
		StorageNode.logger.info("Sorted out my nodes: " + this.node.allNodes.toString());
	}
}
