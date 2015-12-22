package gui.worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.List;

import app.translate.MainTrans;
import app.xueqiu.StockOpertion;

public class ImportWorker implements Runnable {

	private List<String> names;
	private StockFrame frame;

	public ImportWorker(List<String> names, StockFrame frame) {
		this.names = names;
		this.frame = frame;
	}

	@Override
	public void run() {
		try {
			StockOpertion oper = new StockOpertion();
			/*try {
				oper.delAll();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			for(String name : names){
				try {
					MainTrans.translate(name);
					oper.addAll();
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
