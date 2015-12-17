package app.translate;

import java.io.IOException;

public class MainTrans {
	
	public static boolean useLog = false;

	/**
	 * 对外接口
	 * @param fileName
	 * @throws IOException
	 */
	public static void translate(String fileName) throws IOException {
		StockInterface sm = new StockInterface();
		sm.useLog = MainTrans.useLog ;
		sm.translate(fileName);
	}

}
