package gui;

import gui.core.StockFrame;

import java.io.File;

import util.Constants;
import util.FileUtil;
import util.core.ProjectUtil;



public class MainGui {

	public static void main(String[] args) throws ClassNotFoundException {
		validate();
		new StockFrame("stock");
	}

	/**
	 * 启动的时候，验证目录和文件是否存在
	 */
	private static void validate() {
		
		validateDict();
		ValidateFile();
		
	}

	private static void validateDict() {
		FileUtil.createFolder(Constants.out_result_path);
		FileUtil.createFolder(Constants.out_config_path);
		FileUtil.createFolder(Constants.out_custom_path);
		FileUtil.createFolder(Constants.out_concept_path);
		FileUtil.createFolder(Constants.out_industry_path);
	}
	
	private static void ValidateFile() {
		
		String cookiePath = Constants.out_config_path+"/"+Constants.req_cookie_name;
		String paramsPath = Constants.out_config_path+"/"+Constants.req_params_name;
		
		if(!FileUtil.exists(cookiePath)){
			System.out.println("拷贝cookie文件到【"+Constants.out_config_path+"】中");
			File oldfile = new File(ProjectUtil.getClasspath() + Constants.config_path + Constants.req_cookie_name);
			FileUtil.copy(cookiePath, oldfile);
		};
		
		if(!FileUtil.exists(paramsPath)){
			System.out.println("拷贝params文件到【"+Constants.out_config_path+"】中");
			File oldfile = new File(ProjectUtil.getClasspath() + Constants.config_path + Constants.req_params_name);
			FileUtil.copy(paramsPath, oldfile);
		};
	}

}
