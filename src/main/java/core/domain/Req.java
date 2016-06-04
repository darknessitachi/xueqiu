package core.domain;

import java.util.ArrayList;
import java.util.List;

public class Req {
	
	public ReqHead head;
	public ReqBody body;
	public String cookie;
	
	public long startDate;
	public long endDate;
	
	//要查询的日期，默认日期，从大到小排序
	public List<String> mapKey = new ArrayList<String>();
	
	public static class ReqHead{
		
		public boolean filterNotice = true;
		public boolean combine = true;
		
		public int day = 1;
		public int sleep = 1000;
		public int threadNum = 1;
		public int errWaitTime = 38;
		public int addTime = 5;
		
	}
	public static class ReqBody{
		public String bodyName = null;
		public List<Stock> list = new ArrayList<Stock>();
	}
	
}
