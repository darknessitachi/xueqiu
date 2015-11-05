package web;

import java.io.IOException;
import java.util.Date;

public class StockMain {

	public static void main(String[] args) throws IOException {
		long start = new Date().getTime();
		
		StockCommand c = new StockCommand();
		c.initReq();
		c.sendReq();
		//合计
		c.combine();
		c.printReq();
		
		long end = new Date().getTime();
		System.out.println("用时："+(end-start)/1000+"秒");
	}

}
