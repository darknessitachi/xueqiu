import java.io.File;







public class Test {

	public static void main(String[] args) {
		File folder = new File("d:/x/y/z");
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}
	

}
