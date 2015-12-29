package func.xueqiu;

import java.io.IOException;

import func.translate.MainTrans;
import config.Constants;

public class MainAddJust {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		MainTrans.translate(Constants.ZXG_FILE_NAME);
		
		StockOpertion oper = new StockOpertion();
		oper.uploadBody();
		
	}

}
