package util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
public class HttpClientUtil {
	
	
	
	public static String get(String httpReqUrl, Map<String, Object> param,
			String cookie, String referer) throws IOException {
		return handle(httpReqUrl, param, cookie, referer, "GET");
	}
	
	
	public static String post(String httpReqUrl, Map<String, String> param,
			String cookie, String referer){
		
		
		return null;
		
	}
	
	 private static String paseResponse(HttpResponse response) {  
	        HttpEntity entity = response.getEntity();  
	          
	        String charset = EntityUtils.getContentCharSet(entity);  
	          
	        String body = null;  
	        try {  
	            body = EntityUtils.toString(entity);  
	        } catch (ParseException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	          
	        return body;  
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
	
	 private static HttpPost postForm(String url, Map<String, String> params){  
         
	        HttpPost httpost = new HttpPost(url);  
	        List<NameValuePair> nvps = new ArrayList <NameValuePair>();  
	          
	        Set<String> keySet = params.keySet();  
	        for(String key : keySet) {  
	            nvps.add(new BasicNameValuePair(key, params.get(key)));  
	        }  
	          
	        try {  
	            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	        }  
	          
	        return httpost;  
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
