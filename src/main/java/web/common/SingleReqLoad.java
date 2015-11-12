package web.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import web.domain.Req;
import web.domain.Stock;
import web.util.CollectionUtil;
import web.util.Constants;
import web.util.DateUtil;
import web.util.FileUtil;

public class SingleReqLoad implements ReqLoad {
	
	private Req req;
	private String classpath;

	public void init() {
		
		this.classpath = StockCommand.class.getClassLoader().getResource("").getPath();
		
		try {
			initReq();
			initCookie();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void initReq() throws IOException {
		req = new Req();
		// 设置请求path的路径
		String reqFilePath = this.classpath + "web/source/" + Constants.REQ_SINGLE_NAME;

		// 设置请求的股票代码
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(reqFilePath));
			br = new BufferedReader(fr);
			String line = null;
			int number = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 && !line.startsWith("#")) {
					if (number == 0) {
						initReqSleep(line);
					} else if (number == 1) {
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

	
	private void initReqSleep(String line) {
		String[] array = line.split("=");
		req.sleep = Integer.parseInt(array[1]);
	}

	private void initReqStock(String line) {
		String[] array = line.split(",");
		Stock stock = new Stock(array[0], array[1]);
		//加载stock自己的mapKey
		Date beginDate = DateUtil.parse(array[2], DateUtil.yyyyMMdd);
		int day = Integer.parseInt(array[3]);
		for (int i = 0; i < day; i++) {
			String d = DateUtil.add(beginDate,i);
			stock.selfMapKey.add(d);
		}
		//对selfMapKey进行倒序
		stock.selfMapKey = CollectionUtil.reverse(stock.selfMapKey);
		req.list.add(stock);
	}
	
	private void initCookie() {
		// 初始化cookie
		req.cookie = FileUtil.read(this.classpath + "web/source/" + Constants.REQ_COOKIE_NAME).trim();
	}
	
	

	public void setReq(Req req) {
		this.req = req;
	}


	public void print() {
		// TODO Auto-generated method stub
		
	}

}
