package xueqiu;

import java.io.IOException;

import translate.MainTrans;
import translate.StockInterface;
import config.Constants;

public class MainDelAndAdd {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		//先翻译，把翻译的文件写入request_body中
		StockInterface sm = new StockInterface();
		sm.useLog = MainTrans.useLog ;
		sm.translate(Constants.ZXG_FILE_NAME);
		
		StockOpertion oper = new StockOpertion();
		oper.delAll();
		oper.addAll();
		
	}

}
