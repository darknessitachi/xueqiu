package worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.Date;

import util.Constants;
import util.DateUtil;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;

public class UploadDatabaseWorker  {

	private StockFrame frame;

	public UploadDatabaseWorker(StockFrame frame) {
		this.frame = frame;
	}

	public void run() {
		try {
			String prefix = DateUtil.formatDate(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss);
			String file = Constants.out_path + Constants.db_path + Constants.db_name;
			
			String newName = prefix + "_" + Constants.db_name;
			
			QiniuUtil qn = new QiniuUtil(QiniuConstants.testBucketname, QiniuConstants.testDownloadDomainURL);
			boolean success = qn.upload(file , QiniuConstants.databasePrefix+"/"+newName);
			
			if(success){
				frame.displayLabel.setText("上传database完成。");
				System.out.println("******************（3）上传数据库完成******************");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
