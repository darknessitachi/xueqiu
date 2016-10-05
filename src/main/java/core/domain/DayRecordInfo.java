package core.domain;

import java.util.ArrayList;
import java.util.List;

public class DayRecordInfo {
	
	//2016年9月13号
	public String day;
	
	public List<Float> list;
	
	public DayRecordInfo(String day) {
		super();
		this.day = day;
		list = new ArrayList<Float>();
	}
	
	public void addValue(Float value) {
		list.add(value);
	}
	
	public String getMonth() {
		return day.substring(0,7);
	}
	
	public float getMidRate() {
		float total = 0;
		for(Float f : list){
			total = total + f;
		}
		float base = total/list.size();
		//原始数据点数是整数，返回值要除以100
		return base/100;
	}
	
	public float getMinRate() {
		float min = 1000;
		for(Float f : list){
			if(f<min){
				min = f;
			}
		}
		return min/100;
	}
	
	public float getMaxRate() {
		float max = 0;
		for(Float f : list){
			if(f>max){
				max = f;
			}
		}
		return max/100;
	}



	
	
	
}
