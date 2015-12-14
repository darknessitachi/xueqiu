package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static final String yyyyMMdd_HHmmss = "yyyy-MM-dd HH:mm:ss";
	
	public static final String yyyyMMdd = "yyyy-MM-dd";
	
	public static final String yyyyMMdd_HHmmss2 = "yyyy-MM-dd HH：mm：ss";
	
	public static String formatDate(Date reqDate, String format) {
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(reqDate);
	}
	/**
	 * 当前时间减i天
	 * @param i
	 * @return
	 */
	public static String minus(int i) {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Date beginDate = new Date();
		Calendar date = Calendar.getInstance();
		date.setTime(beginDate);
		date.set(Calendar.DATE, date.get(Calendar.DATE) - i);
		Date endDate = null;
		try {
			endDate = dft.parse(dft.format(date.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formatDate(endDate,"yyyy-MM-dd");
	}
	
	/**
	 * 根据传入时间，减i天
	 * @param i
	 * @return
	 */
	public static String minus(Date beginDate,int i) {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar date = Calendar.getInstance();
		date.setTime(beginDate);
		date.set(Calendar.DATE, date.get(Calendar.DATE) - i);
		Date endDate = null;
		try {
			endDate = dft.parse(dft.format(date.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formatDate(endDate,"yyyy-MM-dd");
	}
	
	
	/**
	 * 根据传入时间，加i天
	 * @param i
	 * @return
	 */
	public static String add(Date beginDate,int i) {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar date = Calendar.getInstance();
		date.setTime(beginDate);
		date.set(Calendar.DATE, date.get(Calendar.DATE) + i);
		Date endDate = null;
		try {
			endDate = dft.parse(dft.format(date.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formatDate(endDate,"yyyy-MM-dd");
	}
	
	 /** 
     * 使用参数Format将字符串转为Date 
     */  
    public static Date parse(String strDate, String pattern)
    {  
        try {
			return new SimpleDateFormat(pattern).parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
        return null;
    }  

}
