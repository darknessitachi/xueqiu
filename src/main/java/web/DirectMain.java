package web;

import java.io.IOException;
import java.util.Date;

import config.Constants;
import web.common.StockCommand;

public class DirectMain {

	public static void main(String[] args) throws IOException {
		long start = new Date().getTime();
		
		StockCommand c = new StockCommand(Constants.business_direct);
		c.start();
		
		long end = new Date().getTime();
		System.out.println("��ʱ��"+(end-start)/1000+"��");
	}

}
