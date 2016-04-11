package util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;


public class HttpClientUniqueUtil {
	
	
	public static final String COOKIE = "cookie";
	public static final String REFERER = "referer";
	public static final String ENCODING = "encoding";
	

	public static String get(String httpReqUrl,Map<String, String> headers) throws IOException {
	
		CloseableHttpClient client = HttpClients.createDefault();
        //使用Get方式请求
        HttpGet httpGet = new HttpGet(httpReqUrl);
        
        setHeader(httpGet,headers);
        
        String encoding = "UTF-8";
        if(headers.get(ENCODING)!=null){
        	encoding = headers.get(ENCODING);
        }
        
        //执行请求
        try {
            HttpResponse response = client.execute(httpGet);
            
            HttpEntity entity = response.getEntity();  
            if (entity != null) {  
                // 得到返回的主体内容.  
                InputStream instream = entity.getContent();  
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream, encoding));  
                return reader.readLine();
            }  
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
		return null;
	}
	
	public static String post(String httpReqUrl,Map<String, String> headers, Map<String, String> param) throws IOException {
		
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        HttpPost httpPost = new HttpPost(httpReqUrl);  
        
        setHeader(httpPost,headers);
        
        String encoding = "UTF-8";
        if(headers.get(ENCODING)!=null){
        	encoding = headers.get(ENCODING);
        }
        
        UrlEncodedFormEntity formEntity;  
        try {  
        	 List<NameValuePair> formData = fromMap2Pair(param);
            formEntity = new UrlEncodedFormEntity(formData, encoding);  
            httpPost.setEntity(formEntity);  
            CloseableHttpResponse response = httpclient.execute(httpPost);  
            try {  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                    //System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));  
                }  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
		return null;
	}
	
	
	/**
	 * 设置请求头信息
	 * @param method
	 * @param headers
	 */
	private static void setHeader(HttpRequestBase method, Map<String, String> headers) {
		if(headers != null){
			if(headers.get(HttpClientUniqueUtil.COOKIE)!=null){
	        	method.addHeader("Cookie",headers.get(HttpClientUniqueUtil.COOKIE));
	        }
	        
	        if(headers.get(HttpClientUniqueUtil.REFERER)!=null){
	        	method.addHeader("Referer",headers.get(HttpClientUniqueUtil.REFERER));
	        }
		}
	}

	/**
	 * 通过map创建参数对象
	 * @param param
	 * @return
	 */
	private static List<NameValuePair> fromMap2Pair(Map<String, String> param) {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
        for(String key : param.keySet()){
        	 formparams.add(new BasicNameValuePair(key, param.get(key))); 
        }
		return formparams;
	}
	

}
