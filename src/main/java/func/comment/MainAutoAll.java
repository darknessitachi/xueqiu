package func.comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MainAutoAll {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		long start = new Date().getTime();
		for(String name : new ArrayList<String>()){
			MainAutoOne.autoOne(name);
		}
		long end = new Date().getTime();
		System.out.println("总共用时："+(end-start)/1000+"秒");
	}

}
