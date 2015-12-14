package config;


public class Constants {
	
	
	public static final String[] group = new String[]{
		//"充电桩","锂电池",
		//"互联网金融","国产软件","信息安全","云计算",
		//"汽车电子",
		//"稀缺资源",
		//"稀土永磁",
		//"次新股",
		//"电子支付",
		//"虚拟现实",
		//"上海自贸",
		//"石墨烯"
		//"黄金概念",
		//"央企改革",
		//"自选股"
		"证券",
		//"01强势股"
	};
	// 自选股 
	public static final String ZXG_FILE_NAME = "自选股.EBK";
	
	
	//request_body的src路径集合
	public static final String[] request_body_src_path = new String[]{
		"D:/Documents/GitHub/xueqiu/src/main/java/config/request_body.txt",	//家里的路径
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
	
	public static final String ebkPath = "D:/EBK";
	
	public static final String referer_prefix = "http://xueqiu.com/S/";
	
	public static final int business_sort = 1;
	public static final int business_single = 2;
	public static final int business_direct = 3;
	
	public static final String classpath = Constants.class.getClassLoader().getResource("").getPath();
	
	public static final String inter_url = "http://hq.sinajs.cn/list=";
	
	
	

}
