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
import java.util.Set;

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

public class StockCommand {
	
	private Req req;
	
	private String reqPath;
	
	private String cookie;
	
	public void initReq() throws IOException {
		req = new Req();
		String sourcePath = StockCommand.class.getClassLoader().getResource("").getPath()+"web/source";
		
		//��������path��·��
		this.reqPath = sourcePath+"/"+Req.REQ_SEARCH_NAME;
		//��ʼ��cookie
		this.cookie = FileUtil.read(sourcePath+"/"+Req.REQ_COOKIE_NAME).trim();
		//��������Ĺ�Ʊ����
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(this.reqPath));
			br = new BufferedReader(fr);
			String line = null;
			int number = 0;
			while((line = br.readLine()) !=null){
				//���numberΪ0�Ļ�����ȡ��ѯ���ڡ�����Ļ������ظ�����Ϣ
				if(number == 0){
					//�����ǰ����#��ͷ�����ȡ��ǰ����
					if(line.startsWith("#")){
						req.mapKey.add(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
					}else{
						String[] keys = line.split(",");
						for(String key : keys){
							req.mapKey.add(key);
						}
					}
				}else{
					//�����ǰ�в�Ϊ�գ����߲���#��ͷ�����ȡ
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
	
	public void sendReq() {
		for(Stock stock : req.list){
			int page = 1;
			while(true){
				String url = getReqUrl(stock,page);
				String result = HttpUtil.getResult(url,this.cookie);
				//���ڷ��صĽ�����мӹ�
				boolean isFinish = calculate(result,stock);
				if(isFinish){
					break;
				}
				page++;
			}
		}
	}
	
	
	public void combine() {
		String combineName = req.mapKey.size()+"����";
		req.mapKey.add(combineName);
		//������Ʊ������ÿһֻ��Ʊ�������ڵĺϼ�
		for(Stock stock : req.list){
			Set<String> keys = stock.map.keySet();
			int total = 0;
			for(String key : keys){
				total = total + stock.map.get(key);
			}
			stock.map.put(combineName, total);
		}
	}
	
	
	/**
	 * ��ÿ��Ϊ��λ���д�ӡ
	 * @throws IOException 
	 */
	public void printReq() throws IOException {
		for(String key : req.mapKey){
			System.out.println("������������"+key+" �����ȶȡ�����������");
			List<Entity> sortList = new ArrayList<Entity>();
			//�ѽ����װ��Entity��Ȼ�����number����
			for(Stock stock : req.list){
				sortList.add(new Entity(stock.name,stock.map.get(key)==null?0:stock.map.get(key)));
			}
			//����
			ComparatorEntity comparator=new ComparatorEntity();
			Collections.sort(sortList, comparator);
			//��ӡ�����д���ļ���
			File folder = new File(Constants.outPath);
			if(!folder.exists()){
				folder.mkdir();
			}
			File f = new File(Constants.outPath+"/"+key+"�����ȶ�.txt");
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for(Entity e : sortList){
				bw.write(e.toString()+"\n");
				System.out.println(e);
			}
			bw.close();
			System.out.println();
		}
	}
	
	private String getReqUrl(Stock stock, int page) {
		String href = "http://xueqiu.com/statuses/search.json?count=15&comment=0&symbol="+stock.code+"&hl=0&source=all&sort=time&page="+page+"&_=1445444564351";
		return href;
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
			String timeStr = DateUtil.formatDate(new Date(time), "yyyy-MM-dd");
			//���timeStr����mapKey�е�һ�������ڶ�Ӧ��key��+1������Ļ����˳�
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
	 * �������ʱ����mapKey�ڣ�����1(+1)
	 * �������ʱ���mapKey�����Ļ�Ҫ������ʱ����mapKey������С֮�䣩������2(��������)
	 * �������ʱ���mapKey����С�Ļ���С������3������ѭ����
	 * @param timeStr
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
