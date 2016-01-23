package func.inter;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import func.domain.Req;
import func.domain.Stock;
import func.impl.ReqLoadImpl;
import func.impl.Worker;

public class StockCommand {
	
	private ExecutorService pool = null;
	
	private ReqLoad load = null;

	public StockCommand(Req req) {
		super();
		pool = Executors.newFixedThreadPool(req.head.threadNum);
		load = new ReqLoadImpl(req);
	}

	private void init() throws IOException {
		load.init();
	}
	
	private void send() {
		Req req = load.getReq();
		
		for(Stock stock : req.body.list) {
			pool.execute(new Worker(stock, req));
		}
	}
	
	private void finish() throws IOException {
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				load.print();
				break;
			}
		}
	}


	public void start() throws IOException {
		load.getReq().startDate = new Date().getTime();
		this.init();
		this.send();
		this.finish();
		System.out.println("用时："+ (load.getReq().endDate - load.getReq().startDate)/1000 +"秒");
	}

}
