package sina;

import java.io.IOException;

import config.Constants;

public class MainTranslate {
	
	public static void main(String[] args) throws IOException {
		StockInterface sm = new StockInterface();
		sm.readSource(Constants.ZXG_FILE_NAME);
	}


}
