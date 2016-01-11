package config;

import util.StringUtil;


public class Constants {
	
	public static final String classpath = Constants.class.getClassLoader().getResource("").getPath();
	
	public static final String code_path = "code/";
	public static final String config_path = "config/";
	
	public static final String req_cookie_name = "cookie.txt";
	
	public static final String[] stockIndex = new String[]{"999","399"};
	
	public static final long XUEQIU_SLEEP = 100;
	
	public static final String out_path   =  "d:/xueqiu/";
	public static final String out_result_path   =  "d:/xueqiu/result";
	public static final String out_config_path   =  "d:/xueqiu/config";
	public static final String out_custom_path   =  "d:/xueqiu/code/custom";
	public static final String out_concept_path  =  "d:/xueqiu/code/concept";
	public static final String out_industry_path =  "d:/xueqiu/code/industry";
	
	public static final String export_path = StringUtil.getComputerHomeDir();
	
	public static final String referer_prefix = "http://xueqiu.com/S/";
	public static final String inter_url = "http://hq.sinajs.cn/list=";
	
	

}
