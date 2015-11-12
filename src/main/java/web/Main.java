package web;

import java.io.IOException;
import java.util.Date;

import web.sort.StockCommand;

public class Main {

	public static void main(String[] args) throws IOException {
		long start = new Date().getTime();
		
		StockCommand c = new StockCommand();
		c.init();
		c.send();
		c.finish();
		
		long end = new Date().getTime();
		System.out.println("��ʱ��"+(end-start)/1000+"��");
	}

}
