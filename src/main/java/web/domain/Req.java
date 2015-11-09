package web.domain;

import java.util.ArrayList;
import java.util.List;

public class Req {
	
	public boolean combine = false;
	
	public String maxDate;
	
	public String cookie;
	
	public List<Stock> list = new ArrayList<Stock>();
	
	//要查询的日期
	public List<String> mapKey = new ArrayList<String>();

	

}
