package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	public static String read(String path) {
		StringBuffer result = new StringBuffer();
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(path));
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}

	public static void createFolder(String outpath) {
		File folder = new File(outpath);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	public static void write(String writePath, String result)
			throws IOException {
		// System.out.println("写入文件："+writePath);
		File f = new File(writePath);
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(result);
		bw.close();
	}

	public static List<String> getFileFromFolder(String path) {
		List<String> result = new ArrayList<String>();
		File file = new File(path);
		File[] tempList = file.listFiles();
		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
				String name = tempList[i].getName().split("\\.")[0];
				result.add(name);
			}
		}
		return result;
	}

	public static void copy(String newPath, File oldfile) {
		
		int byteread = 0;
		FileOutputStream fs = null;
		try {
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldfile);
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				System.out.println("拷贝文件到【"+newPath+"】");
			}
		} catch (Exception e) {
			System.err.println("error");
			e.printStackTrace();
		} finally {
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean delete(String filepath) {
		boolean flag = false;  
	    File file = new File(filepath);  
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	}

	public static int readValidLineNum(String path) {
		int num = 0;
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(path));
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if(!StringUtil.isEmpty(line)){
					num++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return num;
	}
}
