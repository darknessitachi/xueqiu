package web.common;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import web.Constants;
import web.domain.Req;
import web.domain.Stock;
import web.sort.LoadAndPrint;
import web.sort.Worker;

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
			//	load = new SinReqLoad(req);
				break;
			default:
				break;
		}
		
		load.init();
		
	}

	
	/**
	 * һֻ��Ʊ����һ���̣߳���һ���ȽϺõ��̷߳������
	 */
	private void send() {
		for (Stock stock : req.list) {
			switch (businessCode) {
				case Constants.business_sort:
					pool.execute(new Worker(stock, this.req));
					break;
				case Constants.business_single:
				//	pool.execute(new SinWorker(stock, this.req));
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
					System.err.println("��������������ʧ�ܣ������cookie�ļ���");
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
