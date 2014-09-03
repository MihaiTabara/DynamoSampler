/**
 * 
 */
package loadbalancer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;

import storage.StorageNodeMetadataCapsule;
import environment.Command;
import environment.Constants;
import environment.Mailman;
import environment.TaskCapsule;

/**
 * @author mtabara
 *
 */
public class LoadBalancerRunner extends Thread {
	private LoadBalancer loadBalancer;
	private Socket communicationSocket;
	
	public LoadBalancerRunner(LoadBalancer loadBalancer, Socket clientSocket) {
		this.loadBalancer = loadBalancer;
		this.communicationSocket = clientSocket;
	}

	/* 
	 * 
	 */
	@Override
	public void run() {
		try {
			LoadBalancer.logger.info("I'm a load balancer runner - I'll check on what's this about ...");
			ObjectInputStream inputStream = new ObjectInputStream(communicationSocket.getInputStream());
			
			try {
				TaskCapsule receivedTaskCapsule = (TaskCapsule)inputStream.readObject();
				Object content = receivedTaskCapsule.getContent();
				analyzeContent(content);
				
			} catch (ClassNotFoundException e) {
				LoadBalancer.logger.log(Level.SEVERE, e.getMessage(), e);
			}
			
			inputStream.close();
			communicationSocket.close();
			
		} catch (IOException e) {
			LoadBalancer.logger.log(Level.SEVERE, e.getMessage(), e);
		}
			
	}

	private void analyzeContent(Object content) throws UnknownHostException, IOException {
		if (content instanceof StorageNodeMetadataCapsule) {
			LoadBalancer.logger.info("A new Storage node requested to join the ring.");
			StorageNodeMetadataCapsule nodeMetadata = (StorageNodeMetadataCapsule)content;
			LoadBalancer.logger.info("Received this metadata: " + nodeMetadata.toString());
			
			this.loadBalancer.storageNodesMetadata.add(nodeMetadata);
			LoadBalancer.logger.info("Added new node in the system.");
			
			LoadBalancer.logger.info("Send updated-array of storage nodes with broadcast");
			for (StorageNodeMetadataCapsule capsule : this.loadBalancer.storageNodesMetadata) {
				Mailman mailMan = new Mailman(Constants.GENERIC_HOST, capsule.getPort());
				mailMan.composeMail(new TaskCapsule(this.loadBalancer.storageNodesMetadata));
				mailMan.sendMail();
			}
		}
		else if (content instanceof Command) {
			Command commandContent = (Command)content;
			StorageNodeMetadataCapsule randomRecipient = this.loadBalancer.getRandomStorageNode();
			LoadBalancer.logger.info("Shuffled storage nodes and had chosen: " + randomRecipient.toString());
			
			Mailman mailMan = new Mailman(Constants.GENERIC_HOST, randomRecipient.getPort());
			mailMan.composeMail(new TaskCapsule(commandContent));
			mailMan.sendMail();
		}
		
	}

}
