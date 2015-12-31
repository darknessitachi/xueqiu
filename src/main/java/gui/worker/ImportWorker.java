package gui.worker;

import func.util.XueqiuUtil;
import gui.core.StockFrame;

import java.io.IOException;
import java.util.List;

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
		int num = 0;
		XueqiuUtil oper = new XueqiuUtil();
		try {
			if(del){
				try {
					oper.delAll();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			for(String name : names){
				try {
					num = num + oper.uploadFile(name);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		frame.displayLabel.setText("上传雪球完成，添加【"+num+"】只股票，共【"+oper.countXueqiu()+"】只股票。");
	}

}
