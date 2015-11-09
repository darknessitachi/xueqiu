package web;

import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import web.domain.Req;
import web.domain.Stock;
import web.util.Constants;
import web.util.DateUtil;
import web.util.HttpUtil;
import web.util.StringUtil;

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
			String url = HttpUtil.getReqUrl(stock,page);
			String result = HttpUtil.getResult(url,req.cookie,Constants.referer_prefix+stock.code);
			//对于返回的结果进行加工
			if(result != null){
				boolean isFinish = calculate(result,stock);
				if(isFinish){
					break;
				}
				page++;
			}else{
				break;
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
			String createDate_all = DateUtil.formatDate(new Date(time), DateUtil.yyyyMMdd_HHmmss);
			//如果timeStr属于mapKey中的一个，就在对应的key上+1，否则的话就退出
			int resultCode = matchMapKey(createDate,createDate_all);
			if(resultCode == 1){
				Integer num = stock.map.get(createDate);
				num = num == null ? 0 : num;
				stock.map.put(createDate, num+1);
			}else if(resultCode == 3){
				return true;
			}
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
	private int matchMapKey(String timeStr, String timeStr_all) {
		String maxKey = req.mapKey.get(0);
		String minKey = req.mapKey.get(req.mapKey.size()-1);
		
		if(!StringUtil.isEmpty(req.maxDate) && timeStr_all.compareTo(req.maxDate)>0){
			return 2;
		}
		
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
