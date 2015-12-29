package gui.worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.List;

import func.translate.MainTrans;
import func.xueqiu.StockOpertion;

public class ImportWorkerGroup implements Runnable {

	private List<String> names;
	private StockFrame frame;
	private String groupName;

	public ImportWorkerGroup(List<String> names, String groupName, StockFrame frame) {
		this.names = names;
		this.groupName = groupName;
		this.frame = frame;
	}

	@Override
	public void run() {
		StockOpertion oper = new StockOpertion();
		oper.cancelGroupAll();
		for(String name : names){
			try {
				MainTrans.translate(name);
				oper.updateGroup(groupName);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		frame.displayLabel.setText("上传分组完成。");
	}

}
