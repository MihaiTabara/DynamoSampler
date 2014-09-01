/**
 * 
 */
package loadbalancer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;

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
				TaskCapsule ret = (TaskCapsule)inputStream.readObject();
				LoadBalancer.logger.info(ret.getMessage());
				
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
