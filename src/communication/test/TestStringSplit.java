package communication.test;

public class TestStringSplit {
	
	public static void main(String[] args) {
		String test = "224.0.0.1";
		
		String[] parts = test.split("\\.");
		
		System.out.println(parts[0]);
	}

}
