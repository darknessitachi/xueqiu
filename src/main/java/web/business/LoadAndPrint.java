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
		// ��������path��·��
		String reqPath = Constants.classpath + Constants.REQ_HEAD_NAME;

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
		// ��������path��·��
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
		req.cookie = FileUtil.read(Constants.classpath + Constants.REQ_COOKIE_NAME).trim();
	}
	
	
	public void print() throws IOException {
		
		if (req.combine) {
			this.combine();
		}
		//�����ļ���
		createFolder();
		
		String fileName = getFileName();
		
		File f = new File(fileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		System.out.println();
		//��ӡ�������Ĺ�Ʊ��
		outMsg(getErrorMsg(),bw);
		
		//������ӡ
		for (String title : req.mapKey) {
			
			outMsg("������������" + title + " �����ȶȡ�����������",bw);
			
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
		// ��ӡ�����д���ļ���
		File folder = new File(Constants.outPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	/**
	 * �������Ĺ�Ʊ��
	 * @return
	 */
	private String getErrorMsg() {
		StringBuilder sb = new StringBuilder();
		for(Stock s : req.list){
			if(s.isError){
				sb.append("��").append(s.name).append("��");
			}
		}
		return sb.toString().length()>0 ? sb.toString()+"����ʧ�ܣ���������������ʧ�ܣ�������������˯��ʱ�䣬�����cookie�ļ���" : "";
	}

	private List<Entity> getSortListByKey(String key) {
		//�ѽ����װ��Entity��Ȼ�����number����
		List<Entity> sortList = new ArrayList<Entity>();
		for (Stock stock : req.list) {
			sortList.add(new Entity(stock.name,stock.map.get(key) == null ? 0 : stock.map.get(key),stock));
		}
		//����
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
		nowDate = nowDate.replace(":", "��");
		
		return Constants.outPath + "/"  + nowDate + " "+ StringUtil.number2word((req.mapKey.size()-1))+"������ȶ�.txt";
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
