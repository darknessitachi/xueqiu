package translate;

import java.io.IOException;

import config.Constants;

public class MainTrans {
	
	//是否打印日志
	public static boolean useLog = false;

	public static void main(String[] args) throws IOException {
		StockInterface sm = new StockInterface();
		sm.useLog = MainTrans.useLog ;
		sm.translate(Constants.ZXG_FILE_NAME);
	}


}
