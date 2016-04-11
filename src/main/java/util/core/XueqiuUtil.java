package util.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.Constants;
import util.DateUtil;
import util.FileUtil;
import util.StringUtil;
import util.http.HttpClientUniqueUtil;
import util.http.HttpUtil;
import func.domain.Req.ReqBody;
import func.domain.Stock;

public class XueqiuUtil {
	
	private String cookie = AccessUtil.readCookie();
	
	//当前雪球list
	private List<String> xueqiuList = null;
	
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
				return this.queryAll().size();
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
		List<String> stocks = this.queryAll();
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


	public void uploadFileToGroup(String groupName, String name) throws IOException, InterruptedException {
		ReqBody body = TranslateUtil.translate(name);
		uploadBodyToGroup(body,groupName);
	}
		
	/**
	 * 取消所有股票的分组
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void cancelAllGroup() {
		try {
			setXueqiuList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//把之前的分组的股票取消
		for(String code : xueqiuList){
			try {
				updateStockGroup("", code);
				Thread.sleep(Constants.XUEQIU_SLEEP);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void export() throws IOException{
		List<String> list = this.queryAll();
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
			this.xueqiuList = this.queryAll();
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
	
	/**
	 * 添加股票到分组
	 * @param groupName 
	 * @throws IOException 
	 */
	private void updateStockGroup(String groupName,String code) throws IOException {
		Map<String,String> params = new HashMap<String,String>();
		params.put("pnames", groupName);
		params.put("symbol", code);
		params.put("category", 2+"");
		HttpUtil.post("http://xueqiu.com/v4/stock/portfolio/updstock.json",params,cookie,"http://xueqiu.com/S/"+code);
		if(StringUtil.isEmpty(groupName)){
			System.out.println("【"+code+"】从分组中删除。");
		}else{
			System.out.println("【"+code+"】添加到分组【"+groupName+"】完成。");
		}
	}


	private List<String> queryAll() throws IOException {
		//请求头
		Map<String,String> header = new HashMap<String,String>();
		header.put(HttpClientUniqueUtil.COOKIE, cookie);
		
		String result = HttpClientUniqueUtil.get("http://xueqiu.com/v4/stock/portfolio/stocks.json?size=1000&tuid=9631865301&pid=-1&category=2&type=5",header);
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
	
	/**
	 * 把当前body中的股票导入雪球自选股的分组中
	 * @param body 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void uploadBodyToGroup(ReqBody body, String groupName) throws IOException, InterruptedException {
		//先添加股票
		uploadBody(body);
		//添加股票到分组
		for(Stock stock : body.list){
			String code = stock.code;
			updateStockGroup(groupName, code);
			Thread.sleep(Constants.XUEQIU_SLEEP);
		}
		System.out.println("添加分组完成，分组【"+groupName+"】一共添加了【"+body.list.size()+"】只股票，总共【"+xueqiuList.size()+"】只股票。");
	}

}
