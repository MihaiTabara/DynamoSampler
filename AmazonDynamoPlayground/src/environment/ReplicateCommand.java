/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */

package environment;

import java.io.Serializable;

/**
 * @author mtabara
 * The Replicate Command is send over by the coordinator
 * of a job after a successfully PUT request. This way,
 * it ensures replication in the N-1 other nodes from the
 * prefernce list
 */
public class ReplicateCommand implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * The (integer) key ring position - where the key 
	 * is mapped in the consistent hashing ring
	 */
	private int keyRingPosition;
	/**
	 * The Replication Vector Clock that is being exchanged
	 */
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

	@Override
	public String toString() {
		return "ReplicateCommand [keyRingPosition=" + keyRingPosition
				+ ", replicaVC=" + replicaVC + "]";
	}

	
	
}
