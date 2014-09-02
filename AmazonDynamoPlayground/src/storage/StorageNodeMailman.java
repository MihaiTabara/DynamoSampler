/**
 * 
 */
package storage;

import environment.MailBox;
import environment.TaskCapsule;

/**
 * @author mtabara
 *
 */
public class StorageNodeMailman extends MailBox {

	/**
	 * @param host
	 * @param port
	 */
	public StorageNodeMailman(String host, int port) {
		super(host, port);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see environment.MailBox#setTaskCapsule(environment.TaskCapsule)
	 */
	@Override
	public void composeMail(TaskCapsule content) {
		this.mailContent = content;
	}

}
