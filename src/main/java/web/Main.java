package web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import web.domain.Entity;
import web.domain.Req;
import web.domain.Stock;
import web.util.ComparatorEntity;
import web.util.Constants;
import web.util.DateUtil;
import web.util.FileUtil;
import web.util.HttpUtil;

public class Main {
	
	private static String reqPath;
	
	private static Req req;

	public static void main(String[] args) throws IOException {
		
		long start = new Date().getTime();
		//收集要查询的股票代码，并进行封装请求
		initReq();
		//发送请求
		sendReq();
		//打印请求结果
		printReq();
		long end = new Date().getTime();
		
		System.out.println("用时："+(end-start)/1000+"秒");
	} 
	/**
	 * 按每天为单位进行打印
	 * @throws IOException 
	 */
	private static void printReq() throws IOException {
		for(String searchDate : req.mapKey){
			System.out.println("——————"+searchDate+" 个股热度——————");
			List<Entity> sortList = new ArrayList<Entity>();
			//把结果封装在Entity，然后根据number排序
			for(Stock stock : req.list){
				sortList.add(new Entity(stock.name,stock.map.get(searchDate)==null?0:stock.map.get(searchDate)));
			}
			//排序
			ComparatorEntity comparator=new ComparatorEntity();
			Collections.sort(sortList, comparator);
			//打印结果，写入文件中
			File folder = new File(Constants.outPath);
			if(!folder.exists()){
				folder.mkdir();
			}
			File f = new File(Constants.outPath+"/"+searchDate+"个股热度.txt");
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for(Entity e : sortList){
				bw.write(e.toString()+"\n");
				System.out.println(e);
			}
			bw.close();
			System.out.println();
		}
	}

	private static void sendReq() {
		for(Stock stock : req.list){
			int page = 1;
			while(true){
				String url = getReqUrl(stock,page);
				String result = HttpUtil.getResult(url,req.cookie);
				//对于返回的结果进行加工
				boolean isFinish = calculate(result,stock);
				if(isFinish){
					break;
				}
				page++;
			}
		}
	}
	/**
	 * 返回true标识结束循环请求
	 * @param result
	 * @param stock
	 * @return
	 */
	private static boolean calculate(String result, Stock stock) {
		JSONObject json = JSONObject.fromObject(result);
		JSONArray array = (JSONArray) json.get("list");
		for(int i=0;i<array.size();i++){
			JSONObject entity = (JSONObject) array.get(i);
			//评论时间
			long time = (Long) entity.get("created_at");
			String timeStr = DateUtil.formatDate(new Date(time), "yyyy-MM-dd");
			//如果timeStr属于mapKey中的一个，就在对应的key上+1，否则的话就退出
			int resultCode = matchMapKey(timeStr);
			if(resultCode == 1){
				Integer num = stock.map.get(timeStr);
				num = num == null ? 0 : num;
				stock.map.put(timeStr, num+1);
			}else if(resultCode == 3){
				return true;
			}
		}
		return false;
	}
	/**
	 * 如果评论时间在mapKey内，返回1(+1)
	 * 如股评论时间比mapKey中最大的还要大（评论时间在mapKey最大和最小之间），返回2(继续遍历)
	 * 如果评论时间比mapKey中最小的还有小，返回3（跳出循环）
	 * @param timeStr
	 * @return
	 */
	
	private static int matchMapKey(String timeStr) {
		String maxKey = req.mapKey.get(0);
		String minKey = req.mapKey.get(req.mapKey.size()-1);
		
		if(req.mapKey.contains(timeStr)){
			return 1;
		}else if(timeStr.compareTo(maxKey)>0){
			return 2;
		}else if(timeStr.compareTo(minKey)<0){
			return 3;
		}else if(timeStr.compareTo(minKey)>0 && timeStr.compareTo(maxKey)<0){
			return 2;
		}
		return 2;
	}

	private static String getReqUrl(Stock stock, int page) {
		String href = "http://xueqiu.com/statuses/search.json?count=15&comment=0&symbol="+stock.code+"&hl=0&source=all&sort=time&page="+page+"&_=1445444564351";
		return href;
	}

	private static void initReq() throws IOException {
		req = new Req();
		//设置请求path的路径
		String sourcePath = Main.class.getClassLoader().getResource("").getPath()+"web/source";
		reqPath = sourcePath+"/req.txt";
		//初始化cookie
		String cookie = FileUtil.read(sourcePath+"/cookie.txt").trim();
		req.cookie = cookie;
		//设置请求的股票代码
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(reqPath));
			br = new BufferedReader(fr);
			String line = null;
			int number = 0;
			while((line = br.readLine()) !=null){
				//如果number为0的话，读取查询日期。否则的话，加载个股信息
				if(number == 0){
					//如果当前行以#开头，则读取当前日期
					if(line.startsWith("#")){
						req.mapKey.add(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
					}else{
						String[] keys = line.split(",");
						for(String key : keys){
							req.mapKey.add(key);
						}
					}
				}else{
					//如果当前行不为空，或者不以#开头，则读取
					if(line.trim().length()>0 && !line.startsWith("#")){
						String[] array = line.split(",");
						req.list.add(new Stock(array[0],array[1]));
					}
				}
				number++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			br.close();
		}
		
	}
	



}
