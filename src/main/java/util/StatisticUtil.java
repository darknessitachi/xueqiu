package util;

import java.io.IOException;

import bean.Req;
import core.inter.StockCommand;

public class StatisticUtil {
	

	public static void statistic(Req req) throws IOException {
		StockCommand c = new StockCommand(req);
		c.start();
	}

}
