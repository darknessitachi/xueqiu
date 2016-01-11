package func.domain;

import java.util.HashMap;
import java.util.Map;

public class Stock {
	
	public String code;
	
	public String name;
	
	//2015-10-20=20,2015-10-21=12
	public Map<String,Integer> result = new HashMap<String, Integer>();
	
	//请求是否出错
	public boolean isError = false;

	public Stock(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
}
