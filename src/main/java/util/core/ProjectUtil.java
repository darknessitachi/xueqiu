package util.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import javax.swing.filechooser.FileSystemView;

import util.Constants;
import util.StringUtil;
import func.domain.Stock;


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
	
	/**
	 * 判断通达信导出的股票代码是不是指数
	 * 如果是指数，返回true
	 * @param code
	 * @return
	 */
	public static boolean isStockIndex(String code) {
		String prefix = code.substring(1,4);
		for(String element:Constants.stockIndex){
			if(element.equals(prefix)){
				return true;
			}
		}
		return false;
	}
	
	public static String getSearchUrl(Stock stock, int page) {
		int count = 20;
		String href = "http://xueqiu.com/statuses/search.json?count="+count+"&comment=0&symbol="+ stock.code+ "&hl=0&source=all&sort=time&page="
				+ page+ "&_="+new Date().getTime();
		return href;
	}
	
	/**
	 * 把通达信导出的股票代码，转化成标准的股票代码
	 * @param code
	 * @return
	 */
	public static String tdxCode2StandardCode(String code) {
		//过滤掉指数
		if(ProjectUtil.isStockIndex(code)){
			return null;
		};
		
		if(code.startsWith("1")){
			return "sh"+code.substring(1);
		}else{
			return "sz"+code.substring(1);
		}
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
	 * 读取内容的行数，过滤不正确的行
	 * @param path
	 * @param filterIndex
	 * @return
	 * @throws FileNotFoundException
	 */
	public static int readValidLineNum(String path, boolean filterIndex) throws FileNotFoundException {
		int num = 0;
		FileReader fr = new FileReader(new File(path));
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if (!StringUtil.isEmpty(line)) {
					if(filterIndex){
						if(!ProjectUtil.isStockIndex(line)){
							num++;
						}
					}else{
						num++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return num;
	}

}
