package environment;

import java.util.HashMap;
import java.util.HashSet;

public class TestClass {

	private HashMap<Integer, String> h = new HashMap<>();
	
	public void test() {
		HashSet<String> h = new HashSet<>();
		h.add(new String(""));
		h.add(new String(""));
		h.add(new String("x"));
		
		System.out.println(h.size());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestClass t = new TestClass();
		t.test();
	}

}
