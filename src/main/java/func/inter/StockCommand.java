package func.inter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import func.domain.Req;
import func.domain.Stock;
import func.impl.LevelWorker;
import func.impl.ReqLoadImpl;
import func.impl.Worker;

public class StockCommand {
	
	public static AtomicBoolean isError = new AtomicBoolean(false); 
	
	public static AtomicInteger number = new AtomicInteger();
	
	private ExecutorService pool = null;
	
	private ReqLoad load = null;

	private boolean isLevels;

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
		this.isLevels = isLevels(req);
		
		if(isLevels){
			req.head.combine = false;
			req.head.combineName = req.head.day+"天内热度";
			for(Stock stock : req.body.list) {
				pool.execute(new LevelWorker(stock, req));
			}
		}else{
			for(Stock stock : req.body.list) {
				pool.execute(new Worker(stock, req));
			}
		}
	}
	
	private boolean isLevels(Req req) {
		return req.head.levels.size() == 0 ? false : true;
	}

	private void finish() throws IOException {
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				preFinish();
				load.print();
				break;
			}
		}
	}

	private void preFinish() {
		if(this.isLevels){
			load.getReq().mapKey.clear();
			load.getReq().mapKey.add(load.getReq().head.combineName);
		}
	}

	public void start() throws IOException {
		this.init();
		this.send();
		this.finish();
	}

}
