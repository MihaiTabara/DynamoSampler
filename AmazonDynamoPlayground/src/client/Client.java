/**
 * 
 */
package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
		Socket clientSocket = null;
		ObjectOutputStream output = null;
		try {
			clientSocket = new Socket("localhost", 5000);
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			output.writeObject(new TaskCapsule(new String("Message test from client")));
			output.close();
			clientSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
