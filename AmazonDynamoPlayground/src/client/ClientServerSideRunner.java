/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;

import environment.TaskCapsule;

/**
 * @author mtabara
 * The client server side thread - each for each connection
 * the server accepts on its port.
 */
public class ClientServerSideRunner extends Thread {

	/**
	 * The communication socket between the server and 
	 * any of the communicating storage nodes
	 */
	private Socket communicationSocket;
	
	public ClientServerSideRunner(Socket clientSocket) {
		this.communicationSocket = clientSocket;
	}
	
	@Override
	public void run() {
		try {
			ObjectInputStream inputStream = new ObjectInputStream(communicationSocket.getInputStream());
			
			try {
				TaskCapsule receivedTaskCapsule = (TaskCapsule)inputStream.readObject();
				Object content = receivedTaskCapsule.getContent();
				analyzeContent(content);
				
			} catch (ClassNotFoundException e) {
				ClientServerSide.logger.log(Level.SEVERE, e.getMessage(), e);
			}
			
			inputStream.close();
			communicationSocket.close();
			
		} catch (IOException e) {
			ClientServerSide.logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Method to analyze the content. In this case, fairly simple,
	 * the server accepts only GET answers from storage nodes, thus
	 * only strings
	 */
	private void analyzeContent(Object content) {
		if (content instanceof String) {
			String ret = (String)content;
			synchronized (Client.locker) {
				Client.resultOfGET = ret;
				Client.locker.notify();
			}
		}
		else
			throw new IllegalArgumentException("I do not understand the content I received!");
	}	

}
