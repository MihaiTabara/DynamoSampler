/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package environment;

import environment.MailBox;
import environment.TaskCapsule;

/**
 * @author mtabara
 * This class inherits the primitives specified in the MailBox
 * and wraps around them in a usable form by all classes, objects 
 * and entities in the project
 */
public class Mailman extends MailBox {

	/**
	 * @param host
	 * @param port
	 */
	public Mailman(String host, int port) {
		super(host, port);
	}

	@Override
	public void composeMail(TaskCapsule content) {
		this.mailContent = content;
	}

}
