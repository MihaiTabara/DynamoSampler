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
 * The Load Balancer base class to describe its specification
 * It behaves in the natural way, as a wall for all the incoming
 * request that arrive from the user. It randomly chooses a storage
 * node and forwards the information towards it. The only information
 * that it keeps is the metadata about the nodes, so that is ensures a 
 * transparent traffic-control information - each time a storage node
 * is added/removed from the ring.
 */
public class LoadBalancer {

	/**
	 * the socket where the load balancer listens for connections
	 */
	private static ServerSocket serverSocket;
	/**
	 * The running port - hard-coded in the Constants file
	 */
	private int port = Constants.LOAD_BALANCER_RUNNING_PORT;
	/**
	 * The metadata kept about the storage nodes
	 */
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
	
	/**
	 * Each time the LB receives a package, it randomly shuffles the 
	 * current storage nodes bucket and forwards it to one lucky
	 * winner
	 */
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
