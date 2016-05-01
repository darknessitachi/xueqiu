package util.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import util.Constants;
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
		InputStream fis = null;
		Properties prop = new Properties();
		try {
			fis = new FileInputStream(params_path);
			prop.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return prop;
	}
}
