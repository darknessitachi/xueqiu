package util.core;

import java.io.IOException;
import java.util.Date;

import func.domain.Req;
import func.inter.StockCommand;

public class StatisticUtil {
	

	public static void statistic(Req req) throws IOException {
		//再执行查询热度方法
		long start = new Date().getTime();
		StockCommand c = new StockCommand(req);
		c.start();
		long end = new Date().getTime();
		System.out.println("用时："+(end-start)/1000+"秒");
	}

}
