package bean;

import java.util.Comparator;

public class ComparatorEntity implements Comparator<Object> {

	public int compare(Object arg0, Object arg1) {
		
		Entity e1 = (Entity) arg0;
		Entity e2 = (Entity) arg1;

		return e2.number - e1.number;
	}

}
