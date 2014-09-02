/**
 * 
 */
package environment;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author mtabara
 *
 */
public abstract class MailBox {

	protected String host;
	protected int port;
	protected TaskCapsule mailContent;
	
	abstract public void composeMail(TaskCapsule content);
	
	protected MailBox(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void sendMail() throws UnknownHostException, IOException {
		Socket destinationSocket = openDestinationSocket();
		ObjectOutputStream destinationBlackboard = getDestinationBlackboard(destinationSocket);
		
		destinationBlackboard.writeObject(mailContent);
		
		wipeDestinationBlackboard(destinationBlackboard);
		closeDestinationSocket(destinationSocket);
	}
	
	private Socket openDestinationSocket() throws UnknownHostException, IOException {
		Socket destinationSocket = new Socket(host, port);
		return destinationSocket;
	}
	
	private ObjectOutputStream getDestinationBlackboard(Socket destinationSocket) throws IOException {
		ObjectOutputStream destinationBlackboard = new ObjectOutputStream(destinationSocket.getOutputStream());
		return destinationBlackboard;
	}
	
	private void wipeDestinationBlackboard(ObjectOutputStream destinationBlackboard) throws IOException {
		destinationBlackboard.close();
	}
	
	private void closeDestinationSocket(Socket destinationSocket) throws IOException {
		destinationSocket.close();
	}
}
