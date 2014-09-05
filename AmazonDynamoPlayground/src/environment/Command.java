/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package environment;

import java.io.Serializable;

/**
 * @author mtabara
 * The Command Serializable object is the one that ensures
 * the transportation between the user requests and the
 * storage nodes abilities.
 */
public class Command implements Serializable {

	/**
	 * Enum type for the two operations available
	 *
	 */
	public static enum Type {
		GET,
		PUT,
	}
	
	/**
	 * type of the action to be sent via Command object
	 */
	private Type action;
	/**
	 * the unique key associated with the search
	 */
	private String key;
	/**
	 * the new value assigned with the ops
	 */
	private String value;
	/**
	 * source port for the sender's address
	 */
	private int sourcePort;
	/**
	 * destination port for the receiver's address
	 */
	private int destinationPort;
	
	private static final long serialVersionUID = 1L;
	
	public Command(Type type, String argument) {
		if (type != Type.GET) {
			throw new IllegalArgumentException("Only GET command can have an argument!"); 
		}
		action = type;
		key = argument;
	}
	
	public Command(Type type, String firstArgment, String secondArgument) {
		if (type != Type.PUT) {
			throw new IllegalArgumentException("Only PUT command can have an argument!"); 
		}
		action = type;
		key = firstArgment;
		value = secondArgument;
	}
	
	public Type getType() {
		return action;
	}
	
	public String getkey() {
		if (action != Type.GET && action != Type.PUT) {
			throw new IllegalStateException("Illegal action");
		}
		return key;
	}
	
	public String getValue() {
		if (action != Type.PUT) {
			throw new IllegalStateException("Action has less than two arguments");
		}
		return value;
	}

	/**
	 * @return the sourcePort
	 */
	public int getSourcePort() {
		return sourcePort;
	}

	/**
	 * @return the destinationPort
	 */
	public int getDestinationPort() {
		return destinationPort;
	}
	
	/**
	 * @param sourcePort the sourcePort to set
	 */
	public void setCoordinates(int sourcePort, int destinationPort) {
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
	}


	@Override
	public String toString() {
		String ret = action.toString();
		switch (action) {
		case GET:
			ret += "(" + key.toString() + ")";
			break;
		case PUT:
			ret += "(" + key.toString() + ", " + value + ")";
			break;
		}
		ret += "from " + sourcePort + " to " + destinationPort;
		return ret;
	}
}
