/**
 * 
 */
package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import environment.Command;
import environment.Constants;
import environment.Mailman;
import environment.TaskCapsule;

/**
 * @author mtabara
 *
 */
public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client();
		try {
			System.out.println("Send greetings to load balancer.");
			
			Mailman mailMan = new Mailman(Constants.GENERIC_HOST, Constants.LOAD_BALANCER_RUNNING_PORT);
			mailMan.composeMail(new TaskCapsule(new Command("Greetings from client!")));
			mailMan.sendMail();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
