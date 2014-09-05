/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package environment;

import java.io.Serializable;

/**
 * @author mtabara
 * KeyVersionRequest represents the Serializable type of object
 * send between the storage nodes to exchange the Vector Clocks
 * before returning a GET call. It is used by the task coordinator 
 * to ask for current and available replicas.
 */
public class KeyVersionRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * The key for which storage node coordinator asks about
	 */
	private String key;
	/**
	 * Sender's source port
	 */
	private int sourcePort;
	/**
	 * Receiver's port
	 */
	private int destinationPort;
	
	/**
	 * @param key
	 * @param sourcePort
	 * @param destinationPort
	 */
	public KeyVersionRequest(String key, int sourcePort, int destinationPort) {
		this.key = key;
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the sourcePort
	 */
	public int getSourcePort() {
		return sourcePort;
	}
	/**
	 * @param sourcePort the sourcePort to set
	 */
	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}
	/**
	 * @return the destinationPort
	 */
	public int getDestinationPort() {
		return destinationPort;
	}
	/**
	 * @param destinationPort the destinationPort to set
	 */
	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}

	@Override
	public String toString() {
		return "KeyVersionRequest [key=" + key + ", sourcePort=" + sourcePort
				+ ", destinationPort=" + destinationPort + "]";
	}

}
