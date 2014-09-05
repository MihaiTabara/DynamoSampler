/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
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
 * The main class that describes the behavior and design of a Storage node.
 * The storage nodes communicate between themselves to ensure data replication, 
 * versioning, failure safety, etc.
 */
public class StorageNode {

	/**
	 * @param args
	 */
	
	/**
	 * All the metadata that is kept about a storage node
	 */
	private StorageNodeMetadataCapsule metadata;
	/**
	 * The socket that ensures communication
	 */
	private ServerSocket nodeServerSocket;
	/**
	 * All metadata about the other storage nodes. This attribute
	 * is refreshed by the load balancer each time new nodes arrive
	 * in the bucket
	 */
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

	/**
	 * Based on the key, its key ring position is computed and all the
	 * corresponding storage nodes associated within the ring are returned.
	 * @param key
	 * @return
	 * @throws Exception
	 */
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
	
	/**
	 * Out of the prefernce list, it returns the head of it
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public StorageNodeMetadataCapsule getKeyCoordinator(String key) throws Exception {
		List <StorageNodeMetadataCapsule> prefList = this.getPreferenceListForAKey(key);
		return prefList.get(0);
	}

	/**
	 * Method to test if the current storage node lies in the 
	 * preference list of a specific key
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public boolean patternityTest(String key) throws Exception {
		List <StorageNodeMetadataCapsule> prefList = this.getPreferenceListForAKey(key);
		
		for (StorageNodeMetadataCapsule s : prefList) {
			if (s.getNodeName().equals(this.getMetadata().getNodeName())) {
				return true;
			}
		}
		
		return false;
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
