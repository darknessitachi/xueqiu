package web.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	//yyyy-MM-dd HH:mm:ss
	public static String formatDate(Date reqDate, String format) {
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(reqDate);
	}

}
