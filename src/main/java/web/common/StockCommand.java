package web.common;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import web.domain.Req;
import web.domain.Stock;
import web.single.SinReqLoad;
import web.single.SinWorker;
import web.sort.LoadAndPrint;
import web.sort.Worker;
import web.util.Constants;

public class StockCommand {
	
	public static AtomicBoolean isError = new AtomicBoolean(false); 

	private ExecutorService pool = Executors.newFixedThreadPool(4);
	
	private ReqLoad load = null;
	
	private Req req;
	
	private int businessCode;

	public StockCommand(int businessCode) {
		super();
		this.businessCode = businessCode;
	}


	private void init() throws IOException {
		req = new Req();
		
		switch (businessCode) {
			case Constants.business_sort:
				load = new LoadAndPrint(req);
				break;
			case Constants.business_single:
				load = new SinReqLoad(req);
				break;
			default:
				break;
		}
		
		load.init();
		
	}

	
	/**
	 * 一只股票启动一个线程，是一个比较好的线程分配策略
	 */
	private void send() {
		for (Stock stock : req.list) {
			switch (businessCode) {
				case Constants.business_sort:
					pool.execute(new Worker(stock, this.req));
					break;
				case Constants.business_single:
					pool.execute(new SinWorker(stock, this.req));
					break;
				default:
					break;
			}
		}
	}

	private void finish() throws IOException {
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				/*if(!isError.get()){
					load.print();
				}else{
					System.err.println("如果连续多次请求失败，请更新cookie文件。");
				}*/
				load.print();
				break;
			}
		}
	}


	public void start() throws IOException {
		this.init();
		this.send();
		this.finish();
	}

}
