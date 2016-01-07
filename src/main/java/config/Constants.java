package config;

import util.StringUtil;


public class Constants {
	
	public static final String classpath = Constants.class.getClassLoader().getResource("").getPath();
	
	public static final String code_path = "code/";
	
	public static final String config_path = "config/";
	
	public static final String req_head_name = "request_head.txt";
	
	public static final String req_body_name = "request_body.txt";
	
	public static final String req_cookie_name = "cookie.txt";
	
	public static final String[] stockIndex = new String[]{"999","399"};
	
	public static final long XUEQIU_SLEEP = 100;
	
	public static final String out_path = "D:/xueqiu/result";
	
	public static final String export_path = StringUtil.getComputerHomeDir();
	
	public static final String referer_prefix = "http://xueqiu.com/S/";
	public static final String inter_url = "http://hq.sinajs.cn/list=";
	
	public static final String concept_absolute_path = classpath + code_path + "concept";
	public static final String industry_absolute_path = classpath + code_path + "industry";

}
