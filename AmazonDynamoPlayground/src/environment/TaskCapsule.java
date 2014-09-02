/**
 * 
 */
package environment;

import java.io.Serializable;

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
	
	public Object getContent() {
		return content;
	}
}
