package web.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

	public static String read(String path) {
		StringBuffer result = new StringBuffer();
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(path));
			br = new BufferedReader(fr);
			String line = null;
			while((line = br.readLine()) !=null){
				result.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}
}
