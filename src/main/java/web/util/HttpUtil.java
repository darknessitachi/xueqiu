package web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import web.StockCommand;
import web.domain.Stock;

public class HttpUtil {
	
	
	public static String getReqUrl(Stock stock, int page) {
		String href = "http://xueqiu.com/statuses/search.json?count=15&comment=0&symbol="+stock.code+"&hl=0&source=all&sort=time&page="+page+"&_=1445444564351";
		return href;
	}

	public static String getResult(String url,String cookie, String referer){ 
		String result = null;
		try {
			BufferedReader reader = null;  
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();  
			
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			conn.setRequestProperty("Cookie",cookie);  
			conn.setRequestProperty("Referer",referer);
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));  
			String line = "";  
			StringBuffer resultBuffer = new StringBuffer();  
			while((line = reader.readLine()) != null){  
			    resultBuffer.append(line);  
			}  
			result = resultBuffer.toString();
			conn.disconnect();  
			reader.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			StockCommand.isError.set(true);
			System.err.println("您的请求过于频繁，请稍后再试。"+HttpUtil.class.getName());
			//e.printStackTrace();
		}  
        return result;
    }


}
