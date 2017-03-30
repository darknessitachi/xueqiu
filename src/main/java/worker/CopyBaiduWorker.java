package worker;

import java.io.IOException;
import java.util.List;

import util.Constants;
import util.FileUtil;
import gui.StockFrame;

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
		String baiduPath = Constants.baidu_path+"/"+getNewVersion();
		try {
			FileUtil.copyDirectiory(Constants.jgy_path, baiduPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("拷贝完成。");
		frame.displayLabel.setText("拷贝完成。");
	}
	
	private String getNewVersion() {
		List<String> list = FileUtil.getFullFileNames(Constants.baidu_path);
		int max = 0;
		for(String str:list){
			if(str.startsWith("version")){
				String s = str.replace("version", "");
				int num = Integer.parseInt(s);
				if(num>max){
					max = num;
				}
			}
		}
		return "version"+(max+1);
	}
	
}
