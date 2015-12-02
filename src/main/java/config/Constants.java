package config;


public class Constants {
	
	
	public static final String CODE_PATH = "code/";
	// 自选股    down  up
	public static final String ZXG_FILE_NAME = Constants.CODE_PATH + "down.EBK";
	
	
	public static final String CONFIG_PATH = "config/";
	
	public static final String REQ_HEAD_NAME = Constants.CONFIG_PATH + "request_head.txt";
	
	public static final String REQ_BODY_NAME = Constants.CONFIG_PATH + "request_body.txt";
	
	public static final String REQ_COOKIE_NAME = Constants.CONFIG_PATH + "cookie.txt";
	
	
	public static final String outPath = "D:/雪球热度";
	
	public static final String referer_prefix = "http://xueqiu.com/S/";
	
	public static final int business_sort = 1;
	public static final int business_single = 2;
	public static final int business_direct = 3;
	
	public static final String classpath = Constants.class.getClassLoader().getResource("").getPath();
	
	public static final String inter_url = "http://hq.sinajs.cn/list=";

}
