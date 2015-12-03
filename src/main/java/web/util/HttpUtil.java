package web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import web.domain.Stock;

public class HttpUtil {
	
	
	public static String getSearchUrl(Stock stock, int page) {
		String href = "http://xueqiu.com/statuses/search.json?count=15&comment=0&symbol="+stock.code+"&hl=0&source=all&sort=time&page="+page+"&_=1445444564351";
		return href;
	}

	public static String getResult(String url,String cookie, String referer) throws IOException{ 
        return getResult(url,cookie,referer,"utf-8");
    }

	public static String getResult(String httpReqUrl, String cookie,
			String referer, String code) throws IOException {
		String result = null;
		try {
			BufferedReader reader = null;  
			HttpURLConnection conn = (HttpURLConnection) new URL(httpReqUrl).openConnection();  
			
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			if(cookie!=null){
				conn.setRequestProperty("Cookie",cookie);  
			}
			if(referer!=null){
				conn.setRequestProperty("Referer",referer);
			}
			
			
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),code));  
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
		}
        return result;
	}

	public static String getResult(String httpReqUrl, String code) throws IOException {
		return getResult(httpReqUrl,null,null,code);
	}


}
