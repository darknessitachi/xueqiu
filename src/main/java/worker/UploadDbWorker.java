package worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.Date;

import util.Constants;
import util.DateUtil;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;

public class UploadDbWorker implements Runnable {

	private StockFrame frame;

	public UploadDbWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		try {
			String prefix = DateUtil.formatDate(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss);
			String file = Constants.out_path + Constants.db_path + Constants.db_name;
			
			String newName = prefix + "_" + Constants.db_name;
			QiniuConstants.bucketname = QiniuConstants.dbBucketname;
			boolean success = QiniuUtil.upload(file , QiniuConstants.database+"/"+newName);
			
			if(success){
				System.out.println("上传database完成。");
				frame.displayLabel.setText("上传database完成。");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
