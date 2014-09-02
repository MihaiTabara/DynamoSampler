/**
 * 
 */
package storage;

import java.io.Serializable;

/**
 * @author mtabara
 * Class to describe how the metadata looks like
 */
public class StorageNodeMetadataCapsule implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * Identifier inherited from the storage node
	 */
	private int id;
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
	 * @param id
	 * @param port
	 * @param position
	 */
	public StorageNodeMetadataCapsule(int id, int port, int position) {
		this.id = id;
		this.port = port;
		this.position = position;
		this.nodeName = "Storage node " + id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
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
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StorageNodeMetadataCapsule [id=" + id + ", nodeName="
				+ nodeName + ", port=" + port + ", position=" + position + "]";
	}
	
}
