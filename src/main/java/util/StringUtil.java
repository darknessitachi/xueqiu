package util;

import javax.swing.filechooser.FileSystemView;

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
		case 7:
			result = "七";
			break;
		case 8:
			result = "八";
			break;
		case 9:
			result = "九";
			break;
		case 10:
			result = "十";
			break;
		default:
			break;
		}
		
		if(result == null){
			System.err.println("查询天数没有对应的中文汉字。");
			result = i+"";
		}
		return result;
	}
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
	/**
	 * 通达信导出自选股编码，0开头的是sz，1开头的是sh
	 * @param code
	 * @return
	 */
	public static String xq2Tdx(String code) {
		code = code.substring(2);
		if(code.startsWith("6")){
			return "1"+code;
		}
		return "0"+code;
	}
	/**
	 * 获取电脑桌面绝对路径
	 * @return
	 */
	public static String getComputerHomeDir() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		return fsv.getHomeDirectory().getAbsolutePath();
	}

}
