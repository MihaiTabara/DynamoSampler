/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */

package loadbalancer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import storage.StorageNodeMetadataCapsule;
import environment.Constants;

/**
 * @author mtabara
 *
 */
public class LoadBalancer {

	private static ServerSocket serverSocket;
	private int port = Constants.LOAD_BALANCER_RUNNING_PORT;
	public List<StorageNodeMetadataCapsule> storageNodesMetadata = new ArrayList<>();
	
	public static final Logger logger = Logger.getLogger(LoadBalancer.class.getName());
	
	public LoadBalancer() {
		try {
			serverSocket = new ServerSocket(this.port);
			logger.info("Load balancer awaiting connections on localhost:5000");
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public StorageNodeMetadataCapsule getRandomStorageNode() {
		Collections.shuffle(this.storageNodesMetadata);
		return this.storageNodesMetadata.get(0);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public static void main(String[] args) throws SecurityException, IOException {
		Handler fh = new FileHandler("%h/load_balancer.log", true);
		logger.addHandler(fh);
		logger.setLevel(Level.INFO);
		
		LoadBalancer loadBalancer = new LoadBalancer();
		
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				logger.fine("Somebody tries to connect ...");
				LoadBalancerRunner actionRunner = new LoadBalancerRunner(loadBalancer, clientSocket);
				actionRunner.start();
				
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
	}

}
