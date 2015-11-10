package web.util;

public class StringUtil {

	public static boolean isEmpty(String maxDate) {
		return maxDate==null || "".equals(maxDate);
	}

	public static String number2word(int i) {
		String result = null;
		
		switch (i) {
		case 1:
			result = "一";
			break;
		case 2:
			result = "两";
			break;
		case 3:
			result = "三";
			break;
		case 4:
			result = "四";
			break;
		case 5:
			result = "五";
			break;
		case 6:
			result = "六";
			break;
		default:
			break;
		}
		
		if(result == null){
			System.err.println("查询天数没有对应的中文汉字。");
		}
		return result;
	}

}
