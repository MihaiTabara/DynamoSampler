/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */

package storage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import loadbalancer.LoadBalancer;
import environment.Command;
import environment.Command.Type;
import environment.Constants;
import environment.Hasher;
import environment.KeyVersionReply;
import environment.KeyVersionRequest;
import environment.Mailman;
import environment.NodeCounterTuple;
import environment.ReplicateCommand;
import environment.TaskCapsule;
import environment.VectorClock;

/**
 * @author mtabara
 * The Storage node runner describes the behavior of the Storage node
 * each time a piece of information is exchanged. 
 */
public class StorageNodeRunner extends Thread {
	/**
	 * A reference to the basic - process - storage node (where all the metadata is 
	 * being store)
	 */
	private StorageNode node;
	/**
	 * The socket where the information pops in each time a connection is established
	 */
	private Socket communicationSocket;
	/**
	 * The global locker used to sync the threads when replicas request is called into 
	 * service
	 */
	private static final Object locker = new Object();
	/**
	 * Global variable to use with the same purpose as the aforementioned locker object
	 */
	private static int noOfReplicasToReceive;
	/**
	 * Variable to store all the vector clocks received from the other nodes 
	 * in the preference list for a specific key, assumed that the current storage 
	 * node is the coordinator
	 */
	private static List<VectorClock> requestedVersions = new ArrayList<>(); 
	/**
	 * The actual key-value storage used
	 */
	@SuppressWarnings("rawtypes")
	private static HashMap storage = new HashMap<>();
	/**
	 * The Vector-clock retaining data-structure
	 */
	private static HashMap<Integer, VectorClock> versions = new HashMap<>();
	
	
	public StorageNodeRunner(StorageNode node, Socket clientSocket) {
		this.node = node;
		this.communicationSocket = clientSocket;
	}


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


