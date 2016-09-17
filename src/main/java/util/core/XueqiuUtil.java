package util.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.Constants;
import util.DateUtil;
import util.FileUtil;
import util.StringUtil;
import util.http.HttpClientUniqueUtil;
import core.domain.Req.ReqBody;
import core.domain.Stock;

public class XueqiuUtil {
	
	
	private static final String ALL_GROUP_PID = "-1";
	private static final String ZX_GROUP_PID = "2";
	private static final String A2_GROUP_PID = "0";
	private static final String A3_GROUP_PID = "1";
	
	private String cookie = AccessUtil.readCookie();
	
	//当前雪球list
	private List<String> xueqiuList = null;
	
	//组名：组下的个股
	private Map<String,List<Stock>> map = new HashMap<String, List<Stock>>();
	
	
	/**
	 * 
	 * @param realTime 实时
	 * @return
	 */
	public int countXueqiu(boolean realTime){
		if(realTime){
			try {
				try {
					Thread.sleep(Constants.XUEQIU_SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return this.query(ALL_GROUP_PID).size();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				setXueqiuList();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return xueqiuList.size();
		}
		return 0;
	};
	
	
	public void delAll() throws IOException, InterruptedException {
		List<String> stocks = this.query(ALL_GROUP_PID);
		for(String code:stocks){
			delStock(code);
			Thread.sleep(Constants.XUEQIU_SLEEP);
		}
		System.out.println("股票清理完成，一共清理【"+stocks.size()+"】只股票。");
	}
	
	public int uploadFile(String name) throws IOException, InterruptedException {
		ReqBody body = TranslateUtil.translate(name);
		return uploadBody(body);
	}
	
	/**
	 * 上传自选股到指定组
	 * @param name
	 * @param string
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public int uploadFile(String name, String groupName) throws IOException, InterruptedException {
		ReqBody body = TranslateUtil.translate(name);
		//System.out.println(name+"下的个股数："+body.list.size());
		
		map.put(groupName, body.list);
		
		return uploadBody(body);
	}
	


	/**
	 * 添加个股到指定组内
	 * @param code
	 * @param name
	 */
	private void addIntoGroup(String code,String groupName) {
		//请求头
		Map<String,String> header = new HashMap<String,String>();
		header.put(HttpClientUniqueUtil.COOKIE, cookie);
		header.put(HttpClientUniqueUtil.REFERER, "http://xueqiu.com/S/"+code);
		
		//请求参数
		Map<String,String> params = new HashMap<String,String>();
		params.put("pnames", groupName);
		params.put("symbol", code);
		params.put("category", "2");
		
		try {
			HttpClientUniqueUtil.post("https://xueqiu.com/v4/stock/portfolio/updstock.json",header,params);
			System.out.println("添加【"+code+"】到组【"+groupName+"】完成。");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出自选股到桌面
	 * @throws IOException
	 */
	public void export() throws IOException{
		List<String> list = this.query(ALL_GROUP_PID);
		StringBuilder sb = new StringBuilder();
		for(String code : list){
			String tdxCode = StringUtil.xq2Tdx(code);
			sb.append(tdxCode).append("\n");
		}
		String writePath = getWritePath();
		FileUtil.write(writePath, sb.toString());
		System.out.println("导出股票个数【"+list.size()+"】");
	}
	
	
	
	private void setXueqiuList() throws IOException {
		if(this.xueqiuList == null){
			this.xueqiuList = this.query(ALL_GROUP_PID);
		}
	}
	
	private void delStock(String code) throws IOException {
		//请求头
		Map<String,String> header = new HashMap<String,String>();
		header.put(HttpClientUniqueUtil.COOKIE, cookie);
		header.put(HttpClientUniqueUtil.REFERER, "http://xueqiu.com/S/"+code);
		//请求参数
		Map<String,String> params = new HashMap<String,String>();
		params.put("url", "/stock/portfolio/delstock.json");
		params.put("data[code]", code);
		params.put("data[_]", new Date().getTime()+"");
		
		HttpClientUniqueUtil.post("https://xueqiu.com/service/poster",header,params);
		
		System.out.println("删除【"+code+"】完成。");
	}

	private void addStock(String code, String name) throws IOException {
		//请求头
		Map<String,String> header = new HashMap<String,String>();
		header.put(HttpClientUniqueUtil.COOKIE, cookie);
		header.put(HttpClientUniqueUtil.REFERER, "http://xueqiu.com/S/"+code);
		
		//请求参数
		Map<String,String> params = new HashMap<String,String>();
		params.put("code", code);
		params.put("isnotice", 1+"");
		
		HttpClientUniqueUtil.post("https://xueqiu.com/stock/portfolio/addstock.json",header,params);
		
		System.out.println("添加【"+code+","+name+"】完成。");
		
	}
	


	private List<String> query(String group_pid) throws IOException {
		//请求头
		Map<String,String> header = new HashMap<String,String>();
		header.put(HttpClientUniqueUtil.COOKIE, cookie);
		
		String url = getQueryURL(group_pid);
		String result = HttpClientUniqueUtil.get(url,header);
		JSONObject json = JSONObject.fromObject(result);
		JSONArray array = (JSONArray) json.get("stocks");
		List<String> list = new ArrayList<String>();
		for(int i=0;i<array.size();i++){
			JSONObject obj = (JSONObject) array.get(i);
			String code = (String) obj.get("code");
			list.add(code);
		}
		return list;
	}
	
	private String getQueryURL(String group_pid) {
		String type = "2";
		if(group_pid.equals(ALL_GROUP_PID)){
			type = "1";
		}else{
			type = "2";
		}
		long date = new Date().getTime();
		String url = "https://xueqiu.com/v4/stock/portfolio/stocks.json?size=1000&tuid=9631865301&pid="+group_pid+"&category=2&type="+type+"&_="+date;
		return url;
	}


	private String getWritePath() {
		String nowDate = DateUtil.getNowDate();
		String writePath = ProjectUtil.getComputerHomeDir()  + "/" + nowDate + ".EBK";
		return writePath;
	}

	/**
	 * 把当前body中的股票导入雪球自选股中
	 * @param body 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private int uploadBody(ReqBody body) throws  IOException, InterruptedException {
		
		setXueqiuList();
		
		int num = 0;
		for(Stock stock : body.list){
			String code = stock.code;
			String name = stock.name;
			//如果不在自选股中，则添加
			if(!xueqiuList.contains(code)){
				addStock(code,name);
				num++;
			}
			Thread.sleep(Constants.XUEQIU_SLEEP);
		}
		System.out.println("添加股票完成，一共添加了【"+num+"】只股票。");
		return num;
	}


	public String login(String username,String passeword) {
		Map<String,String> result = null;
		//请求头
		Map<String,String> header = new HashMap<String,String>();
		header.put(HttpClientUniqueUtil.X_Requested_With, "XMLHttpRequest");
		
		//请求参数
		Map<String,String> params = new HashMap<String,String>();
		params.put("telephone", username);
		params.put("password", passeword);
		params.put("areacode", "86");
		params.put("remember_me", "on");
		
		try {
			result = HttpClientUniqueUtil.post("https://xueqiu.com/user/login", header, params);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result!=null?result.get(HttpClientUniqueUtil.COOKIE):null;
	}

	//分析个股所在的所有组，然后上传到对应的组中
	public void commitGroup() {
		//个股名：个股所在的组。如：SZ300496：XZ,A2
	    Map<String,String> stockToGroup = new HashMap<String, String>();
	    
	    Set<String> keys = map.keySet();
	    for(String key : keys){
	    	List<Stock> list = map.get(key);
	    	for(Stock stock : list){
	    		String code = stock.code;
	    		String currentGroup = stockToGroup.get(code);
	    		//如果group为空，则添加group；如果group不为空，在group后面追加",GroupName"
	    		if(StringUtil.isEmpty(currentGroup)){
	    			stockToGroup.put(code, key);
	    		}else{
	    			stockToGroup.put(code, currentGroup+","+key);
	    		}
	    	}
	    	
	    }
	    
	    //分析个股所在组完成后，执行添加操作
	    Set<String> codes = stockToGroup.keySet();
	    System.out.println();
	    for(String code : codes){
	    	String groupName = stockToGroup.get(code);
	    	addIntoGroup(code, groupName);
	    }
	    System.out.println("添加到各个分组完成。");
	    
	}

	/**
	 * 下载雪球的各个板块下的个股信息
	 * @return
	 * @throws IOException 
	 */
	public Map<String, List<String>> queryStockWithGroup() throws IOException {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		result.put("ZX", this.query(ZX_GROUP_PID));
		result.put("A2", this.query(A2_GROUP_PID));
		result.put("A3", this.query(A3_GROUP_PID));
		return result;
	}

	

}
