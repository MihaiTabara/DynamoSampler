/**
 * 
 */
package environment;

import java.io.Serializable;

/**
 * @author mtabara
 *
 */
public class NodeCounterTuple implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nodeName;
	private int counter;
	
	/**
	 * @param nodeName
	 * @param counter
	 */
	public NodeCounterTuple(String nodeName, int counter) {
		super();
		this.nodeName = nodeName;
		this.counter = counter;
	}
	
	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}
	/**
	 * @param nodeName the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	/**
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}
	/**
	 * @param counter the counter to set
	 */
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public void incrementCounter() {
		this.counter++;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NodeCounterTuple [nodeName=" + nodeName + ", counter="
				+ counter + "]";
	}
	
	
}
