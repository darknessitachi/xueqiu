package web;

import java.io.IOException;
import java.util.Date;

import config.Constants;

/**
 * 只需要把原始文件放入code中，就可以执行该方法了
 * @author Administrator
 *
 */
public class MainAutoAll {

	public static void main(String[] args) throws IOException {
		long start = new Date().getTime();
		for(String name : Constants.group){
			String fileName = Constants.CODE_PATH + name + ".EBK";
			MainAutoOne.autoOne(fileName);
		}
		long end = new Date().getTime();
		System.out.println("AutoAll总共用时："+(end-start)/1000+"秒");
	}

}
