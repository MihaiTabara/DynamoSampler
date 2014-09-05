/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package environment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mtabara
 * The Vector Clock class encapsulates all the information a
 * storage node keeps about a specific key
 */
public class VectorClock implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * The current value for the key
	 */
	private String value;
	/**
	 * The vector of the (nodename, counter) pairs
	 */
	private List<NodeCounterTuple> clocks = new ArrayList<>();
	
	public VectorClock() {
		super();
		this.value = "";
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the clocks
	 */
	public List<NodeCounterTuple> getClocks() {
		return clocks;
	}

	/**
	 * @param clocks the clocks to set
	 */
	public void setClocks(List<NodeCounterTuple> clocks) {
		this.clocks = clocks;
	}
	
	public void update(String nodeName, String value) {
		boolean alreadyStamped = false;
		for (NodeCounterTuple clock : clocks ) {
			if (clock.getNodeName().equals(nodeName)) {
				alreadyStamped = true;
				clock.incrementCounter();
			}
		}
		if (!alreadyStamped) {
			clocks.add(new NodeCounterTuple(nodeName, 1));
		}
		
		this.value = value;
	}

	@Override
	public String toString() {
		return "VectorClock [value=" + value + ", clocks=" + clocks + "]";
	}
	
}
