package web.domain;

import java.util.HashMap;
import java.util.Map;

public class Stock {
	
	public String code;
	
	public String name;
	
	//2015-10-20=20,2015-10-21=12
	public Map<String,Integer> map = new HashMap<String, Integer>();

	public Stock(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
	
	
}
