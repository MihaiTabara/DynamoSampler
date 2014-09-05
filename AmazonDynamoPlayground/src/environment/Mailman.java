/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package environment;

import environment.MailBox;
import environment.TaskCapsule;

/**
 * @author mtabara
 *
 */
public class Mailman extends MailBox {

	/**
	 * @param host
	 * @param port
	 */
	public Mailman(String host, int port) {
		super(host, port);
	}

	/* (non-Javadoc)
	 * @see environment.MailBox#setTaskCapsule(environment.TaskCapsule)
	 */
	@Override
	public void composeMail(TaskCapsule content) {
		this.mailContent = content;
	}

}
