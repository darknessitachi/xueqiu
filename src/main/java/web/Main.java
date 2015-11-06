package web;

import java.io.IOException;
import java.util.Date;

public class Main {

	public static void main(String[] args) throws IOException {
		long start = new Date().getTime();
		
		StockCommand c = new StockCommand();
		c.init();
		c.sendReq();
		c.finish();
		
		long end = new Date().getTime();
		System.out.println("”√ ±£∫"+(end-start)/1000+"√Î");
	}

}
