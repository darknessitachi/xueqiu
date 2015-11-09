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
			//���ڷ��صĽ�����мӹ�
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
	 * ����true��ʶ����ѭ������
	 * @param result
	 * @param stock
	 * @return
	 */
	private boolean calculate(String result, Stock stock) {
		JSONObject json = JSONObject.fromObject(result);
		JSONArray array = (JSONArray) json.get("list");
		for(int i=0;i<array.size();i++){
			JSONObject entity = (JSONObject) array.get(i);
			//����ʱ��
			long time = (Long) entity.get("created_at");
			String createDate = DateUtil.formatDate(new Date(time), "yyyy-MM-dd");
			String createDate_all = DateUtil.formatDate(new Date(time), DateUtil.yyyyMMdd_HHmmss);
			//���timeStr����mapKey�е�һ�������ڶ�Ӧ��key��+1������Ļ����˳�
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
	 * �����жϣ��������ʱ���req.maxDate��Ҫ��Ļ�������2������������
	 * Ȼ���жϣ�
	 * 		�������ʱ����mapKey�ڣ�����1(+1)
	 * 		�������ʱ���mapKey�����Ļ�Ҫ������ʱ����mapKey������С֮�䣩������2(��������)
	 * 		�������ʱ���mapKey����С�Ļ���С������3������ѭ����
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
