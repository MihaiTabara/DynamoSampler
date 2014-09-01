/**
 * 
 */
package environment;

import java.io.Serializable;

/**
 * @author mtabara
 *
 */
public class TaskCapsule implements Serializable {

	/**
	 * @param message
	 */
	public TaskCapsule(String message) {
		super();
		setMessage(message);
	}

	private static final long serialVersionUID = 1L;

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
