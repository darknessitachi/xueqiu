package web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {

	public static String getResult(String url,String cookie){ 
		String result = null;
		try {
			BufferedReader reader = null;  
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();  
			
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			conn.setRequestProperty("Cookie",cookie);  
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
			e.printStackTrace();
		}  
        return result;
    } 

}
