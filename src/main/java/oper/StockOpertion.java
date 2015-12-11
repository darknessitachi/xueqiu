package oper;

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
import util.FileUtil;
import util.HttpUtil;
import config.Constants;

public class StockOpertion {
	

	private String cookie = FileUtil.read(Constants.classpath + Constants.REQ_COOKIE_NAME).trim();
	
	private long sleep = 100;

	private void delStock(String code) throws IOException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("code", code);
		params.put("_", new Date().getTime());
		HttpUtil.get("http://xueqiu.com/stock/portfolio/delstock.json",params,cookie ,"http://xueqiu.com/S/"+code);
		System.out.println("ɾ����"+code+"����ɡ�");
	}

	private void addStock(String code) throws IOException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("code", code);
		params.put("isnotice", 1);
		HttpUtil.post("http://xueqiu.com/stock/portfolio/addstock.json",params,cookie,"http://xueqiu.com/S/"+code);
		System.out.println("��ӡ�"+code+"����ɡ�");
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
			Thread.sleep(sleep);
		}
		System.out.println("��Ʊ������ɣ�һ������"+stocks.size()+"��ֻ��Ʊ��");
	}

	public void addAll() throws IOException, InterruptedException {
		
		List<String> list = this.queryAll();
		
		//��request_body.txt�л�ȡ��Ʊ���루request_body�еĹ�Ʊ�����Ѿ�����ָ�����룩��Ȼ�����
		String reqPath = Constants.classpath + Constants.REQ_BODY_NAME;

		BufferedReader br = null;
		int num = 0;
		try {
			FileReader fr = new FileReader(new File(reqPath));
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 && !line.startsWith("#")) {
					String code = line.split(",")[0];
					//���������ѡ���У������
					if(!list.contains(code)){
						addStock(code);
						Thread.sleep(sleep);
						num++;
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
		
		System.out.println("��ӹ�Ʊ��ɣ�һ������ˡ�"+num+"��ֻ��Ʊ��");
	}

}
