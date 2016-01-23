package util.core;

import java.io.File;
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
			System.out.println("拷贝cookie文件到【"+Constants.out_config_path+"】中");
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

	public static Properties readParams() {
		String params_path = Constants.out_config_path + "/" + Constants.req_params_name;
		
		InputStream fis = null;
		try {
			fis = new FileInputStream(params_path);
		} catch (FileNotFoundException e) {
			System.out.println("拷贝params文件到【"+Constants.out_config_path+"】中");
			File oldfile = new File(ProjectUtil.getClasspath() + Constants.config_path + Constants.req_params_name);
			FileUtil.createFolde(Constants.out_config_path);
			FileUtil.copy(params_path, oldfile);
			try {
				fis = new FileInputStream(params_path);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		
		Properties prop = new Properties();
		try {
			prop.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
