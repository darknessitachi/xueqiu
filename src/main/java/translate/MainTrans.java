package translate;

import java.io.IOException;

import config.Constants;

public class MainTrans {
	
	//�Ƿ��ӡ��־
	public static boolean useLog = false;

	public static void main(String[] args) throws IOException {
		StockInterface sm = new StockInterface();
		sm.useLog = MainTrans.useLog ;
		sm.translate(Constants.ZXG_FILE_NAME);
	}


}
