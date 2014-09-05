/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */

package environment;

import java.io.Serializable;
import java.util.List;

import storage.StorageNodeMetadataCapsule;

/**
 * @author mtabara
 * The generic capsule exchanged between all the threads.
 * The way it was wrapped around all the content makes it 
 * easier and transparent for all the entities in the project
 * It hides out all the details about the actual type of object
 * that is being transmitted.
 */
public class TaskCapsule implements Serializable {
	
	private static final long serialVersionUID = 1L;	
	/**
	 * The content that's being transmitted
	 */
	private Object content;

	public TaskCapsule(StorageNodeMetadataCapsule metadata) {
		this.content = metadata;
	}
	
	public TaskCapsule(List<StorageNodeMetadataCapsule> storageNodesMetadata) {
		this.content = storageNodesMetadata;
	}

	public TaskCapsule(Command command) {
		this.content = command;
	}

	public TaskCapsule(KeyVersionRequest keyVersionRequest) {
		this.content = keyVersionRequest;
	}

	public TaskCapsule(KeyVersionReply keyVersionReply) {
		this.content = keyVersionReply;
	}

	public TaskCapsule(ReplicateCommand replica) {
		this.content = replica;
	}

	public TaskCapsule(String ret) {
		this.content = ret;
	}

	public Object getContent() {
		return content;
	}
}
