package app.translate;

import java.io.IOException;

import config.Constants;

public class MainTrans {
	
	public static boolean useLog = false;

	public static void main(String[] args) throws IOException {
		
		translate(Constants.ZXG_FILE_NAME);
	}

	public static void translate(String fileName) throws IOException {
		StockInterface sm = new StockInterface();
		sm.useLog = MainTrans.useLog ;
		sm.translate(fileName);
	}


}
