/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */

package environment;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author mtabara
 * Abstract class to encapsulate the mailing mechanism. 
 * It specifies the destination socket identified by the
 * host and port but also the mail content. The class 
 * is used in this way to separate the actual transmission
 * job from the other ongoing implied tasks along the way
 */
public abstract class MailBox {

	/**
	 * Host of the destination
	 */
	protected String host;
	/**
	 * Running port of the destination
	 */
	protected int port;
	/**
	 * The message content to be send
	 */
	protected TaskCapsule mailContent;
	
	abstract public void composeMail(TaskCapsule content);
	
	protected MailBox(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * The method that opens the connection to the destination
	 * socket, writes content and closes the connection afterwards
	 * @throws UnknownHostException
	 * @throws IOException
	 */
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
