package config;

import util.StringUtil;


public class Constants {
	
	// 自选股 
	public static final String ZXG_FILE_NAME = "自选股.EBK";
	
	//request_body的src路径集合
	public static final String[] request_body_src_path = new String[]{
		"D:/Documents/GitHub/xueqiu/src/main/java/config/request_body.txt",	//家里的路径
		"D:/projects/xq2/xueqiu-master/src/main/java/config/request_body.txt",
		"C:/Users/yangrui/Documents/GitHub/xueqiu/src/main/java/config/request_body.txt",	//公司的路径
	};
	
	
	public static final String CODE_PATH = "code/";
	
	public static final String CONFIG_PATH = "config/";
	
	public static final String REQ_HEAD_NAME = Constants.CONFIG_PATH + "request_head.txt";
	
	public static final String REQ_BODY_NAME = Constants.CONFIG_PATH + "request_body.txt";
	
	public static final String REQ_COOKIE_NAME = Constants.CONFIG_PATH + "cookie.txt";
	
	public static final String[] stockIndex = new String[]{"1999999","0399005","0399006","0399001"};
	
	public static final long XUEQIU_SLEEP = 100;
	
	public static final String outPath = "D:/xueqiu";
	
	public static final String ebkPath = "D:/ebk";
	
	public static final String export = StringUtil.getComputerHomeDir();
	
	public static final String referer_prefix = "http://xueqiu.com/S/";
	
	public static final int business_sort = 1;
	public static final int business_single = 2;
	public static final int business_direct = 3;
	
	public static final String classpath = Constants.class.getClassLoader().getResource("").getPath();
	
	public static final String inter_url = "http://hq.sinajs.cn/list=";
	
	public static final String custom_path = classpath + CODE_PATH + "custom";
	public static final String concept_path = classpath + CODE_PATH + "concept";
	public static final String industry_path = classpath + CODE_PATH + "industry";

}
