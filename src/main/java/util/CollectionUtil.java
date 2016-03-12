package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtil {
	/**
	 * 对列表进行倒序
	 * @param oldList
	 * @return
	 */
	public static List<String> reverse(List<String> oldList) {
		
		List<String> newList = new ArrayList<String>();
		
		for(int i = oldList.size()-1 ; i>=0 ; i--){
			newList.add(oldList.get(i));
		}
		return newList;
	}
	
	/**
	 * 获取两个list中相同的元素
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static List<String> same(List<String> list1, List<String> list2) {
		List<String> result = new ArrayList<String>();
		for(String str : list1){
			if(list2.contains(str)){
				result.add(str);
			}
		}
		return result;
	}
	/**
	 * 把list转化成字符串，并且换行
	 * @param same
	 * @return
	 */
	public static String toLineString(Collection<String> same) {
		StringBuilder sb = new StringBuilder();
		for(String str : same){
			sb.append(str).append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * 统计targert中独有的元素
	 * @param result_1
	 * @param result_2
	 * @return
	 */
	public static List<String> different(List<String> targert,
			List<String> filter) {
		List<String> result = new ArrayList<String>();
		for(String str : targert){
			if(!filter.contains(str)){
				result.add(str);
			}
		}
		return result;
	}

}
