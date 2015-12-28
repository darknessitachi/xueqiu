package app.xueqiu;

import java.io.IOException;

import app.translate.MainTrans;
import config.Constants;

public class MainAddJust {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		MainTrans.translate(Constants.ZXG_FILE_NAME);
		
		StockOpertion oper = new StockOpertion();
		oper.addAllBody();
		
	}

}
