/**
 * 
 */
package storage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.crypto.NodeSetData;

import environment.Hasher;
import loadbalancer.LoadBalancer;
import loadbalancer.LoadBalancerRunner;

/**
 * @author mtabara
 *
 */
public class StorageNode {

	/**
	 * @param args
	 */
	
	private static int counter = 0;
	private StorageNodeMetadataCapsule metadata;
	private ServerSocket nodeServerSocket;
	
	public static final Logger logger = Logger.getLogger(StorageNode.class.getName());
	
	public StorageNode() {
		try {
			nodeServerSocket = new ServerSocket(0);
			logger.info("Storage node started; awaiting connections on localhost:" + nodeServerSocket.getLocalPort()); 
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		int localId = ++counter;
		int localPosition = 0;
		
		try {
			localPosition = Hasher.getRingPosition("Storage node with id" + localId, true);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.metadata = new StorageNodeMetadataCapsule(localId, nodeServerSocket.getLocalPort(), localPosition);
	}
	
	public static void main(String[] args) throws SecurityException, IOException {
		StorageNode node = new StorageNode();
		
		Handler fh = new FileHandler("%h/storage_node" + node.metadata.getId() + ".log", true);
		logger.addHandler(fh);
		logger.setLevel(Level.INFO);
		logger.info(node.metadata.toString());

//		while (true) {
//			try {
//				Socket clientSocket = node.nodeServerSocket.accept();
//				logger.fine("Somebody tries to connect ...");
//				LoadBalancerRunner actionRunner = new LoadBalancerRunner(clientSocket);
//				actionRunner.start();
//				
//			} catch (IOException e) {
//				logger.log(Level.SEVERE, e.getMessage(), e);
//			}
//		}
	}

}
