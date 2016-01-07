package func.impl;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.ComparatorEntity;
import util.DateUtil;
import util.FileUtil;
import util.StringUtil;
import util.core.AccessUtil;
import func.domain.Entity;
import func.domain.Req;
import func.domain.Stock;
import func.inter.ReqLoad;
import config.Constants;

public class ReqLoadImpl implements ReqLoad {
	
	public Req req;
	
	public ReqLoadImpl() {
		req = new Req();
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
		String reqPath = Constants.classpath + Constants.config_path + Constants.req_head_name;

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
		String reqPath = Constants.classpath + Constants.config_path + Constants.req_body_name;

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
						initBodyName(line);
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
	
	private void initBodyName(String line) {
		if(line.contains(",")){
			System.err.println("request_body文件第一行没有要查询的板块名称。");
			initReqStock(line);
		}else{
			req.bodyName = line;
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
		req.cookie = AccessUtil.readCookie();
	}
	
	
	public void print() throws IOException {
		
		if (req.combine) {
			this.combine();
		}
		//创建子文件夹
		String subFolder = Constants.out_result_path+"/" + DateUtil.formatDate(new Date(), DateUtil.yyyyMMdd);
		FileUtil.createFolde(subFolder);
		
		String errorMsg = getErrorMsg();
		String filepath = getWriteFilePath(subFolder,errorMsg);
		
		File f = new File(filepath);
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		System.out.println();
		//打印请求错误的股票名
		outMsg(errorMsg+"\n",bw);
		
		outMsg("【"+getCodeShowName(req.bodyName)+"】板块的股票总数为【"+req.list.size()+"】个 \n",bw);
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

	private String getCodeShowName(String bodyName) {
		//如果第一个字母是26个英文字母，则截取前两位，否则不截取
		Pattern pattern = Pattern.compile("[a-zA-Z]");  
        Matcher matcher = pattern.matcher(bodyName.subSequence(0, 1));  
        System.out.println();  
        if(matcher.matches()){
        	return bodyName.substring(2);
        }
        return bodyName;
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

	private String getWriteFilePath(String subFolder, String errorMsg) {
		String prefix = StringUtil.isEmpty(errorMsg)?"":"？";
		
		String nowDate = null;
		if(StringUtil.isEmpty(req.maxDate)){
			nowDate = DateUtil.formatDate(new Date(), DateUtil.yyyyMMdd_HHmmss);
		}else{
			nowDate = req.maxDate;
		}
		nowDate = nowDate.replace(":", "：");
		String fileName = nowDate + StringUtil.number2word((req.mapKey.size()-1))+"天个股热度（"+getCodeShowName(req.bodyName)+prefix+"）.txt";
		return subFolder + "/"  + fileName ;
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

	@Override
	public Req getReq() {
		return req;
	}
}
