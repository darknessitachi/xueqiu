package func.impl;

import java.io.IOException;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.Constants;
import util.DateUtil;
import util.HttpUtil;
import util.ProjectUtil;
import func.domain.Req;
import func.domain.Stock;

public class Worker implements Runnable{

	private Stock stock;
	private Req req;

	public Worker(Stock stock, Req req) {
		this.stock = stock;
		this.req = req;
	}
	public void run() {
		int page = 1;
		while(true){
			
			everySleep();
			String result = getResult(page);
			
			//对于返回的结果进行加工
			if(result != null){
				boolean isFinish = calculate(result,stock);
				if(isFinish){
					System.out.println("【"+stock.name+"】已完成请求。");
					break;
				}
				page++;
			}else{
				break;
			}
		}
	}
	
	
	private String getResult(int page) {
		String result = null;
		String url = ProjectUtil.getSearchUrl(stock,page);
		try {
			result = HttpUtil.getResult(url,req.cookie,Constants.referer_prefix+stock.code);
		} catch (IOException e1) {
			//stock.isError = true;
			System.err.println("【"+stock.name+"】，正在请求第【"+page+"】页，请求过于频繁。");
			req.head.errWaitTime = req.head.errWaitTime + 1;
			try {
				Thread.sleep(req.head.errWaitTime*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.err.println("睡眠了【"+req.head.errWaitTime +"】秒钟，开始重新请求当前页。");
			return getResult(page);
		}
		return result;
	}
	
	
	private void everySleep() {
		//每次请求完开始下一次请求的时候，睡眠一段时间
		if(req.head.sleep != 0){
			try {
				Thread.sleep(req.head.sleep*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 返回true标识结束循环请求
	 * @param result
	 * @param stock
	 * @return
	 */
	private boolean calculate(String result, Stock stock) {
		JSONObject json = JSONObject.fromObject(result);
		JSONArray array = (JSONArray) json.get("list");
		for(int i=0;i<array.size();i++){
			JSONObject entity = (JSONObject) array.get(i);
			//评论时间
			long time = (Long) entity.get("created_at");
			String createDate = DateUtil.formatDate(new Date(time), "yyyy-MM-dd");
			int resultCode = matchMapKey(createDate);
			if(resultCode == 1){
				boolean isNotice = isNotice(entity);
				if(isNotice && req.head.filterNotice){
					continue;
				}else{
					Integer srcNum = stock.result.get(createDate);
					srcNum = srcNum == null ? 0 : srcNum;
					stock.result.put(createDate, srcNum+1);
				}
			}else if(resultCode == 3){
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断当前评论是否公告
	 * @param entity
	 * @return
	 */
	private boolean isNotice(JSONObject entity) {
		String source = (String) entity.get("source");
		if("公告".equals(source)){
			return true;
		}
		return false;
	}

	/**
	 * 首先判断：如果评论时间比req.maxDate还要大的话，返回2（继续遍历）
	 * 然后判断：
	 * 		如果评论时间在mapKey内，返回1(+1)
	 * 		如股评论时间比mapKey中最大的还要大（评论时间在mapKey最大和最小之间），返回2(继续遍历)
	 * 		如果评论时间比mapKey中最小的还有小，返回3（跳出循环）
	 * @param timeStr
	 * @param timeStr_all 
	 * @return
	 */
	private int matchMapKey(String timeStr) {
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

}