	/**
	 * The method which classifies the behavior of the storage node 
	 * depending on the content received
	 * @param content
	 * @throws Exception
	 */
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
		else if (content instanceof ReplicateCommand) {
			processReplica(content);
		}
	}


	/**
	 * The method is to describe the storage node's behavior each time
	 * a replica is received
	 * @param content
	 */
	@SuppressWarnings("unchecked")
	private void processReplica(Object content) {
		ReplicateCommand replica = (ReplicateCommand)content;
		StorageNode.logger.info("Received a Replica" + replica.toString());
		
		VectorClock updatedVC = replica.getReplicaVC();
		int keyRingPosition = replica.getKeyRingPosition();
		Integer keyRingPositionInteger = new Integer(keyRingPosition);
		String newValue = updatedVC.getValue();
		
		updatedVC.update(this.node.getMetadata().getNodeName(), newValue);
		
		storage.put(keyRingPosition, newValue);
		versions.put(keyRingPositionInteger, updatedVC);
		StorageNode.logger.info("Updated all my storages with the REPLICATE results");
		
		StorageNode.logger.info("...___ storage " + storage.toString());
		StorageNode.logger.info("...___ versions " + versions.toString());
	}


	/**
	 * The method is to describe the storage node's behavior each time
	 * a key version reply is received
	 */
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


	/**
	 * The method is to describe the storage node's behavior each time
	 * a key version request is received
	 * @param content
	 * @throws Exception
	 */
	private void processKeyVersionRequest(Object content) throws Exception {
		KeyVersionRequest req = (KeyVersionRequest)content;
		StorageNode.logger.info("Received a KeyVersionRequest: " + req.toString());
		
		String key = req.getKey();
		
		int keyRingPosition = Hasher.getRingPosition(key, false);
		Integer keyRingPositionInteger = new Integer(keyRingPosition);
		
		if (!versions.containsKey(keyRingPositionInteger)) {
			versions.put(keyRingPositionInteger, new VectorClock());
		}
		
		VectorClock reply = versions.get(keyRingPositionInteger);
		KeyVersionReply k = new KeyVersionReply(reply, this.node.getMetadata().getPort(), req.getSourcePort());
		
		Mailman mailMan = new Mailman(Constants.GENERIC_HOST, req.getSourcePort());
		mailMan.composeMail(new TaskCapsule(k));
		mailMan.sendMail();
		
		StorageNode.logger.info("Replied a KeyVersionReply to port " + req.getSourcePort());
	}


	/**
	 * The method is to describe the storage node's behavior each time
	 * a new command arrives - it either processes it or it forwards it
	 * to the right owner
	 * @param content
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws Exception
	 */
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


	/**
	 * The forward method if the storage node is not among the preference list nodes 
	 * for a specific key
	 * @param command
	 * @param key
	 * @throws Exception
	 */
	private void forwardCapsule(Command command, String key) throws Exception {
		StorageNode.logger.info("I will forward it to its right owner.");
		StorageNodeMetadataCapsule coordinator = this.node.getKeyCoordinator(key);
		
		Mailman mailMan = new Mailman(Constants.GENERIC_HOST, coordinator.getPort());
		mailMan.composeMail(new TaskCapsule(command));
		mailMan.sendMail();
		
		StorageNode.logger.info("Task forwarded to " + coordinator.toString());
	}


	/**
	 * The method to take into account the different operations that are possible
	 * @param command
	 * @param key
	 * @throws Exception
	 */
	private void handleCommand(Command command, String key) throws Exception {
		Type cmdType = command.getType();
		
		if (cmdType == Type.GET) {
			processGETCommand(command, key);
		}
		else if (cmdType == Type.PUT) {
			processPUTCommand(command, key);
		}
	}


	/**
	 * The method is to describe the storage node's behavior each time
	 * a PUT command is received
	 * @param command
	 * @param key
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void processPUTCommand(Command command, String key) throws Exception {
		StorageNode.logger.info("Received PUT command!");
		StorageNode.logger.info("First, gather the other replicas from around");
		
		gatherReplicasVersions(key);
		
		List<VectorClock> tmpVersions = new ArrayList<>(requestedVersions);
		int keyRingPosition = Hasher.getRingPosition(key, false);
		Integer keyRingPositionInteger = new Integer(keyRingPosition);
		
		StorageNode.logger.info("Prepare to merge them and update my storage gear.");
		
		String newValue = command.getValue();
		StorageNode.logger.info("PUT Command message looks like this " + command.toString());
		
		StorageNode.logger.info("Context before merge " + tmpVersions.toString());	
		VectorClock context = mergeVectorClocks(tmpVersions);
		context.update(this.node.getMetadata().getNodeName(), newValue);
		
		storage.put(keyRingPosition, newValue);
		versions.put(keyRingPositionInteger, context);
		
		StorageNode.logger.info("Context after merge: " + context.toString());
		StorageNode.logger.info("+++___ storage " + storage.toString());
		StorageNode.logger.info("+++___ versions " + versions.toString());
		
		Thread.sleep(4000);
		
		StorageNode.logger.info("Sending REPLICATE command to all other nodes from preference list");
		List<StorageNodeMetadataCapsule> prefList = this.node.getPreferenceListForAKey(key);
		for (StorageNodeMetadataCapsule s : prefList) {
			if (!s.getNodeName().equals(this.node.getMetadata().getNodeName())) {
				ReplicateCommand replica = new ReplicateCommand(keyRingPosition, versions.get(keyRingPositionInteger));
				Mailman mailMan = new Mailman(Constants.GENERIC_HOST, s.getPort());
				mailMan.composeMail(new TaskCapsule(replica));
				mailMan.sendMail();
			}
		}
		
	}


	/**
	 * The method describes the behavior of the storage node
	 * after it receives all the key version reply and merges them
	 * to get a proper answer
	 * @param tmpVersions
	 * @return
	 */
	private VectorClock mergeVectorClocks(List<VectorClock> tmpVersions) {
		VectorClock ret = new VectorClock();
		HashMap<String, Integer> reducer = new HashMap<>();
		
		for (VectorClock vc : tmpVersions) {
			if (vc.getValue() == "") {
				continue;
			}
			
			for (NodeCounterTuple clock : vc.getClocks()) {
				String nodename = clock.getNodeName();
				Integer counter = new Integer(clock.getCounter());
				
				if (reducer.containsKey(nodename) && reducer.get(nodename) >= counter) {
					continue;
				}
				reducer.put(nodename, counter);
			}
		}
		
		for (String k : reducer.keySet() ) {
			ret.getClocks().add(new NodeCounterTuple(k, reducer.get(k)));
		}
		
		return ret;
	}

	/**
	 * The method is to describe the storage node's behavior each time
	 * a GET command is received
	 * @param command
	 * @param key
	 * @throws Exception
	 */
	private void processGETCommand(Command command, String key) throws Exception {
		StorageNode.logger.info("I am part of the preference list for key <" + key + ">");
		
		gatherReplicasVersions(key);
		
		StorageNode.logger.info("Prepare to check the other replicas.");
		List<VectorClock> tmpVersions = new ArrayList<>(requestedVersions);
		int keyRingPosition = Hasher.getRingPosition(key, false);
		
		boolean differentValuesAmongReplicas = checkReplicas(tmpVersions);
		String ret;
		if (differentValuesAmongReplicas) {
			ret = (String)tmpVersions.toString(); 
		}
		else {
			ret = (String) storage.get(keyRingPosition);
		}
		
		StorageNode.logger.info("The GET result is " + ret);
		StorageNode.logger.info("Command message looks like this " + command.toString());
		
		Mailman mailMan = new Mailman(Constants.GENERIC_HOST, command.getSourcePort());
		mailMan.composeMail(new TaskCapsule(ret));
		mailMan.sendMail();
		StorageNode.logger.info("Sent the GET result to client!");
	}


	/**
	 * The method is to describe what a storage node does after gathering 
	 * multiple replica versions from the other nodes in the preference list
	 * @param tmpVersions
	 * @return
	 */
	private boolean checkReplicas(List<VectorClock> tmpVersions) {
		HashSet<String> set = new HashSet<>();
		for (VectorClock clock : tmpVersions) {
			set.add(clock.getValue());
		}
		
		if (set.size() > 1)
			return true;
		
		return false;
	}


	/**
	 * Common method used by both the GET and PUT command to gather replicas versions
	 * from the other nodes in the preference list
	 * @param key
	 * @throws Exception
	 */
	private void gatherReplicasVersions(String key) throws Exception {
		requestedVersions.clear();
		
		int keyRingPosition = Hasher.getRingPosition(key, false);
		Integer keyRingPositionInteger = new Integer(keyRingPosition);
		
		if (!versions.containsKey(keyRingPositionInteger)) {
			versions.put(keyRingPositionInteger, new VectorClock());
		}
		requestedVersions.add(versions.get(keyRingPositionInteger));
		
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
		
	}


	/**
	 * The method to describe the behavior of the storage node each time
	 * it receives new information about the other storage nodes from the
	 * load balancer
	 * @param content
	 */
	@SuppressWarnings("unchecked")
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
