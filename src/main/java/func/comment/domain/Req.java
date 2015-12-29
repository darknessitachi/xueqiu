package func.comment.domain;

import java.util.ArrayList;
import java.util.List;

public class Req {
	
	public String maxDate;
	
	public boolean combine = false;
	
	//每个线程在请求一页数据后，睡眠的毫秒数
	public int sleep = 0;

	public Boolean filterNotice = true;
	
	public String cookie;
	
	//要查询的日期，默认日期，从大到小排序
	public List<String> mapKey = new ArrayList<String>();
	
	public List<Stock> list = new ArrayList<Stock>();
	
	//要查询的板块名称
	public String bodyName = "未定义";
	
}
