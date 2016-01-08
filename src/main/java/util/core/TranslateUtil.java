package util.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import util.HttpUtil;
import config.Constants;
import func.domain.ReqBody;
import func.domain.Stock;

public class TranslateUtil {
	
	public static final boolean useLog = false;
	/**
	 * 把原始ebk文件翻译完成后，写入body对象
	 * @param absolute_path
	 * @throws IOException
	 */
	public static ReqBody translate(String absolute_path) throws IOException {
		
		ReqBody body = new ReqBody();
		body.bodyName = getFileName(absolute_path);
		
		//先读取文件
		BufferedReader br = null;
		int num = 0;
		try {
			FileReader fr = new FileReader(new File(absolute_path));
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
								body.list.add(new Stock(completeCode.toUpperCase(), name));
								num++;
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
		
		System.out.println("一共写入【"+num+"】只股票！\n");
		return body;
	}


	private static boolean isValidName(String name) {
		return !"\";".equals(name);
	}

	/**
	 * 返回要翻译的板块名称
	 * @param file
	 * @return
	 */
	private static String getFileName(String path) {
		int lastIndex = path.lastIndexOf("/");
		return path.substring(lastIndex+1).split("\\.")[0];
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
