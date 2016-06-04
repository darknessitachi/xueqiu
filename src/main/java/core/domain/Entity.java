package core.domain;

import util.core.ProjectUtil;

public class Entity {
	
	public Stock stock;
	
	public String name;
	
	public int number;
	
	public Entity(String name, int number,Stock stock) {
		this.name = name;
		this.number = number;
		this.stock = stock;
	}
	
	@Override
	public String toString() {
		return ProjectUtil.formatStockName(name)+" : "+number;
	}

}
