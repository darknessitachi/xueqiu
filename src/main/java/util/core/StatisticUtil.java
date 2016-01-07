package util.core;

import java.io.IOException;
import java.util.Date;

import func.inter.StockCommand;

public class StatisticUtil {
	

	public static void statistic(String absolutePath) throws IOException {
		//先翻译，把翻译的文件写入request_body中
		TranslateUtil.translate(absolutePath);
		
		//再执行查询热度方法
		long start = new Date().getTime();
		StockCommand c = new StockCommand();
		c.start();
		long end = new Date().getTime();
		System.out.println("用时："+(end-start)/1000+"秒");
	}

}
