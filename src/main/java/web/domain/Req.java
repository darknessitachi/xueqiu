package web.domain;

import java.util.ArrayList;
import java.util.List;

public class Req {
	
	public static final String REQ_STOCK_NAME = "stock.txt";
	
	public static final String REQ_SEARCH_NAME = "req.txt";
	
	public static final String REQ_COOKIE_NAME = "cookie.txt";
	
	public List<Stock> list = new ArrayList<Stock>();
	
	//要查询的日期
	public List<String> mapKey = new ArrayList<String>();

}
