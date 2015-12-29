package gui.worker;

import gui.core.StockFrame;

import java.io.IOException;
import java.util.List;

import func.translate.MainTrans;
import func.xueqiu.StockOpertion;

public class ImportWorker implements Runnable {

	private List<String> names;
	private StockFrame frame;
	private boolean del = false;

	public ImportWorker(List<String> names, boolean del, StockFrame frame) {
		this.names = names;
		this.frame = frame;
		this.del  = del;
	}

	@Override
	public void run() {
		try {
			StockOpertion oper = new StockOpertion();
			if(del){
				try {
					oper.delAll();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			for(String name : names){
				try {
					MainTrans.translate(name);
					oper.uploadBody();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		frame.displayLabel.setText("上传雪球完成。");
	}

}
