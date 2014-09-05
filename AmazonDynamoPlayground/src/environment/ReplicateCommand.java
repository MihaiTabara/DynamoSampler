/**
 * 
 */
package environment;

import java.io.Serializable;

/**
 * @author mtabara
 *
 */
public class ReplicateCommand implements Serializable {

	private static final long serialVersionUID = 1L;
	private int keyRingPosition;
	private VectorClock replicaVC;
	
	public ReplicateCommand(int keyRingPosition, VectorClock replica) {
		this.keyRingPosition = keyRingPosition;
		this.replicaVC = replica;
	}

	/**
	 * @return the keyRingPosition
	 */
	public int getKeyRingPosition() {
		return keyRingPosition;
	}

	/**
	 * @param keyRingPosition the keyRingPosition to set
	 */
	public void setKeyRingPosition(int keyRingPosition) {
		this.keyRingPosition = keyRingPosition;
	}

	/**
	 * @return the replica
	 */
	public VectorClock getReplicaVC() {
		return replicaVC;
	}

	/**
	 * @param replica the replica to set
	 */
	public void setReplicaVC(VectorClock replica) {
		this.replicaVC = replica;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReplicateCommand [keyRingPosition=" + keyRingPosition
				+ ", replicaVC=" + replicaVC + "]";
	}

	
	
}
