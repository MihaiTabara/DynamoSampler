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
import environment.Command.Type;

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
			
			System.out.println("Sending my first GET key over the network!");
			
			Command command = new Command(Type.GET, "firstKeyToSend");
			command.setCoordinates(0, Constants.LOAD_BALANCER_RUNNING_PORT);
			
			Mailman mailMan = new Mailman(Constants.GENERIC_HOST, Constants.LOAD_BALANCER_RUNNING_PORT);
			mailMan.composeMail(new TaskCapsule(command));
			mailMan.sendMail();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
