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
	
	public LoadBalancerRunner(Socket clientSocket) {
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
				if (content instanceof StorageNodeMetadataCapsule) {
					System.out.println("Victory, transfer succeeded!");
					StorageNodeMetadataCapsule test = (StorageNodeMetadataCapsule)content;
					System.out.println("Afisez " + test.toString());
				}
				
			} catch (ClassNotFoundException e) {
				LoadBalancer.logger.log(Level.SEVERE, e.getMessage(), e);
			}
			
			inputStream.close();
			communicationSocket.close();
			
		} catch (IOException e) {
			LoadBalancer.logger.log(Level.SEVERE, e.getMessage(), e);
		}
			
	}

}
