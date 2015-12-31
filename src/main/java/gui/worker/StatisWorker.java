package gui.worker;

import java.io.IOException;
import java.util.List;

import config.Constants;
import func.MainAutoOne;
import gui.core.StockFrame;

public class StatisWorker implements Runnable {

	private List<String> names;
	private StockFrame frameFirst;

	public StatisWorker(List<String> names, StockFrame frameFirst) {
		this.names = names;
		this.frameFirst = frameFirst;
	}

	@Override
	public void run() {
		for(String name : names){
			//获取每个板块的路径
			try {
				MainAutoOne.autoOne(name);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		frameFirst.displayLabel.setText("统计完成，输出目录【"+Constants.outPath+"】");
	}

}
