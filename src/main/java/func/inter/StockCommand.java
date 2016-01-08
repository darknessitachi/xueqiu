package func.inter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import func.domain.Req;
import func.domain.Stock;
import func.impl.ReqLoadImpl;
import func.impl.Worker;

public class StockCommand {
	
	public static AtomicBoolean isError = new AtomicBoolean(false); 
	
	public static AtomicInteger number = new AtomicInteger();
	
	private ExecutorService pool = Executors.newFixedThreadPool(4);
	
	private ReqLoad load = null;

	public StockCommand(Req req) {
		super();
		load = new ReqLoadImpl(req);
	}


	private void init() throws IOException {
		load.init();
	}
	
	private void send() {
		Req req = load.getReq();
		for (Stock stock : req.body.list) {
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
		this.init();
		this.send();
		this.finish();
	}

}
