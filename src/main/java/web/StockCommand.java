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

		// 设置请求path的路径
		this.reqPath = sourcePath + "/" + Req.REQ_SEARCH_NAME;
		// 初始化cookie
		this.cookie = FileUtil.read(sourcePath + "/" + Req.REQ_COOKIE_NAME)
				.trim();
		// 设置请求的股票代码
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(this.reqPath));
			br = new BufferedReader(fr);
			String line = null;
			int number = 0;
			while ((line = br.readLine()) != null) {
				// 如果number为0的话，读取查询日期。否则的话，加载个股信息
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
		// 如果当前行不为空，或者不以#开头，则读取
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

	/**
	 * 按每天为单位进行打印
	 * 
	 * @throws IOException
	 */
	public void printReq() throws IOException {

		for (String key : req.mapKey) {
			System.out.println("——————" + key + " 个股热度——————");
			List<Entity> sortList = new ArrayList<Entity>();
			// 把结果封装在Entity，然后根据number排序
			for (Stock stock : req.list) {
				sortList.add(new Entity(stock.name,
						stock.map.get(key) == null ? 0 : stock.map.get(key)));
			}
			// 排序
			ComparatorEntity comparator = new ComparatorEntity();
			Collections.sort(sortList, comparator);
			// 打印结果，写入文件中
			File folder = new File(Constants.outPath);
			if (!folder.exists()) {
				folder.mkdir();
			}
			File f = new File(Constants.outPath + "/" + key + "个股热度.txt");

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
				// 合计
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
