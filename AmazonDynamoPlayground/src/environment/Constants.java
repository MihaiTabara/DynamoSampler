/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package environment;

/**
* @author mtabara
* The constants class used across the project by all entities
*/
public final class Constants {
	
	/**
	 * Private Constructor of the constants class
	 */
	private Constants() {
		
	}
	
	/**
	 * Size of preference list for any key
	 */
	public static final int DYNAMO_N = 3;

	/**
	 * Minimum no. of reads to participate in a successful read operation
	 */
	public static final int DYNAMO_R = 2;
	
	/**
	 * Minimum no. of writes to participate in a successful write operation
	 */
	public static final int DYNAMO_W = 2;
	
	/**
	 * The port where load balancer runs
	 */
	public static final int LOAD_BALANCER_RUNNING_PORT = 5000;
	
	/**
	 * The port where load balancer runs
	 */
	public static final int CLIENT_RUNNING_PORT = 5001;
	
	/**
	 * The generic host to use - localhost
	 */
	public static final String GENERIC_HOST = "localhost";
	
}
