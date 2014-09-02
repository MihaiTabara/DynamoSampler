/**
 * 
 */
package loadbalancer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;

import storage.StorageNodeMetadataCapsule;
import environment.TaskCapsule;

/**
 * @author mtabara
 *
 */
public class LoadBalancerRunner extends Thread {
	private Socket communicationSocket;
	private LoadBalancer loadBalancer;
	
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
			LoadBalancer.logger.info("I'm a runner - I'll check on what's this about ...");
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

	private void analyzeContent(Object content) {
		if (content instanceof StorageNodeMetadataCapsule) {
			LoadBalancer.logger.info("A new Storage node requested to join the ring.");
			StorageNodeMetadataCapsule nodeMetadata = (StorageNodeMetadataCapsule)content;
			LoadBalancer.logger.info("Received this metadata: " + nodeMetadata.toString());
			
			this.loadBalancer.storageNodesMetadata.add(nodeMetadata);
			
//			for (StorageNodeMetadataCapsule capsule : this.loadBalancer.storageNodesMetadata) {
//				
//			}
		}
		
	}

}
