/**
 * 
 */
package storage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import environment.Constants;
import environment.Hasher;
import environment.Mailman;
import environment.TaskCapsule;

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
	public List<StorageNodeMetadataCapsule> allNodes;
	
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
		
		System.out.println("Intru aici!");
		this.metadata = new StorageNodeMetadataCapsule(localId, nodeServerSocket.getLocalPort(), localPosition);
		System.out.println("==>" + this.metadata.toString());
	}
	
	public static void main(String[] args) throws SecurityException, IOException {
		List<StorageNode> nodes = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			StorageNode node = new StorageNode();
			nodes.add(node);
		}
		
		System.out.println("XXX: " + nodes.toString());
		
		for (StorageNode node : nodes) {
			System.out.println("Intru in for aici!");
			Handler fh = new FileHandler("%h/storage_node" + node.metadata.getId() + ".log", true);
			logger.addHandler(fh);
			logger.setLevel(Level.INFO);
			
			System.out.println("Send my metadata to load balancer.");
			Mailman mailMan = new Mailman(Constants.GENERIC_HOST, Constants.LOAD_BALANCER_RUNNING_PORT);
			mailMan.composeMail(new TaskCapsule(node.metadata));
			mailMan.sendMail();
	
			logger.info("Start listening on port for connections.");
			while (true) {
				try {
					Socket clientSocket = node.nodeServerSocket.accept();
					logger.fine("Somebody is sending me something");
					StorageNodeRunner actionRunner = new StorageNodeRunner(node, clientSocket);
					actionRunner.start();
					
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}

}
