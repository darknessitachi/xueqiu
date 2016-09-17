package util.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.swing.filechooser.FileSystemView;

import util.Constants;
import util.FileUtil;
import util.StringUtil;
import core.domain.Stock;


public class ProjectUtil {
	
	/**
	 * 获取工程的路径
	 * @return
	 */
	public static String getProjectPath() {
		File directory = new File("");
		String outputPath = directory.getAbsolutePath();
		return outputPath;
	}
	
	
	
	/**
	 * 获取工程class文件的路径
	 * @return
	 */
	public static String getSrcpath() {
		return getProjectPath()+"/src/main/java";
	}
	
	/**
	 * 获取工程class文件的路径
	 * @return
	 */
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
		System.out.println(getSrcpath());
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
	
	public static String StandardCode2tdxCode(String code) {
		if(code.toLowerCase().startsWith("sh")){
			return "1"+code.substring(2);
		}else{
			return "0"+code.substring(2);
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
		//System.out.println(path);
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

	
	/**
	 * 验证工程需要的文件夹是否存在，不存在则建立
	 */
	public static void validate() {
		validateDict();
		validateFile();
	}
	
	private static void validateDict() {
		FileUtil.createFolder(Constants.out_result_path);
		FileUtil.createFolder(Constants.out_config_path);
		FileUtil.createFolder(Constants.out_custom_path);
		FileUtil.createFolder(Constants.out_concept_path);
		FileUtil.createFolder(Constants.out_industry_path);
	}
	private static void validateFile() {
		String cookiePath = Constants.out_config_path+"/"+Constants.req_cookie_name;
		String paramsPath = Constants.out_config_path+"/"+Constants.req_params_name;
		boolean noCookie = false;
		if(!FileUtil.exists(cookiePath)){
			System.out.println("拷贝cookie文件到【"+Constants.out_config_path+"】中");
			File oldfile = new File(ProjectUtil.getClasspath() + Constants.config_path + Constants.req_cookie_name);
			FileUtil.copy(cookiePath, oldfile);
			noCookie = true;
		};
		
		if(!FileUtil.exists(paramsPath)){
			System.out.println("拷贝params文件到【"+Constants.out_config_path+"】中");
			File oldfile = new File(ProjectUtil.getClasspath() + Constants.config_path + Constants.req_params_name);
			FileUtil.copy(paramsPath, oldfile);
		};
		//登录操作
		if(noCookie){
			Properties params = AccessUtil.readParams();
			String username = params.getProperty("username");
			String password = params.getProperty("password");
			if (StringUtil.isEmpty(username)) {
				System.err.println("params.properties缺少用户登录信息。");
			}
			XueqiuUtil xq = new XueqiuUtil();
			String cookies = xq.login(username,password);
			try {
				FileUtil.write(Constants.out_config_path+"/"+Constants.req_cookie_name, cookies);
				System.out.println("登录成功。");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

}
