package gui.worker;

import gui.core.StockFrame;

import java.io.IOException;

import util.core.ProjectUtil;
import util.core.XueqiuUtil;

public class ExportWorker implements Runnable {

	private StockFrame frame;

	public ExportWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		XueqiuUtil so = new XueqiuUtil();
		try {
			so.export();
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.displayLabel.setText("下载完成，目录【"+ProjectUtil.getComputerHomeDir()+"】");
	}

}
