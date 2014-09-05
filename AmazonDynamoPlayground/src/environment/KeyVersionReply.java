/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package environment;

import java.io.Serializable;

/**
 * @author mtabara
 * KeyVersionReply represents the Serializable type of object
 * send between the storage nodes to exchange the Vector Clocks
 * before returning a GET call.
 */
public class KeyVersionReply implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * The current Vector Clock to be exchanged
	 */
	private VectorClock reply;
	/**
	 * Source port of the sender
	 */
	private int sourcePort;
	/**
	 * Destination port of the receiver
	 */
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

	@Override
	public String toString() {
		return "KeyVersionReply [reply=" + reply.toString() + ", sourcePort=" + sourcePort
				+ ", destinationPort=" + destinationPort + "]";
	}
	
}
