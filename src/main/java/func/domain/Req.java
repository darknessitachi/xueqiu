package func.domain;

import java.util.ArrayList;
import java.util.List;

public class Req {
	
	public ReqHead head;
	
	public String cookie;
	
	//要查询的日期，默认日期，从大到小排序
	public List<String> mapKey = new ArrayList<String>();
	
	public List<Stock> list = new ArrayList<Stock>();
	
	//要查询的板块名称
	public String bodyName = "未定义";
	
}
