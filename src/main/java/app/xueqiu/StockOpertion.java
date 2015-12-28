package app.xueqiu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.DateUtil;
import util.FileUtil;
import util.HttpUtil;
import util.StringUtil;
import config.Constants;

public class StockOpertion {
	
	private String cookie = FileUtil.read(Constants.classpath + Constants.REQ_COOKIE_NAME).trim();
	
	//格式：code,name
	private List<String> bodyList = null;
	
	//当前雪球list
	private List<String> list = null;
	
	
	public void delAll() throws IOException, InterruptedException {
		List<String> stocks = this.queryAll();
		for(String code:stocks){
			delStock(code);
			Thread.sleep(Constants.XUEQIU_SLEEP);
		}
		System.out.println("股票清理完成，一共清理【"+stocks.size()+"】只股票。");
	}
	/**
	 * 把当前body中的股票导入雪球自选股中
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void addAllBody() throws  IOException, InterruptedException {
		
		setXueqiuList();
		setBodyList();
		
		int num = 0;
		for(String str : bodyList){
			String code = str.split(",")[0];
			String name = str.split(",")[1];
			//如果不在自选股中，则添加
			if(!list.contains(code)){
				addStock(code,name);
				num++;
			}
			Thread.sleep(Constants.XUEQIU_SLEEP);
		}
		System.out.println("添加股票完成，一共添加了【"+num+"】只股票。");
	}
		
	/**
	 * 取消所有股票的分组
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void cancelGroupAll() {
		try {
			setXueqiuList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//把之前的分组的股票取消
		for(String code : list){
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
	
	/**
	 * 把当前body中的股票导入雪球自选股的分组中
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void updateGroup(String groupName) throws IOException, InterruptedException {
		//先添加股票
		addAllBody();
		//添加股票到分组
		for(String str : bodyList){
			String code = str.split(",")[0];
			updateStockGroup(groupName, code);
			Thread.sleep(Constants.XUEQIU_SLEEP);
		}
		System.out.println("添加分组完成，分组【"+groupName+"】一共添加了【"+bodyList.size()+"】只股票。");
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
	
	
	private void setBodyList() throws IOException {
		if(this.bodyList == null){
			this.bodyList = this.readBody();
		}
	}
	
	private void setXueqiuList() throws IOException {
		if(this.list == null){
			this.list = this.queryAll();
		}
	}

	/**
	 * 1、读取body
	 * 2、股票格式："code,name"
	 * @return
	 * @throws IOException 
	 */
	private List<String> readBody() throws IOException{
		
		List<String> result = new ArrayList<String>();
		
		//从request_body.txt中获取股票代码（request_body中的股票代码已经过滤指数代码），然后添加
		String reqPath = Constants.classpath + Constants.REQ_BODY_NAME;

		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(reqPath));
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 && !line.startsWith("#") &&line.contains(",")) {
					String code = line.split(",")[0];
					String name = line.split(",")[1];
					result.add(code+","+name);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
		
		return result;
	}
	
	
	private void delStock(String code) throws IOException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("code", code);
		params.put("_", new Date().getTime());
		HttpUtil.get("http://xueqiu.com/stock/portfolio/delstock.json",params,cookie ,"http://xueqiu.com/S/"+code);
		System.out.println("删除【"+code+"】完成。");
	}

	private void addStock(String code, String name) throws IOException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("code", code);
		params.put("isnotice", 1);
		HttpUtil.post("http://xueqiu.com/stock/portfolio/addstock.json",params,cookie,"http://xueqiu.com/S/"+code);
		System.out.println("添加【"+code+","+name+"】完成。");
	}
	
	/**
	 * 添加股票到分组
	 * @param groupName 
	 * @throws IOException 
	 */
	private void updateStockGroup(String groupName,String code) throws IOException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("pnames", groupName);
		params.put("symbol", code);
		params.put("category", 2);
		HttpUtil.post("http://xueqiu.com/v4/stock/portfolio/updstock.json",params,cookie,"http://xueqiu.com/S/"+code);
		if(StringUtil.isEmpty(groupName)){
			System.out.println("【"+code+"】从分组中删除。");
		}else{
			System.out.println("【"+code+"】添加到分组【"+groupName+"】完成。");
		}
	}


	private List<String> queryAll() throws IOException {
		String result = HttpUtil.getResult("http://xueqiu.com/v4/stock/portfolio/stocks.json?size=1000&tuid=9631865301&pid=-1&category=2&type=5",cookie,"http://xueqiu.com/9631865301","utf-8");
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
		String writePath = Constants.export  + "/" + nowDate + ".EBK";
		FileUtil.createFolder(Constants.export);
		return writePath;
	}

	


}
