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
import web.util.StringUtil;

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
				line = line.trim();
				// ���numberΪ0�Ļ�����ȡ��ѯ���ڡ�����Ļ������ظ�����Ϣ
				if (number == 0) {
					initReqMaxDate(line);
				} else if (number == 1) {
					initReqMapKey(line);
				} else if (number == 2) {
					initReqCombine(line);
				}else {
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
	
	private void initReqMaxDate(String line) {
		// �����ǰ�в�Ϊ�գ����߲���#��ͷ�����ȡ
		if (line.length() > 0 && !line.startsWith("#")) {
			String[] array = line.split("=");
			req.maxDate = array[1];
		}
	}
	/**
	 * ���maxDate��Ϊ�յĻ�����maxDate��ǰ��N��
	 * @param line
	 */
	private void initReqMapKey(String line) {
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

	private void initReqStock(String line) {
		// �����ǰ�в�Ϊ�գ����߲���#��ͷ�����ȡ
		if (line.length() > 0 && !line.startsWith("#")) {
			String[] array = line.split(",");
			req.list.add(new Stock(array[0], array[1]));
		}
	}
	

	private void initCookie() {
		// ��ʼ��cookie
		req.cookie = FileUtil.read(this.classpath + "web/source/" + Constants.REQ_COOKIE_NAME).trim();
	}
	/**
	 * һֻ��Ʊ����һ���̣߳���һ���ȽϺõ��̷߳������
	 */
	public void send() {
		for (Stock stock : req.list) {
			pool.execute(new Worker(stock, this.req));
		}
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
	
	public void finish() throws IOException {
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				if(!isError.get()){
					if (req.combine) {
						this.combine();
					}
					this.printReq();
				}else{
					System.err.println("��������������ʧ�ܣ������cookie�ļ���");
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

}
