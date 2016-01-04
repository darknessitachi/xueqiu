package util.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import util.FileUtil;
import util.HttpUtil;
import config.Constants;

public class TranslateUtil {
	
	public static final boolean useLog = false;
	/**
	 * 把原始ebk文件翻译完成后，写入body文件
	 * @param afterClasspath_filePath
	 * @throws IOException
	 */
	public static void translate(String afterClasspath_filePath) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		
		String readPath = Constants.classpath + afterClasspath_filePath;
		
		//先读取文件
		BufferedReader br = null;
		int num = 0;
		try {
			FileReader fr = new FileReader(new File(readPath));
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 ) {
					String completeCode = completeCode(line);
					if(completeCode!=null){
						try {
							String name = getNameByCode(completeCode);
							if(isValidName(name)){
								sb.append(completeCode).append(",").append(name).append("\n");
								num++;
								if(useLog){
									System.out.println(num);
								}
							}
						} catch (java.net.ConnectException e) {
							System.err.println("翻译【"+completeCode+"】请求异常。");
							continue;
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
		
		String result = sb.toString().toUpperCase();
		//写入文件的时候，在文件第一行加入当前板块的名词
		String fileName = getFileName(afterClasspath_filePath);
		result = fileName + "\n" + result;
		
		System.out.println(result);
		
		/*
		//写入文件到EBK目录
		String ebk_path = getWritePath();
		FileUtil.write(ebk_path,result);*/
		
		//写入request_body中
		writeRequestBody(result);
		System.out.println("写入body完成，一共写入【"+num+"】只股票！\n");
	}

	private static void writeRequestBody(String result) throws IOException {
		
		//写request_body的src路径
		/*boolean isWrite = false;
		for(int i=0;i<Constants.request_body_src_path.length;i++){
			String src_path = Constants.request_body_src_path[i];
			try {
				FileUtil.write(src_path,result);
				isWrite = true;
			} catch (java.io.FileNotFoundException e) {
				continue;
			}
		}
		if(!isWrite){
			System.err.println("request_body在src路径的文件没有写入。");
		}*/
		
		//写request_body的class路径
		String request_body_target_path = Constants.classpath + Constants.REQ_BODY_NAME;
		FileUtil.write(request_body_target_path,result);
	}

	private static boolean isValidName(String name) {
		return !"\";".equals(name);
	}

	/*private String getWritePath() {
		String nowDate = DateUtil.getNowDate();
		String fileName = getFileName();
		String writePath = Constants.ebkPath  + "/" + nowDate + " " + fileName + ".txt";
		FileUtil.createFolder(Constants.ebkPath);
		return writePath;
	}*/
	
	/**
	 * 返回要翻译的板块名称
	 * @param file
	 * @return
	 */
	private static String getFileName(String path) {
		return path.split("/")[2].split("\\.")[0];
	}


	private static String getNameByCode(String completeCode) throws IOException {
		String httpReqUrl = Constants.inter_url+completeCode;
		String result = HttpUtil.getResult(httpReqUrl,"GBK");
		//先通过=分割字符串
		String content = result.split("=")[1];
		//再通过，分割字符串
		String name = content.split(",")[0];
		return name.substring(1);
	}
	/**
	 * 通达信导出自选股编码，0开头的是sz，1开头的是sh
	 * @param code
	 * @return
	 */
	private static String completeCode(String code) {
		//过滤掉指数
		if(isStockIndex(code)){
			return null;
		};
		
		if(code.startsWith("1")){
			return "sh"+code.substring(1);
		}else{
			return "sz"+code.substring(1);
		}
	}

	private static boolean isStockIndex(String code) {
		String prefix = code.substring(1,4);
		for(String element:Constants.stockIndex){
			if(element.equals(prefix)){
				return true;
			}
		}
		return false;
	}
	
	
}
