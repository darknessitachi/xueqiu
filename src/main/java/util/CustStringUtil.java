package util;

public class CustStringUtil {
	
	
	/**
	 * 如果股票名字是3个字，后面加两个空格
	 * @param name
	 * @return
	 */
	public static String formatStockName(String name) {
		if(name.length() == 3){
			return name+"  ";
		}
		return name;
	}
	
	public static String getFormatDay(String str) {
		String[] arr1 = str.split("年");
		String year = arr1[0];
		
		String[] arr2 = arr1[1].split("月");
		String month = arr2[0];
		if(new Integer(month)<10){
			month = "0" + month;
		}
		
		String[] arr3 = arr2[1].split("号");
		String day = arr3[0];
		if(new Integer(day)<10){
			day = "0" + day;
		}
		
		return year+"-"+month+"-"+day;
	}
	
	
	/**
	 * 判断通达信导出的股票代码是不是指数
	 * 如果是指数，返回true
	 * @param code
	 * @return
	 */
	public static boolean isStockIndex(String code) {
		if(code.equals(Constants.CYB_INDEX) || code.equals(Constants.SH_INDEX)){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 把通达信导出的股票代码，转化成标准的股票代码
	 * @param code
	 * @return
	 */
	public static String tdxCode2StandardCode(String code) {
		//过滤掉指数
		if(CustStringUtil.isStockIndex(code)){
			return null;
		};
		
		if(code.startsWith("1")){
			return "sh"+code.substring(1);
		}else{
			return "sz"+code.substring(1);
		}
	}
	
	public static String StandardCode2tdxCode(String code) {
		if(code.toLowerCase().startsWith("sh")){
			return "1"+code.substring(2);
		}else{
			return "0"+code.substring(2);
		}
	}
	
	

}
