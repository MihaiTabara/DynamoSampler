/**
 * Mihai Tabara
 * tabara.mihai@gmail.com
 */
package environment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mtabara
 * Functional utils class for hashing purposes
 */
public class Hasher {
	/*
	 * Cache to store all values generated for all storage nodes such
	 * that no two nodes fall into the same position in the ring
	 */
	private static Set<Integer> generatedValues = new HashSet<>();
	
	/*
	 * Method to return a position in range [0,1023] for any key given
	 * Algorithm is as follows, given any key:
	 * => run sha1() digest 
	 * => get last eight digits (thus < 0xFFFFFFFF),
	 * => convert to Long
	 * => AND with 1023 
	 */
	public static int getRingPosition(String key, boolean isStorageNode) throws Exception {
		Integer presumptiveRingPosition = new Integer(0);
		presumptiveRingPosition = ringPosition(key);	
		
		if (isStorageNode == false) {
			return presumptiveRingPosition.intValue();
		}
		else {
			
			String currentString = key;
			Integer currentRingPosition = presumptiveRingPosition;
			
			while (generatedValues.contains(currentRingPosition)) {
				String nextString = shiftStringPosition(currentString);
				Integer nextRingPosition = ringPosition(nextString);
				
				currentString = nextString;
				currentRingPosition = nextRingPosition;
			}
			
			generatedValues.add(currentRingPosition);
			return currentRingPosition.intValue();
		}
		
	}
	
	private static String shiftStringPosition(String input) {
		return new String(input.charAt(input.length() - 1) + input.substring(0, input.length() - 1));
	}
	
	
	private static Integer ringPosition(String key) throws Exception {
		String sha1Digest = sha1(key);
		String lastEightDigitsHex = lastEightDigits(sha1Digest);
		Long longValue = longFromHex(lastEightDigitsHex);
		Integer ringPosition = new Integer(lastTenBits(longValue));
		
		return ringPosition;
	}
	
	private static int lastTenBits(Long input) {
		Long result = (input & 1023L);
		return result.intValue();
	}
	
	private static Long longFromHex(String hexString) {
		return Long.parseLong(hexString, 16);
	}
	
	private static String lastEightDigits(String input) throws Exception {
		if (input.length() < 9)
			throw new Exception("Valid sha1 must be given");
		return input.substring(input.length() - 8);
	}
	
	private static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        return sb.toString();
    }
}
