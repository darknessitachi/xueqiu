package func.impl;

import java.io.BufferedWriter;
import java.io.File;
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
import util.Constants;
import util.DateUtil;
import util.FileUtil;
import util.StringUtil;
import util.core.AccessUtil;
import func.domain.Entity;
import func.domain.Req;
import func.domain.Stock;
import func.inter.ReqLoad;

public class ReqLoadImpl implements ReqLoad {
	
	public Req req;
	
	public ReqLoadImpl(Req req) {
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
		initReqKey(req.head.day);
	}
	
	private void initBody() throws IOException {
		initBodyName(req.body.bodyName);
	}
	
	private void initBodyName(String line) {
		if(line.contains(",")){
			System.err.println("request_body文件第一行没有要查询的板块名称。");
			initReqStock(line);
		}else{
			req.body.bodyName = line;
		}
	}

	/**
	 * 如果maxDate不为空的话，从maxDate向前推N天
	 * @param line
	 */
	private void initReqKey(int day) {
		Date beginDate = new Date();
		for (int i = 0; i < day; i++) {
			String d = DateUtil.minus(beginDate,i);
			req.mapKey.add(d);
		}
	}
	
	private void initReqStock(String line) {
		String[] array = line.split(",");
		req.body.list.add(new Stock(array[0], array[1]));
	}
	

	private void initCookie() {
		req.cookie = AccessUtil.readCookie();
	}
	
	
	public void print() throws IOException {
		
		if (req.head.combine) {
			this.combine();
		}
		//创建子文件夹
		String subFolder = Constants.out_result_path+"/" + DateUtil.formatDate(new Date(), DateUtil.yyyyMMdd);
		FileUtil.createFolder(subFolder);
		
		String errorMsg = getErrorMsg();
		String filepath = getWriteFilePath(subFolder,errorMsg);
		
		File f = new File(filepath);
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		System.out.println();
		//打印请求错误的股票名
		outMsg(errorMsg+"\n",bw);
		
		req.endDate = new Date().getTime();
		long useTimes = (req.endDate - req.startDate)/1000;
		StringBuilder head_msg = new StringBuilder();
		head_msg.append("【"+getCodeShowName(req.body.bodyName)+"】板块的股票总数为【"+req.body.list.size()+"】个，请求耗时【"+useTimes+"】秒 ").append("\n")
		.append("请求参数sleep【"+req.head.sleep+"】毫秒").append("\n")
		.append("请求参数thread个数【"+req.head.threadNum+"】").append("\n");
		outMsg(head_msg.toString(),bw);
		//遍历打印
		for (String title : req.mapKey) {
			
			outMsg("——————" + title + " 个股热度——————",bw);
			
			List<Entity> sortList = getSortListByKey(title);
			int num = 1;
			for (Entity e : sortList) {
				if(!e.stock.isError){
					outMsg(num + "、" + e.toString(),bw);
					num++;
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
		for(Stock s : req.body.list){
			if(s.isError){
				sb.append("【").append(s.name).append("】");
			}
		}
		return sb.toString().length()>0 ? sb.toString()+"请求失败（如果连续多次请求失败，请增加请求间隔睡眠时间，或更新cookie文件）" : "";
	}

	private List<Entity> getSortListByKey(String key) {
		//把结果封装在Entity，然后根据number排序
		List<Entity> sortList = new ArrayList<Entity>();
		for (Stock stock : req.body.list) {
			sortList.add(new Entity(stock.name,stock.result.get(key) == null ? 0 : stock.result.get(key),stock));
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
		
		String nowDate = DateUtil.formatDate(new Date(), DateUtil.yyyyMMdd_HHmmss);
		nowDate = nowDate.replace(":", "：");
		String fileName = nowDate + StringUtil.number2word((req.mapKey.size()-1))+"天个股热度（"+getCodeShowName(req.body.bodyName)+prefix+"）.txt";
		return subFolder + "/"  + fileName ;
	}

	private void combine() {
		String combineName = req.mapKey.size() + "天内";
		req.mapKey.add(combineName);
		// 遍历股票，计算每一只股票所有周期的合计
		for (Stock stock : req.body.list) {
			Set<String> keys = stock.result.keySet();
			int total = 0;
			for (String key : keys) {
				total = total + stock.result.get(key);
			}
			stock.result.put(combineName, total);
		}
	}

	@Override
	public Req getReq() {
		return req;
	}
}
