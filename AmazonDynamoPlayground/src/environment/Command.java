/**
 * 
 */
package environment;

import java.io.Serializable;

/**
 * @author mtabara
 *
 */
public class Command implements Serializable {

	public static enum Type {
		GET,
		PUT,
	}
	
	private Type action;
	private String key;
	private String value;
	private int sourcePort;
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


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
