package web.domain;

public class Entity {
	
	
	public String name;
	
	public int number;
	
	public Entity(String name, int number) {
		this.name = name;
		this.number = number;
	}
	
	@Override
	public String toString() {
		return name+" : "+number;
	}

}
