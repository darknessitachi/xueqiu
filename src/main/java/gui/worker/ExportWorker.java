package gui.worker;

import gui.StockFrame;

public class ExportWorker implements Runnable {

	private StockFrame frame;

	public ExportWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		
		frame.displayLabel.setText("导出完成。");
	}

}
