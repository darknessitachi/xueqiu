package sina;

import java.io.IOException;

import config.Constants;

public class MainTranslate {
	
	//是否打印日志
	private static boolean useLog = false;

	public static void main(String[] args) throws IOException {
		StockInterface sm = new StockInterface();
		sm.useLog = MainTranslate.useLog ;
		sm.readSource(Constants.ZXG_FILE_NAME);
	}


}
