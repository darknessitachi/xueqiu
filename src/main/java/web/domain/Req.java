package web.domain;

import java.util.ArrayList;
import java.util.List;

public class Req {
	
	public String maxDate;
	
	public boolean combine = false;
	
	public String cookie;
	
	public List<Stock> list = new ArrayList<Stock>();
	
	//要查询的日期
	public List<String> mapKey = new ArrayList<String>();
	
	//每个线程在请求一页数据后，睡眠的毫秒数
	public int sleep;
	

}
