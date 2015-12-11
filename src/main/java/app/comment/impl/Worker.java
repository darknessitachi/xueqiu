package app.comment.impl;

import java.io.IOException;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.DateUtil;
import util.HttpUtil;
import util.StringUtil;

import app.comment.common.StockCommand;
import app.comment.domain.Req;
import app.comment.domain.Stock;

import config.Constants;

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
			String url = HttpUtil.getSearchUrl(stock,page);
			String result = null;
			try {
				//System.out.println("��ʼ����"+stock.name+"���ڡ�"+page+"��ҳ��");
				result = HttpUtil.getResult(url,req.cookie,Constants.referer_prefix+stock.code);
			} catch (IOException e1) {
				stock.isError = true;
				StockCommand.isError.set(true);
				System.err.println("��"+stock.name+"�����������Ƶ����������ڡ�"+page+"��ҳ��");
			}
			//���ڷ��صĽ�����мӹ�
			if(result != null){
				boolean isFinish = calculate(result,stock);
				if(isFinish){
					//int num = StockCommand.number.incrementAndGet();
					System.out.println("��"+stock.name+"�����������");
					break;
				}
				page++;
			}else{
				break;
			}
			//ÿ�������꿪ʼ��һ�������ʱ��˯��һ��ʱ��
			if(req.sleep != 0){
				try {
					Thread.sleep(req.sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
			int resultCode = matchMapKey(createDate,createDate_all);
			if(resultCode == 1){
				boolean isNotice = isNotice(entity);
				if(isNotice && req.filterNotice){
					continue;
				}else{
					Integer srcNum = stock.map.get(createDate);
					srcNum = srcNum == null ? 0 : srcNum;
					stock.map.put(createDate, srcNum+1);
				}
			}else if(resultCode == 3){
				return true;
			}
		}
		return false;
	}
	/**
	 * �жϵ�ǰ�����Ƿ񹫸�
	 * @param entity
	 * @return
	 */
	private boolean isNotice(JSONObject entity) {
		String source = (String) entity.get("source");
		if("����".equals(source)){
			return true;
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
