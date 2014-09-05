/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */

package environment;

import java.util.UUID;

/**
 * @author mtabara
 * Utility function to generate 99%-uniquely ids for storage nodes
 */
public class Utilities {
	
	/**
	 * @returns new ID for a storage node
	 */
	public static String getRandomString() {
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}

}
