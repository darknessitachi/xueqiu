package worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.List;

import util.XueqiuUtil;

public class UploadXueqiuWorker implements Runnable {

	private List<String> names;
	private StockFrame frame;
	private boolean togetherUpload;

	public UploadXueqiuWorker(List<String> names,StockFrame frame, boolean continueUploadCloud) {
		this.names = names;
		this.frame = frame;
		this.togetherUpload = continueUploadCloud;
	}
	@Override
	public void run() {
		int num = 0;
		XueqiuUtil oper = new XueqiuUtil();
		try {
			//先删除，后上传
			try {
				oper.delAll();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(String name : names){
				try {
					String groupName = getGroupName(name);
					num = num + oper.uploadFile(name,groupName);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			oper.commitGroup();
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		frame.displayLabel.setText("上传雪球完成，添加【"+num+"】只股票，共【"+oper.countXueqiu(true)+"】只股票。");
		if(togetherUpload){
			frame.performUploadCloud();
			frame.performUploadDb();
			frame.performUploadImg();
		}
	}

	private String getGroupName(String name) {
		String groupName = "ZX";
		if(name.contains("A2")){
			groupName = "A2";
		}else if(name.contains("A3")){
			groupName = "A3";
		}
		return groupName;
	}

}
