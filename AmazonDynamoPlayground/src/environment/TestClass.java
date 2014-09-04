package environment;

import java.util.HashMap;

public class TestClass {

	private HashMap<Integer, String> h = new HashMap<>();
	
	public void test() {
		Integer x = new Integer(5);
		h.put(x, "cinci");
		
		System.out.println(h.containsKey(x));
		
		Integer y = new Integer(6);
		System.out.println(h.containsKey(y));		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestClass t = new TestClass();
		t.test();
	}

}
