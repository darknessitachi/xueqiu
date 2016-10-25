package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import util.FileUtil;


public class AccessUtil {
	public static String readCookie(){
		String cookie_path = Constants.out_config_path + "/" + Constants.req_cookie_name;
		String cookie = null;
		try {
			cookie = FileUtil.read(cookie_path).trim();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return cookie;
	}

	public static Properties readParams() {
		String params_path = Constants.out_config_path + "/" + Constants.req_params_name;
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(params_path));
			//prop.load(new InputStreamReader(new FileInputStream(params_path), "GBK"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return prop;
	}
}
