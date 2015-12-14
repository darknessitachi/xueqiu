package app.comment;

import java.io.IOException;
import java.util.Date;

import config.Constants;

public class MainAutoAll {

	public static void main(String[] args) throws IOException {
		long start = new Date().getTime();
		for(String name : Constants.group){
			MainAutoCurrent.autoOne(name+".EBK");
		}
		long end = new Date().getTime();
		System.out.println("总共用时："+(end-start)/1000+"秒");
	}

}
