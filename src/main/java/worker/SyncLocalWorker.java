package worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import util.XueqiuUtil;

public class SyncLocalWorker implements Runnable {

	private StockFrame frame;

	public SyncLocalWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		XueqiuUtil xq = new XueqiuUtil();
		Map<String, List<String>> data = null;
		try {
			data = xq.queryStockWithGroup();
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.syncLocal(data);
		frame.performAutoChoose();
		frame.displayLabel.setText("同步本地目录完成。自动导入完成。");
	}
	

}
