/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

import environment.Command;
import environment.Constants;
import environment.Mailman;
import environment.TaskCapsule;
import environment.Command.Type;

/**
 * @author mtabara
 * Base class to interact with the user console
 */
public class Client {

	/**
	 * The port for the running client server
	 */
	public ServerSocket resourceServerSocket;
	/**
	 * Global static locker to be used between client and client server side
	 * to sync up the GET calls
	 */
	public static final Object locker = new Object();
	/**
	 * Global static string to use between client and client server side
	 */
	public static String resultOfGET;
	
	public Client() {
		try {
			this.resourceServerSocket = new ServerSocket(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Where the whole communication with user takes place
	 */
	private void readFromConsole() {
		System.out.print("[usage] GET/PUT key [value] \n");
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    
		try {
			String line = null;
			while (!(line = br.readLine()).trim().equals("")) {
				String[] tokens = line.split(" ");
				if (tokens[0].equals("GET")) {
					String key = tokens[1];
					
					Command command = new Command(Type.GET, key);
					command.setCoordinates(this.resourceServerSocket.getLocalPort(), Constants.LOAD_BALANCER_RUNNING_PORT);
					
					Mailman mailMan = new Mailman(Constants.GENERIC_HOST, Constants.LOAD_BALANCER_RUNNING_PORT);
					mailMan.composeMail(new TaskCapsule(command));
					mailMan.sendMail();
					
					synchronized (Client.locker) {
						try {
							Client.locker.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					System.out.println(Client.resultOfGET);
				}
				else if (tokens[0].equals("PUT")) {
					String key = tokens[1];
					String value = tokens[2];
					
					Command command = new Command(Type.PUT, key, value);
					command.setCoordinates(this.resourceServerSocket.getLocalPort(), Constants.LOAD_BALANCER_RUNNING_PORT);
					
					Mailman mailMan = new Mailman(Constants.GENERIC_HOST, Constants.LOAD_BALANCER_RUNNING_PORT);
					mailMan.composeMail(new TaskCapsule(command));
					mailMan.sendMail();
					
				}
			}
		} catch (IOException ioe) {
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		Runnable clientServerSide = new ClientServerSide(client.resourceServerSocket);
		(new Thread(clientServerSide)).start();
		
		client.readFromConsole();	
	}

}
