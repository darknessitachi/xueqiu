package util;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtil {

	public static List<String> reverse(List<String> oldList) {
		
		List<String> newList = new ArrayList<String>();
		
		for(int i = oldList.size()-1 ; i>=0 ; i--){
			newList.add(oldList.get(i));
		}
		return newList;
	}

}
