package worker;

import gui.StockFrame;

import java.io.IOException;

import util.Constants;
import util.FileUtil;
import util.StringUtil;

public class CopyBaiduWorker {

	private StockFrame frame;

	public CopyBaiduWorker(StockFrame frame) {
		this.frame = frame;
	}

	public void run() {
		if(!FileUtil.exists(Constants.baidu_path)){
			System.out.println("未找到目录【"+Constants.baidu_path+"】");
			return;
		}
		String baiduPath = Constants.baidu_path+"/"+StringUtil.getMaxNextFolderName(Constants.baidu_path,"version");
		try {
			FileUtil.copyDirectiory(Constants.jgy_path, baiduPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("拷贝完成。");
		frame.displayLabel.setText("拷贝完成。");
	}
	
}
