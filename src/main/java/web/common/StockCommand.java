package web.common;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import web.domain.Req;
import web.domain.Stock;
import web.impl.ReqLoadImpl;
import web.impl.Worker;
import config.Constants;

public class StockCommand {
	
	public static AtomicBoolean isError = new AtomicBoolean(false); 
	
	public static AtomicInteger number = new AtomicInteger();
	
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
				load = new ReqLoadImpl(req);
				break;
			case Constants.business_direct:
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
	//	System.out.println("ִ��"+StockCommand.class.getName()+" finish ������");
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
