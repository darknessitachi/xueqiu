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
		// 设置请求path的路径
		String reqPath = this.classpath + "web/source/" + Constants.REQ_SEARCH_NAME;

		// 设置请求的股票代码
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(reqPath));
			br = new BufferedReader(fr);
			String line = null;
			int number = 0;
			while ((line = br.readLine()) != null) {
				// 如果number为0的话，读取查询日期。否则的话，加载个股信息
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
		// 如果当前行不为空，或者不以#开头，则读取
		if (line.trim().length() > 0 && !line.startsWith("#")) {
			String[] array = line.split(",");
			req.list.add(new Stock(array[0], array[1]));
		}
	}
	

	private void initCookie() {
		// 初始化cookie
		req.cookie = FileUtil.read(this.classpath + "web/source/" + Constants.REQ_COOKIE_NAME).trim();
	}

	public void sendReq() {
		for (Stock stock : req.list) {
			pool.execute(new Worker(stock, this.req, "http://xueqiu.com/S/"
					+ stock.code));
		}
	}

	public void combine() {
		String combineName = req.mapKey.size() + "天内";
		req.mapKey.add(combineName);
		// 遍历股票，计算每一只股票所有周期的合计
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
	 * 按每天为单位进行打印
	 * 
	 * @throws IOException
	 */
	private void printReq() throws IOException {
		// 打印结果，写入文件中
		File folder = new File(Constants.outPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		String nowDate = DateUtil.formatDate(new Date(), DateUtil.yyyyMMdd_HHmmss2);
		File f = new File(Constants.outPath + "/"  + nowDate + " "+(req.mapKey.size()-1)+"天内个股热度.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));

		for (String key : req.mapKey) {
			System.out.println("——————" + key + " 个股热度——————");
			bw.write("——————" + key + " 个股热度——————" + "\n");
			List<Entity> sortList = new ArrayList<Entity>();
			// 把结果封装在Entity，然后根据number排序
			for (Stock stock : req.list) {
				sortList.add(new Entity(stock.name,
						stock.map.get(key) == null ? 0 : stock.map.get(key)));
			}
			// 排序
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
