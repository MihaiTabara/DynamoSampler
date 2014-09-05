/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import environment.Constants;


/**
 * @author mtabara
 * The Client server side that runs in a separate thread so that 
 * the server is open before accepting any answeres from the storage
 * nodes
 */
public class ClientServerSide implements Runnable {

	/**
	 * The socket in which the client server side accepts connections
	 */
	private ServerSocket serverSocket;
	
	/**
	 * The client server side running port - used to be dynamically 
	 * generated, now it lies in the Constants file
	 */
	private int clientServerSideRunningPort;
	
	public static final Logger logger = Logger.getLogger(ClientServerSide.class.getName());
	
	
	public int getRunningPort() {
		return clientServerSideRunningPort;
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(Constants.CLIENT_RUNNING_PORT);
			clientServerSideRunningPort = serverSocket.getLocalPort();
			logger.info("Client server side waiting connections on " + clientServerSideRunningPort + "port ...");
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				logger.fine("Somebody tries to connect ...");
				ClientServerSideRunner clientServerSideRunner = new ClientServerSideRunner(clientSocket);
				clientServerSideRunner.start();
				
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
	}
	
}
