/**
 * 
 */
package storage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import loadbalancer.LoadBalancer;
import environment.Command;
import environment.Constants;
import environment.Mailman;
import environment.TaskCapsule;

/**
 * @author mtabara
 *
 */
public class StorageNodeRunner extends Thread {
	private StorageNode node;
	private Socket communicationSocket;
	
	
	public StorageNodeRunner(StorageNode node, Socket clientSocket) {
		this.node = node;
		this.communicationSocket = clientSocket;
	}


	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			StorageNode.logger.info("I'm a node runner - I'll check on what's this about ...");
			ObjectInputStream inputStream = new ObjectInputStream(communicationSocket.getInputStream());
			
			try {
				TaskCapsule receivedTaskCapsule = (TaskCapsule)inputStream.readObject();
				Object content = receivedTaskCapsule.getContent();
				analyzeContent(content);
				
			} catch (ClassNotFoundException e) {
				StorageNode.logger.log(Level.SEVERE, e.getMessage(), e);
			}
			
			inputStream.close();
			communicationSocket.close();
			
		} catch (IOException e) {
			LoadBalancer.logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}


	private void analyzeContent(Object content) {
		if (content instanceof ArrayList) {
			StorageNode.logger.info("Load balancer sent a broadcast!");
			this.node.allNodes = (ArrayList<StorageNodeMetadataCapsule>)content;
			
			Collections.sort(this.node.allNodes, new Comparator<StorageNodeMetadataCapsule>() {
				public int compare(StorageNodeMetadataCapsule a, StorageNodeMetadataCapsule b) {
					return Integer.valueOf(a.getPosition()).compareTo(Integer.valueOf(b.getPosition()));
				}
			});
			
			StorageNode.logger.info("Sorted out my nodes: " + this.node.allNodes.toString());		
		}
		else if (content instanceof Command) {
			System.out.println("Received a forwarded Command from load balancer/other node");
			System.out.println(((Command)content).getMessage());
		}
	}
	
	
	

}
