/**
 * 
 */
package environment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mtabara
 *
 */
public class VectorClock {

	private String value;
	List<NodeCounterTuple> clocks = new ArrayList<>();
	
	public VectorClock() {
		super();
	}
	
	public void updateWithNodeName(String nodeName) {
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
	}
	
}
