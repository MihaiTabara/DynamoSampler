/**
 * 
 */
package environment;

import java.util.UUID;

/**
 * @author mtabara
 *
 */
public class Utilities {
	
	public static String getRandomString() {
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}

}
