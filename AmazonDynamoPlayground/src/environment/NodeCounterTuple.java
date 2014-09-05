/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */

package environment;

import java.io.Serializable;

/**
 * @author mtabara
 * The (node, counter) pair basic class that describes the 
 * atomic element in a Vector Clock used for versioning
 */
public class NodeCounterTuple implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * node identifier 
	 */
	private String nodeName;
	/**
	 * counter used
	 */
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

	@Override
	public String toString() {
		return "NodeCounterTuple [nodeName=" + nodeName + ", counter="
				+ counter + "]";
	}
	
	
}
