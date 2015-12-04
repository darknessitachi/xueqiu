package web.business;

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

import config.Constants;
import util.ComparatorEntity;
import util.DateUtil;
import util.FileUtil;
import util.StringUtil;
import web.common.ReqLoad;
import web.domain.Entity;
import web.domain.Req;
import web.domain.Stock;

public class LoadAndPrint implements ReqLoad {

	private Req req;
	
	public LoadAndPrint(Req req) {
		this.req = req;
	}

	public void init() {
		
		try {
			initHead();
			initBody();
			initCookie();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	private void initHead() throws IOException {
		// 设置请求path的路径
		String reqPath = Constants.classpath + Constants.REQ_HEAD_NAME;

		// 设置请求的股票代码
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
	
	private void initBody() throws IOException {
		// 设置请求path的路径
		String reqPath = Constants.classpath + Constants.REQ_BODY_NAME;

		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(reqPath));
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 && !line.startsWith("#")) {
					initReqStock(line);
				}
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
	 * 如果maxDate不为空的话，从maxDate向前推N天
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
		req.cookie = FileUtil.read(Constants.classpath + Constants.REQ_COOKIE_NAME).trim();
	}
	
	
	public void print() throws IOException {
		
		if (req.combine) {
			this.combine();
		}
		//创建文件夹
		createFolder();
		
		String fileName = getFileName();
		
		File f = new File(fileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		System.out.println();
		//打印请求错误的股票名
		outMsg(getErrorMsg(),bw);
		
		//遍历打印
		for (String title : req.mapKey) {
			
			outMsg("——————" + title + " 个股热度——————",bw);
			
			List<Entity> sortList = getSortListByKey(title);
			for (Entity e : sortList) {
				if(!e.stock.isError){
					outMsg(e.toString(),bw);
				}
			}
			outMsg("",bw);
		}
		bw.close();
		
	}
	private void createFolder() {
		// 打印结果，写入文件中
		File folder = new File(Constants.outPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	/**
	 * 请求错误的股票名
	 * @return
	 */
	private String getErrorMsg() {
		StringBuilder sb = new StringBuilder();
		for(Stock s : req.list){
			if(s.isError){
				sb.append("【").append(s.name).append("】");
			}
		}
		return sb.toString().length()>0 ? sb.toString()+"请求失败（如果连续多次请求失败，请增加请求间隔睡眠时间，或更新cookie文件）" : "";
	}

	private List<Entity> getSortListByKey(String key) {
		//把结果封装在Entity，然后根据number排序
		List<Entity> sortList = new ArrayList<Entity>();
		for (Stock stock : req.list) {
			sortList.add(new Entity(stock.name,stock.map.get(key) == null ? 0 : stock.map.get(key),stock));
		}
		//排序
		ComparatorEntity comparator = new ComparatorEntity();
		Collections.sort(sortList, comparator);
		return sortList;
	}

	private void outMsg(String msg, BufferedWriter bw) throws IOException {
		System.out.println(msg);
		bw.write(msg + "\n");
	}

	private String getFileName() {
		String nowDate = null;
		if(StringUtil.isEmpty(req.maxDate)){
			nowDate = DateUtil.formatDate(new Date(), DateUtil.yyyyMMdd_HHmmss);
		}else{
			nowDate = req.maxDate;
		}
		nowDate = nowDate.replace(":", "：");
		
		return Constants.outPath + "/"  + nowDate + " "+ StringUtil.number2word((req.mapKey.size()-1))+"天个股热度.txt";
	}

	private void combine() {
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


}
