/**
 * 
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

	private int serverRunningPort;
	public static final Object locker = new Object();
	public static String resultOfGET;
	
	public int getServerRunningPort() {
		return serverRunningPort;
	}
	
	public void setServerRunningPort(int port) {
		serverRunningPort = port;
	}
	
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
					command.setCoordinates(serverRunningPort, Constants.LOAD_BALANCER_RUNNING_PORT);
					
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
				}
				else if (tokens[0].equals("PUT")) {
					String key = tokens[1];
					String value = tokens[2];
					
					Command command = new Command(Type.PUT, key, value);
					command.setCoordinates(serverRunningPort, Constants.LOAD_BALANCER_RUNNING_PORT);
					
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
		ClientServerSide clientServerSide = new ClientServerSide();
		(new Thread(clientServerSide)).start();
		client.setServerRunningPort(clientServerSide.getRunningPort());
		
		client.readFromConsole();	
	}

}
