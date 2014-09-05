/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */

package storage;

import java.io.Serializable;

/**
 * @author mtabara
 * Class to describe how the metadata looks like for a storage node
 */
public class StorageNodeMetadataCapsule implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * The (domestic) name of the storage name
	 */
	private String nodeName;
	/**
	 * Port on which its corresponding Storage Node runs on
	 */
	private int port;
	
	/**
	 * Position handled in the ring 
	 */
	private int position;
	
	/**
	 * @param nodeName
	 * @param port
	 * @param position
	 */
	public StorageNodeMetadataCapsule(String nodeName, int port, int position) {
		this.nodeName = nodeName;
		this.port = port;
		this.position = position;
	}

	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}
	/**
	 * @param nodeName the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "StorageNodeMetadataCapsule [nodeName=" + nodeName + ", port="
				+ port + ", position=" + position + "]";
	}

	
}
