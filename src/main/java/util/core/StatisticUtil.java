package util.core;

import java.io.IOException;

import func.domain.Req;
import func.inter.StockCommand;

public class StatisticUtil {
	

	public static void statistic(Req req) throws IOException {
		
		StockCommand c = new StockCommand(req);
		c.start();
	}

}
