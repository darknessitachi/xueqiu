package config;


public class Constants {
	
	
	public static final String[] group = new String[]{
		//"���׮","﮵��",
		"����������","�������","��Ϣ��ȫ","�Ƽ���",
		//"ϡȱ��Դ","ϡ������",
		//"���¹�",
		//"����֧��",
		//"������ʵ",
		//"�Ϻ���ó",
		//"ʯīϩ"
	};
	// ��ѡ�� 
	public static final String ZXG_FILE_NAME = Constants.CODE_PATH + "��ѡ��.EBK";
	
	//�����·��
	//public static final String REQ_BODY_SRC_PATH = "D:/Documents/GitHub/xueqiu/src/main/java/config/request_body.txt";;
	
	//��˾��·��
	public static final String REQ_BODY_SRC_PATH = "C:/Users/yangrui/Documents/GitHub/xueqiu/src/main/java/config/request_body.txt";;
	
	
	public static final String CODE_PATH = "code/";
	
	public static final String CONFIG_PATH = "config/";
	
	public static final String REQ_HEAD_NAME = Constants.CONFIG_PATH + "request_head.txt";
	
	public static final String REQ_BODY_NAME = Constants.CONFIG_PATH + "request_body.txt";
	
	public static final String REQ_COOKIE_NAME = Constants.CONFIG_PATH + "cookie.txt";
	
	public static final String[] stockIndex = new String[]{"1999999","0399005","0399006","0399001"};
	
	public static final long XUEQIU_SLEEP = 100;
	
	public static final String outPath = "D:/ѩ���ȶ�";
	
	public static final String ebkPath = "D:/EBK";
	
	public static final String referer_prefix = "http://xueqiu.com/S/";
	
	public static final int business_sort = 1;
	public static final int business_single = 2;
	public static final int business_direct = 3;
	
	public static final String classpath = Constants.class.getClassLoader().getResource("").getPath();
	
	public static final String inter_url = "http://hq.sinajs.cn/list=";
	
	
	

}
