package sina;

import java.io.IOException;

public class Main2 {
	
	private static String file = "code.EBK";

	public static void main(String[] args) throws IOException {
		StockInterface sm = new StockInterface();
		sm.readSource(file);
	}


}
