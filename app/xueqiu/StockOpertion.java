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
	
	
	public void delAll() throws IOException, InterruptedException {
		List<String> stocks = this.queryAll();
		for(String code:stocks){
			delStock(code);
			Thread.sleep(Constants.XUEQIU_SLEEP);
		}
		System.out.println("股票清理完成，一共清理【"+stocks.size()+"】只股票。");
	}

	public void addAll() throws IOException, InterruptedException {
		
		List<String> list = this.queryAll();
		
		//从request_body.txt中获取股票代码（request_body中的股票代码已经过滤指数代码），然后添加
		String reqPath = Constants.classpath + Constants.REQ_BODY_NAME;

		BufferedReader br = null;
		int num = 0;
		try {
			FileReader fr = new FileReader(new File(reqPath));
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 && !line.startsWith("#") &&line.contains(",")) {
					String code = line.split(",")[0];
					String name = line.split(",")[1];
					//如果不在自选股中，则添加
					if(!list.contains(code)){
						addStock(code,name);
						Thread.sleep(Constants.XUEQIU_SLEEP);
						num++;
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
		
		System.out.println("添加股票完成，一共添加了【"+num+"】只股票。");
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
	
	private String getWritePath() {
		String nowDate = DateUtil.getNowDate();
		String writePath = Constants.export  + "/" + nowDate + ".EBK";
		FileUtil.createFolder(Constants.export);
		return writePath;
	}


}
