package worker;

import java.io.IOException;
import java.util.Date;

import gui.StockFrame;
import util.Constants;
import util.FileUtil;
import util.ZipUtil;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;

public class UploadCloudWorker implements Runnable {

	private StockFrame frame;

	public UploadCloudWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		try {
			
			long start = new Date().getTime();
			
			//如果有压缩文件，先删除
			String zip_path = frame.installZXGRootPath+"/"+Constants.user_path + ".zip";
			FileUtil.delete(zip_path);
			System.out.println("删除zip文件完成。");
			
			//压缩T0002目录
			ZipUtil.compressFile(frame.installZXGRootPath+"/"+Constants.user_path, frame.installZXGRootPath);
			
			QiniuConstants.bucketname = QiniuConstants.dbBucketname;
			//上传压缩后的zip文件到七牛
			boolean success = QiniuUtil.upload(zip_path , Constants.user_path + ".zip");
			
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
