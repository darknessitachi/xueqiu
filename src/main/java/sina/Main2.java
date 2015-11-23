package sina;

import java.io.IOException;

public class Main2 {
	
	private static String file = "2015-11-20.EBK";

	public static void main(String[] args) throws IOException {
		StockInter sm = new StockInter();
		sm.readSource(file);
	}


}
