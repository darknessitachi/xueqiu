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

	/**
	 * 读取文件的所有内容，没有换行
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static String read(String path) throws FileNotFoundException {
		StringBuffer result = new StringBuffer();
		FileReader fr = new FileReader(new File(path));
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if (!StringUtil.isEmpty(line)) {
					result.append(line);
				}
			}
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
	/**
	 * 循环创建文件夹，使用mkdirs，而不是mkdir
	 * @param outpath
	 */
	public static void createFolde(String outpath) {
		File folder = new File(outpath);
		if (!folder.exists()) {
			folder.mkdirs();
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
		FileUtil.createFolde(path);
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
				System.out.println("拷贝文件到【" + newPath + "】");
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

	public static int readValidLineNum(String path, boolean filterIndex) throws FileNotFoundException {
		int num = 0;
		FileReader fr = new FileReader(new File(path));
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if (!StringUtil.isEmpty(line)) {
					if(filterIndex){
						if(!ProjectUtil.isStockIndex(line)){
							num++;
						}
					}else{
						num++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return num;
	}
	
	public static void createNewFile(String file_path) {
		File f = new File(file_path);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
