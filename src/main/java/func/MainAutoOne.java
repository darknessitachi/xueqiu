package func;

import java.io.IOException;
import java.util.Date;

import util.core.TranslateUtil;
import config.Constants;
import func.common.StockCommand;

/**
 * 只需要把原始文件放入code中，就可以执行该方法了
 * @author Administrator
 *
 */
public class MainAutoOne {
	

	public static void main(String[] args) throws IOException {
		autoOne(Constants.CODE_PATH + "custom" + Constants.ZXG_FILE_NAME);
	}

	public static void autoOne(String fileName) throws IOException {
		//先翻译，把翻译的文件写入request_body中
		TranslateUtil.translate(fileName);
		
		//再执行查询热度方法
		long start = new Date().getTime();
		StockCommand c = new StockCommand(Constants.business_sort);
		c.start();
		long end = new Date().getTime();
		System.out.println("用时："+(end-start)/1000+"秒");
	}

}
