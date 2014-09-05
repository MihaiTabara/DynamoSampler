/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package environment;

import java.io.Serializable;

/**
 * @author mtabara
 *
 */
public class KeyVersionRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String key;
	private int sourcePort;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KeyVersionRequest [key=" + key + ", sourcePort=" + sourcePort
				+ ", destinationPort=" + destinationPort + "]";
	}

}
