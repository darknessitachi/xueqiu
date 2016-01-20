package util;

import javax.swing.filechooser.FileSystemView;


public class ProjectUtil {
	
	public static String getClasspath() {
		return ProjectUtil.class.getClassLoader().getResource("").getPath();
	}
	
	/**
	 * 获取电脑桌面绝对路径
	 * @return
	 */
	public static String getComputerHomeDir() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		return fsv.getHomeDirectory().getAbsolutePath();
	}

	
	
	public static void main(String[] args) {
		System.out.println(getClasspath());
	}
	
	
	public static boolean isStockIndex(String code) {
		String prefix = code.substring(1,4);
		for(String element:Constants.stockIndex){
			if(element.equals(prefix)){
				return true;
			}
		}
		return false;
	}

}
