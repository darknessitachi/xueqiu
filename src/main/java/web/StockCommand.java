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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import web.domain.Entity;
import web.domain.Req;
import web.domain.Stock;
import web.util.ComparatorEntity;
import web.util.Constants;
import web.util.DateUtil;
import web.util.FileUtil;

public class StockCommand {
	
	public static AtomicBoolean isError = new AtomicBoolean(false); 

	ExecutorService pool = Executors.newFixedThreadPool(4);
	
	private String classpath;

	private Req req;

	public void init() throws IOException {

		this.classpath = StockCommand.class.getClassLoader().getResource("").getPath();

		initReq();
		initCookie();
	}

	private void initReq() throws IOException {
		req = new Req();
		// ��������path��·��
		String reqPath = this.classpath + "web/source/" + Constants.REQ_SEARCH_NAME;

		// ��������Ĺ�Ʊ����
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(reqPath));
			br = new BufferedReader(fr);
			String line = null;
			int number = 0;
			while ((line = br.readLine()) != null) {
				// ���numberΪ0�Ļ�����ȡ��ѯ���ڡ�����Ļ������ظ�����Ϣ
				if (number == 0) {
					initReqMapKey(line);
				} else if (number == 1) {
					initReqCombine(line);
				} else {
					initReqStock(line);
				}
				number++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}

	}
	
	private void initReqMapKey(String line) {
		int day = Integer.parseInt(line.split("=")[1]);
		for (int i = 0; i < day; i++) {
			String d = DateUtil.minus(i);
			req.mapKey.add(d);
		}
	}
	
	private void initReqCombine(String line) {
		req.combine = new Boolean(line.trim());
	}

	private void initReqStock(String line) {
		// �����ǰ�в�Ϊ�գ����߲���#��ͷ�����ȡ
		if (line.trim().length() > 0 && !line.startsWith("#")) {
			String[] array = line.split(",");
			req.list.add(new Stock(array[0], array[1]));
		}
	}
	

	private void initCookie() {
		// ��ʼ��cookie
		req.cookie = FileUtil.read(this.classpath + "web/source/" + Constants.REQ_COOKIE_NAME).trim();
	}

	public void sendReq() {
		for (Stock stock : req.list) {
			pool.execute(new Worker(stock, this.req, "http://xueqiu.com/S/"
					+ stock.code));
		}
	}

	public void combine() {
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
	
	public void finish() throws IOException {
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				if(!isError.get()){
					if (req.combine) {
						this.combine();
					}
					this.printReq();
				}
				break;
			}
		}
	}

	/**
	 * ��ÿ��Ϊ��λ���д�ӡ
	 * 
	 * @throws IOException
	 */
	private void printReq() throws IOException {
		// ��ӡ�����д���ļ���
		File folder = new File(Constants.outPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		String nowDate = DateUtil.formatDate(new Date(), DateUtil.yyyyMMdd_HHmmss2);
		File f = new File(Constants.outPath + "/"  + nowDate + " "+(req.mapKey.size()-1)+"���ڸ����ȶ�.txt");
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

}
