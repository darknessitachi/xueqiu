package util.core;

import java.io.IOException;

import core.domain.Req;
import core.inter.StockCommand;

public class StatisticUtil {
	

	public static void statistic(Req req) throws IOException {
		StockCommand c = new StockCommand(req);
		c.start();
	}

}
