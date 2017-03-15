package worker;

import gui.StockFrame;
import util.Constants;
import util.FileUtil;

public class WriteJgyFolderWorker implements Runnable {

	private StockFrame frame;

	public WriteJgyFolderWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		if(!FileUtil.exists(Constants.jgy_path)){
			System.out.println("文件夹【"+Constants.jgy_path+"】不存在");
			return;
		}
		
		
		frame.displayLabel.setText("写入坚果云完成。");
	}

	
}
