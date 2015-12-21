package gui.worker;

import java.io.IOException;

import config.Constants;
import app.xueqiu.StockOpertion;
import gui.StockFrame;

public class ExportWorker implements Runnable {

	private StockFrame frame;

	public ExportWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		StockOpertion so = new StockOpertion();
		try {
			so.export();
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.displayLabel.setText("导出完成，导出目录【"+Constants.export+"】");
	}

}
