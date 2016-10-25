package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bean.Stock;
import bean.Req.ReqBody;
import util.StringUtil;
import util.http.HttpClientUniqueUtil;

public class TranslateUtil {
	
	public static final boolean useLog = false;
	/**
	 * 把原始ebk文件翻译完成后，写入body对象
	 * @param absolute_path
	 * @throws IOException
	 */
	public static ReqBody translate(String absolute_path) throws IOException {
		
		ReqBody body = new ReqBody();
		body.bodyName = StringUtil.getFileName(absolute_path);
		
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
					String completeCode = CustStringUtil.tdxCode2StandardCode(line);
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
		
		System.out.println("\n【"+absolute_path+"】文件中有【"+num+"】只股票！");
		return body;
	}


	private static boolean isValidName(String name) {
		return !"\";".equals(name);
	}


	private static String getNameByCode(String completeCode) throws IOException {
		String httpReqUrl = Constants.inter_url+completeCode;
		
		//请求头
		Map<String,String> header = new HashMap<String,String>();
		header.put(HttpClientUniqueUtil.ENCODING, "GBK");
		
		String result = HttpClientUniqueUtil.get(httpReqUrl,header);
		//先通过=分割字符串
		String content = result.split("=")[1];
		//再通过，分割字符串
		String name = content.split(",")[0];
		return name.substring(1);
	}
	

}
