package gui.worker;

import func.xueqiu.StockOpertion;
import gui.core.StockFrame;

import java.io.IOException;
import java.util.List;

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
		oper.cancelAllGroup();
		for(String name : names){
			try {
				oper.uploadFileToGroup(groupName,name);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		frame.displayLabel.setText("上传分组完成。");
	}

}
