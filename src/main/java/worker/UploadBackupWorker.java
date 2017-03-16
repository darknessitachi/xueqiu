package worker;

import java.io.IOException;
import java.util.Date;

import gui.StockFrame;
import util.Constants;
import util.DateUtil;
import util.FileUtil;
import util.ZipUtil;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;

public class UploadBackupWorker  {

	private StockFrame frame;

	public UploadBackupWorker(StockFrame frame) {
		this.frame = frame;
	}

	public void run() {
		try {
			long start = new Date().getTime();
			//如果有压缩文件，先删除
			String zip_path = frame.installZXGRootPath+"/"+Constants.user_path + ".zip";
			FileUtil.delete(zip_path);
			//压缩T0002目录
			ZipUtil.compressFile(frame.installZXGRootPath+"/"+Constants.user_path, frame.installZXGRootPath);
			
			//上传压缩后的zip文件到七牛
			QiniuUtil qn = new QiniuUtil(QiniuConstants.testBucketname, QiniuConstants.testDownloadDomainURL);
			String subFix = DateUtil.formatDate(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss);
			boolean success = qn.upload(zip_path , Constants.user_path +"_"+subFix+ ".zip");
			
			long end = new Date().getTime();
			if(success){
				System.out.println("上传备份完成，总共耗时【"+((end-start)/1000)+"】秒。");
				frame.displayLabel.setText("上传七牛完成。");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
