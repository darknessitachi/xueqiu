package web.common;

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

import web.domain.Entity;
import web.domain.Req;
import web.domain.Stock;
import web.util.ComparatorEntity;
import web.util.Constants;
import web.util.DateUtil;
import web.util.FileUtil;
import web.util.StringUtil;

public class SortReqLoad implements ReqLoad {

	private Req req;
	private String classpath = StockCommand.class.getClassLoader().getResource("").getPath();
	
	public SortReqLoad(Req req) {
		this.req = req;
	}

	public void init() {
		
		try {
			initReq();
			initCookie();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initReq() throws IOException {
		// ��������path��·��
		String reqPath = this.classpath + "web/source/" + Constants.REQ_SORT_NAME;

		// ��������Ĺ�Ʊ����
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(reqPath));
			br = new BufferedReader(fr);
			String line = null;
			int number = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 && !line.startsWith("#")) {
					if (number == 0) {
						initReqNowDate(line);
					} else if (number == 1) {
						initReqKey(line);
					} else if (number == 2) {
						initReqCombine(line);
					}else if (number == 3) {
						initReqSleep(line);
					}else if (number == 4) {
						initReqFilterNotice(line);
					} else {
						initReqStock(line);
					}
				}
				number++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}

	}
	
	private void initReqFilterNotice(String line) {
		String[] array = line.split("=");
		req.filterNotice = new Boolean(array[1]);
	}

	private void initReqNowDate(String line) {
		String[] array = line.split("=");
		req.maxDate = array[1];
	}
	/**
	 * ���maxDate��Ϊ�յĻ�����maxDate��ǰ��N��
	 * @param line
	 */
	private void initReqKey(String line) {
		Date beginDate = null;
		if(StringUtil.isEmpty(req.maxDate)){
			beginDate = new Date();
		}else{
			beginDate = DateUtil.parse(req.maxDate, DateUtil.yyyyMMdd_HHmmss);
		}
		int day = Integer.parseInt(line.split("=")[1]);
		for (int i = 0; i < day; i++) {
			String d = DateUtil.minus(beginDate,i);
			req.mapKey.add(d);
		}
	}
	
	private void initReqCombine(String line) {
		String combine = line.split("=")[1];
		req.combine = new Boolean(combine);
	}
	
	private void initReqSleep(String line) {
		String[] array = line.split("=");
		req.sleep = Integer.parseInt(array[1]);
	}

	private void initReqStock(String line) {
		String[] array = line.split(",");
		req.list.add(new Stock(array[0], array[1]));
	}
	

	private void initCookie() {
		req.cookie = FileUtil.read(this.classpath + "web/source/" + Constants.REQ_COOKIE_NAME).trim();
	}
	
	
	public void print() throws IOException {
		if (req.combine) {
			this.combine();
		}
		// ��ӡ�����д���ļ���
		File folder = new File(Constants.outPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		String nowDate = null;
		if(StringUtil.isEmpty(req.maxDate)){
			nowDate = DateUtil.formatDate(new Date(), DateUtil.yyyyMMdd_HHmmss);
		}else{
			nowDate = req.maxDate;
		}
		nowDate = nowDate.replace(":", "��");
		
		File f = new File(Constants.outPath + "/"  + nowDate + " "+ StringUtil.number2word((req.mapKey.size()-1))+"������ȶ�.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));

		for (String key : req.mapKey) {
			System.out.println("������������" + key + " �����ȶȡ�����������");
			bw.write("������������" + key + " �����ȶȡ�����������" + "\n");
			List<Entity> sortList = new ArrayList<Entity>();
			// �ѽ����װ��Entity��Ȼ�����number����
			for (Stock stock : req.list) {
				sortList.add(new Entity(stock.name,
						stock.map.get(key) == null ? 0 : stock.map.get(key)));
			}
			// ����
			ComparatorEntity comparator = new ComparatorEntity();
			Collections.sort(sortList, comparator);
			
			for (Entity e : sortList) {
				bw.write(e.toString() + "\n");
				System.out.println(e);
			}
			
			System.out.println();
			bw.write("\n");
		}
		bw.close();
	}
	
	private void combine() {
		String combineName = req.mapKey.size() + "����";
		req.mapKey.add(combineName);
		// ������Ʊ������ÿһֻ��Ʊ�������ڵĺϼ�
		for (Stock stock : req.list) {
			Set<String> keys = stock.map.keySet();
			int total = 0;
			for (String key : keys) {
				total = total + stock.map.get(key);
			}
			stock.map.put(combineName, total);
		}
	}


}
