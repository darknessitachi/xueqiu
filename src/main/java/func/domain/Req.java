package func.domain;

import java.util.ArrayList;
import java.util.List;

public class Req {
	
	public ReqHead head;
	public ReqBody body;
	
	public String cookie;
	
	//要查询的日期，默认日期，从大到小排序
	public List<String> mapKey = new ArrayList<String>();
	
	public static class ReqHead{
		public String nowDate = null;
		public int day = 1;
		public boolean combine = true;
		public int sleep = 1000;
		public boolean filterNotice = true;
		public int threadNum = 1;
		//[10,6,3]从大到小排序
		public List<Integer> levels = new ArrayList<Integer>();
		public String combineName = null;
	}
	public static class ReqBody{
		public String bodyName = null;
		public List<Stock> list = new ArrayList<Stock>();
	}
	
}
