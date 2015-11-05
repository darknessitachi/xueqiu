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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import web.domain.Entity;
import web.domain.Req;
import web.domain.Stock;
import web.util.ComparatorEntity;
import web.util.Constants;
import web.util.DateUtil;
import web.util.FileUtil;

public class StockCommand {

	ExecutorService pool = Executors.newFixedThreadPool(4);

	private Req req;

	private String reqPath;

	private String cookie;

	private boolean combine = false;

	public void initReq() throws IOException {
		req = new Req();
		String sourcePath = StockCommand.class.getClassLoader().getResource("")
				.getPath()
				+ "web/source";

		// ��������path��·��
		this.reqPath = sourcePath + "/" + Req.REQ_SEARCH_NAME;
		// ��ʼ��cookie
		this.cookie = FileUtil.read(sourcePath + "/" + Req.REQ_COOKIE_NAME)
				.trim();
		// ��������Ĺ�Ʊ����
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(this.reqPath));
			br = new BufferedReader(fr);
			String line = null;
			int number = 0;
			while ((line = br.readLine()) != null) {
				// ���numberΪ0�Ļ�����ȡ��ѯ���ڡ�����Ļ������ظ�����Ϣ
				if (number == 0) {
					initMapKey(line);
				} else if(number == 1){
					initCombine(line);
				}else{
					initStock(line);
				}
				number++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
	}

	private void initCombine(String line) {
		this.combine = new Boolean(line.trim());
	}

	private void initStock(String line) {
		// �����ǰ�в�Ϊ�գ����߲���#��ͷ�����ȡ
		if (line.trim().length() > 0 && !line.startsWith("#")) {
			String[] array = line.split(",");
			req.list.add(new Stock(array[0], array[1]));
		}
	}

	private void initMapKey(String line) {
		int day = Integer.parseInt(line.split("=")[1]);
		for(int i=0;i<day;i++){
			String d = DateUtil.minus(i);
			req.mapKey.add(d);
		}
	}

	public void sendReq() {
		for (Stock stock : req.list) {
			pool.execute(new Worker(stock, this.cookie, this.req));
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

	/**
	 * ��ÿ��Ϊ��λ���д�ӡ
	 * 
	 * @throws IOException
	 */
	public void printReq() throws IOException {

		for (String key : req.mapKey) {
			System.out.println("������������" + key + " �����ȶȡ�����������");
			List<Entity> sortList = new ArrayList<Entity>();
			// �ѽ����װ��Entity��Ȼ�����number����
			for (Stock stock : req.list) {
				sortList.add(new Entity(stock.name,
						stock.map.get(key) == null ? 0 : stock.map.get(key)));
			}
			// ����
			ComparatorEntity comparator = new ComparatorEntity();
			Collections.sort(sortList, comparator);
			// ��ӡ�����д���ļ���
			File folder = new File(Constants.outPath);
			if (!folder.exists()) {
				folder.mkdir();
			}
			File f = new File(Constants.outPath + "/" + key + "�����ȶ�.txt");

			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (Entity e : sortList) {
				bw.write(e.toString() + "\n");
				System.out.println(e);
			}
			bw.close();
			System.out.println();
		}
	}

	public void finish() throws IOException {
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				// �ϼ�
				if(this.combine){
					this.combine();
				}
				this.printReq();
				break;
			}
		}
	}

	public void setCombine(boolean b) {
		this.combine = b;
		
	}

}
