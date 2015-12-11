import config.Constants;




public class Test {

	public static void main(String[] args) {
		
		
		System.out.println(Constants.class.getClassLoader().getResource("").getPath());
	}

}
