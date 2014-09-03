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
import environment.Utilities;

/**
 * @author mtabara
 *
 */
public class StorageNode {

	/**
	 * @param args
	 */
	
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

		int localPosition = 0;
		String localName = Utilities.getRandomString();
		
		try {
			localPosition = Hasher.getRingPosition(localName, true);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	
		this.metadata = new StorageNodeMetadataCapsule(localName, nodeServerSocket.getLocalPort(), localPosition);
		logger.info("==>" + this.metadata.toString());
	}
	
	/**
	 * @return the metadata
	 */
	public StorageNodeMetadataCapsule getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(StorageNodeMetadataCapsule metadata) {
		this.metadata = metadata;
	}

	public List<StorageNodeMetadataCapsule> getPreferenceListForAKey(String key) throws Exception {
		List<StorageNodeMetadataCapsule> preferenceList = new ArrayList<>();
		int keyRingPosition = Hasher.getRingPosition(key, false);
		
		int counter = 0;
		int foundCoordinatorPos = -1;
		for (StorageNodeMetadataCapsule s : this.allNodes) {
			if (s.getPosition() >= keyRingPosition) {
				foundCoordinatorPos = counter;
				break;
			}
			counter++;
		}
		if (foundCoordinatorPos == -1) {
			foundCoordinatorPos = 0;
		}
			
		preferenceList.add(this.allNodes.get(foundCoordinatorPos));
		for (int i = 1; i < Constants.DYNAMO_N; i++) {
			int nexNodeIndex = ((foundCoordinatorPos + i) % this.allNodes.size());
			preferenceList.add(this.allNodes.get(nexNodeIndex));
		}
		
		logger.info("The key " + key + " has ring position" + keyRingPosition + " with preflist " + preferenceList.toString());
		return preferenceList;
	}
	
	public static void main(String[] args) throws SecurityException, IOException {
		StorageNode node = new StorageNode();
		logger.info("Data about myself(storage node): " + node.toString());
		
		Handler fh = new FileHandler("%h/storage_node" + node.metadata.getNodeName() + ".log", true);
		logger.addHandler(fh);
		logger.setLevel(Level.INFO);
		
		logger.info("Send my metadata to load balancer.");
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
