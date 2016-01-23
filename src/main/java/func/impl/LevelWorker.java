package func.impl;

import java.io.IOException;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.Constants;
import util.DateUtil;
import util.HttpUtil;
import func.domain.Req;
import func.domain.Stock;
import func.inter.StockCommand;

public class LevelWorker implements Runnable{

	private Stock stock;
	private Req req;

	public LevelWorker(Stock stock, Req req) {
		this.stock = stock;
		this.req = req;
	}
	public void run() {
		//levels:10,5,3
		for(Integer level : req.head.levels){
			String url = HttpUtil.getSearchUrl(stock,level);
			String result = null;
			try {
				System.out.println("开始请求【"+stock.name+"】第【"+level+"】页。");
				result = HttpUtil.getResult(url,req.cookie,Constants.referer_prefix+stock.code);
			} catch (IOException e1) {
				stock.isError = true;
				StockCommand.isError.set(true);
				System.err.println("【"+stock.name+"】，请求过于频繁正在请求第【"+level+"】页。");
			}
			//对于返回的结果进行加工
			if(result != null){
				boolean isFinish = calculate(result,stock,level);
				if(isFinish){
					System.out.println("【"+stock.name+"】已完成请求。");
					break;
				}
			}
			
			//每次请求完开始下一次请求的时候，睡眠一段时间
			if(req.head.sleep != 0){
				try {
					Thread.sleep(req.head.sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 返回true标识结束循环请求
	 * @param result
	 * @param stock
	 * @param level 
	 * @return
	 */
	private boolean calculate(String result, Stock stock, Integer level) {
		JSONObject json = JSONObject.fromObject(result);
		JSONArray array = (JSONArray) json.get("list");
		JSONObject entity_0 = (JSONObject) array.get(0);
		//评论时间
		long time = (Long) entity_0.get("created_at");
		String createDate = DateUtil.formatDate(new Date(time), "yyyy-MM-dd");
		int resultCode = matchMapKey(createDate);
		if(resultCode == 1){
			stock.result.put(req.head.combineName,level*20);
			return true;
		}
		return false;
	}
	
	/**
	 * 1:符合条件，终止循环
	 * 2：不符合条件，继续向下循环
	 * @param timeStr
	 * @return
	 */
	private int matchMapKey(String createDate) {
		if(req.mapKey.contains(createDate)){
			return 1;
		}
		return 2;
	}

}
