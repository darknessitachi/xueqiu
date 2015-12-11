package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import comment.domain.Stock;

public class HttpUtil {

	public static String getSearchUrl(Stock stock, int page) {
		String href = "http://xueqiu.com/statuses/search.json?count=15&comment=0&symbol="+ stock.code+ "&hl=0&source=all&sort=time&page="
				+ page+ "&_=1445444564351";
		return href;
	}

	public static String getResult(String url, String cookie, String referer)
			throws IOException {
		return getResult(url, cookie, referer, "utf-8");
	}

	public static String getResult(String httpReqUrl, String cookie,
			String referer, String code) throws IOException {
		String result = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(httpReqUrl).openConnection();

			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			if (cookie != null) {
				conn.setRequestProperty("Cookie", cookie);
			}
			if (referer != null) {
				conn.setRequestProperty("Referer", referer);
			}
			
			result = getResultFromInputStream(conn.getInputStream(), code);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getResult(String httpReqUrl, String code)
			throws IOException {
		return getResult(httpReqUrl, null, null, code);
	}

	public static String getResult(String httpReqUrl) throws IOException {
		return getResult(httpReqUrl, "utf-8");
	}

	
	
	public static String post(String httpReqUrl, Map<String, Object> param,
			String cookie, String referer) throws IOException {
		return handle(httpReqUrl, param, cookie, referer, "POST");
	}
	
	public static String get(String httpReqUrl, Map<String, Object> param,
			String cookie, String referer) throws IOException {
		return handle(httpReqUrl, param, cookie, referer, "GET");
	}
	
	public static String handle(String httpReqUrl, Map<String, Object> param,
			String cookie, String referer,String method) throws IOException {
		URL url = new URL(httpReqUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		if (cookie != null) {
			conn.setRequestProperty("Cookie", cookie);
		}
		if (referer != null) {
			conn.setRequestProperty("Referer", referer);
		}
		
		//连接超时 单位毫秒
		// conn.setConnectTimeout(10000);
		//读取超时 单位毫秒
		// conn.setReadTimeout(2000);
		
		// 是否输入参数
		conn.setDoOutput(true);

		StringBuffer sb = new StringBuffer();
		// 表单参数与get形式一样
		for(String key:param.keySet()){
			sb.append(key).append("=").append(param.get(key)).append("&");
		}
		byte[] bypes = sb.toString().substring(0, sb.toString().length()-1).getBytes();
		// 输入参数
		conn.getOutputStream().write(bypes);
		InputStream is = conn.getInputStream();
		
		String result = getResultFromInputStream(is,"utf-8");
		
		return result;
	}

	private static String getResultFromInputStream(InputStream is, String code) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, code));
		String line = "";
		StringBuffer resultBuffer = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			resultBuffer.append(line);
		}
		reader.close();
		return  resultBuffer.toString();
	}

}
