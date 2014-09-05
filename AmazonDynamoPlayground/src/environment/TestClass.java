package environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

public class TestClass {
	
	public static void main(String[] args) {
		System.out.print("[usage] GET/PUT key [value] \n");
	 
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	 
	    String userName = null;
	
		try {
			String line = null;
			while (!(line = br.readLine()).trim().equals("")) {
				String[] tokens = line.split(" ");
				if (tokens[0].equals("GET")) {
					System.out.println(tokens[1]);
				}
				else if (tokens[0].equals("PUT")) {
					System.out.println(tokens[1] + " " + tokens[2]);
				}
			}
		} catch (IOException ioe) {
			System.exit(1);
		}
	}

}
