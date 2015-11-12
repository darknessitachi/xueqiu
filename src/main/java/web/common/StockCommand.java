package web.common;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import web.domain.Req;
import web.domain.Stock;
import web.single.SingleReqLoad;
import web.single.SingleWorker;
import web.sort.SortReqLoad;
import web.sort.SortWorker;
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


	public void init() throws IOException {
		req = new Req();
		
		switch (businessCode) {
			case Constants.business_sort:
				load = new SortReqLoad(req);
				break;
			case Constants.business_single:
				load = new SingleReqLoad(req);
				break;
			default:
				break;
		}
		
		load.init();
		
	}

	
	/**
	 * 一只股票启动一个线程，是一个比较好的线程分配策略
	 */
	public void send() {
		for (Stock stock : req.list) {
			switch (businessCode) {
				case Constants.business_sort:
					pool.execute(new SortWorker(stock, this.req));
					break;
				case Constants.business_single:
					pool.execute(new SingleWorker(stock, this.req));
					break;
				default:
					break;
			}
		}
	}

	public void finish() throws IOException {
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				if(!isError.get()){
					load.print();
				}else{
					System.err.println("如果连续多次请求失败，请更新cookie文件。");
				}
				break;
			}
		}
	}

}
