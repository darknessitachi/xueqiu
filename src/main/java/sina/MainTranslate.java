package sina;

import java.io.IOException;

import config.Constants;

public class MainTranslate {
	
	//�Ƿ��ӡ��־
	private static boolean useLog = false;

	public static void main(String[] args) throws IOException {
		StockInterface sm = new StockInterface();
		sm.useLog = MainTranslate.useLog ;
		sm.readSource(Constants.ZXG_FILE_NAME);
	}


}
