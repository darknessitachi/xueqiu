package comment;

import java.io.IOException;
import java.util.Date;

import config.Constants;
import comment.common.StockCommand;

public class Main {

	public static void main(String[] args) throws IOException {
		long start = new Date().getTime();
		
		StockCommand c = new StockCommand(Constants.business_sort);
		c.start();
		
		long end = new Date().getTime();
		System.out.println("”√ ±£∫"+(end-start)/1000+"√Î");
	}

}
