package util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

@SuppressWarnings("deprecation")
public class HttpClientUtil {
	
	
	
	public static String get(String httpReqUrl, Map<String, Object> param,
			String cookie, String referer) throws IOException {
		return handle(httpReqUrl, param, cookie, referer, "GET");
	}
	
	
	
	@SuppressWarnings("resource")
	public static String handle(String httpReqUrl, Map<String, Object> param,
			String cookie, String referer,String method) throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
        //使用Get方式请求
        HttpGet httpget = new HttpGet(httpReqUrl);
        httpget.addHeader(new BasicHeader("Cookie",cookie)); //设置cookie
        httpget.setHeader("Referer", referer);//设置referer
        
        for(String key:param.keySet()){
        	httpget.getParams().setParameter(key, param.get(key));
		}
          
        //执行请求
        try {
            HttpResponse response = client.execute(httpget);
            
            // 得到返回的client里面的实体对象信息.  
            HttpEntity entity = response.getEntity();  
            if (entity != null) {  
                // 得到返回的主体内容.  
                InputStream instream = entity.getContent();  
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));  
                return reader.readLine();
            }  
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
	    return null;
	}

	
	public static String getResult(String url, String cookie, String referer)
			throws IOException {
		return getResult(url,cookie,referer,"UTF-8");
	}
	
	
	
	@SuppressWarnings("resource")
	public static String getResult(String httpReqUrl, String cookie,
			String referer, String code) {
		DefaultHttpClient client = new DefaultHttpClient();
        //使用Get方式请求
        HttpGet httpget = new HttpGet(httpReqUrl);
        httpget.addHeader(new BasicHeader("Cookie",cookie)); //设置cookie
        httpget.setHeader("Referer", referer);//设置referer
          
        //执行请求
        try {
            HttpResponse response = client.execute(httpget);
            
            
            // 得到返回的client里面的实体对象信息.  
            HttpEntity entity = response.getEntity();  
            if (entity != null) {  
                // 得到返回的主体内容.  
                InputStream instream = entity.getContent();  
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream, code));  
                return reader.readLine();
            }  
           // System.out.println("httpclicent"+response.getStatusLine().getStatusCode());
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
		
		return null;
	}
	

}
