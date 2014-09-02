/**
 * 
 */
package environment;

import java.io.Serializable;
import java.util.List;

import storage.StorageNodeMetadataCapsule;

/**
 * @author mtabara
 *
 */
public class TaskCapsule implements Serializable {
	
	private static final long serialVersionUID = 1L;	
	private Object content;

	public TaskCapsule(StorageNodeMetadataCapsule metadata) {
		this.content = metadata;
	}
	
	public TaskCapsule(List<StorageNodeMetadataCapsule> storageNodesMetadata) {
		this.content = storageNodesMetadata;
	}

	public Object getContent() {
		return content;
	}
}
