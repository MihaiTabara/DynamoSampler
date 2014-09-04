/**
 * 
 */
package environment;

import java.io.Serializable;

/**
 * @author mtabara
 *
 */
public class KeyVersionReply implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private VectorClock reply;
	private int sourcePort;
	private int destinationPort;
	
	/**
	 * @param key
	 * @param sourcePort
	 * @param destinationPort
	 */
	public KeyVersionReply(VectorClock reply2, int sourcePort, int destinationPort) {
		this.reply = reply2;
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
	}

	/**
	 * @return the reply
	 */
	public VectorClock getReply() {
		return reply;
	}

	/**
	 * @param reply the reply to set
	 */
	public void setReply(VectorClock reply) {
		this.reply = reply;
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
		return "KeyVersionReply [reply=" + reply.toString() + ", sourcePort=" + sourcePort
				+ ", destinationPort=" + destinationPort + "]";
	}
	
}
