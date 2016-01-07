package util.core;

import util.FileUtil;
import config.Constants;


public class AccessUtil {
	public static String readCookie(){
		String cookie_path = Constants.out_config_path + "/" + Constants.req_cookie_name;
		return FileUtil.read(cookie_path).trim();
	}
}
