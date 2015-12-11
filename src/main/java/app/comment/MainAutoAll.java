package app.comment;

import java.io.IOException;
import java.util.Date;

import config.Constants;

public class MainAutoAll {

	public static void main(String[] args) throws IOException {
		long start = new Date().getTime();
		for(String name : Constants.group){
			String fileName = Constants.CODE_PATH + name + ".EBK";
			MainAutoOne.autoOne(fileName);
		}
		long end = new Date().getTime();
		System.out.println("用时："+(end-start)/1000+"秒");
	}

}
