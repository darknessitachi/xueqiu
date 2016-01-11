package util.core;

import java.io.File;
import java.io.FileNotFoundException;

import util.FileUtil;
import util.ProjectUtil;
import config.Constants;


public class AccessUtil {
	public static String readCookie(){
		String cookie_path = Constants.out_config_path + "/" + Constants.req_cookie_name;
		String cookie = null;
		try {
			cookie = FileUtil.read(cookie_path).trim();
		} catch (FileNotFoundException e) {
			//把classpath中的cookie拷贝到out_config_path
			File oldfile = new File(ProjectUtil.getClasspath() + Constants.config_path + Constants.req_cookie_name);
			FileUtil.createFolde(Constants.out_config_path);
			FileUtil.copy(cookie_path, oldfile);
			try {
				cookie = FileUtil.read(cookie_path).trim();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		return cookie;
	}
}
