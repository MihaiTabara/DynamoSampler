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
		
	public static final Logger logger = Logger.getLogger(ClientServerSide.class.getName());
	
	public ClientServerSide(ServerSocket s) {
		this.serverSocket = s;
	}
	
	@Override
	public void run() {
		logger.info("Client server side waiting connections on " + serverSocket.getLocalPort() + "port ...");
		
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
