/**
 * 
 */
package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author mtabara
 *
 */
public class ClientServerSide implements Runnable {

	private ServerSocket serverSocket;
	private int clientServerSideRunningPort;
	
	public static final Logger logger = Logger.getLogger(ClientServerSide.class.getName());
	
	
	public int getRunningPort() {
		return clientServerSideRunningPort;
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(0);
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
