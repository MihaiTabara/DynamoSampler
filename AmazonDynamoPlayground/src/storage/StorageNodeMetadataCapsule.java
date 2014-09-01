/**
 * 
 */
package storage;

/**
 * @author mtabara
 * Class to describe how the metadata looks like
 */
public class StorageNodeMetadataCapsule {
		
	/**
	 * Identifier inherited from the storage node
	 */
	private int id;
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
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
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
		return "StorageNodeMetadataCapsule [id=" + id + ", port=" + port
				+ ", position=" + position + "]";
	}
	
}
