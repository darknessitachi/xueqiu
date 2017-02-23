package worker;

import java.io.IOException;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;

import util.Constants;
import util.FileUtil;
import util.ZipUtil;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;
import gui.StockFrame;

public class SyncLocalWorker implements Runnable {

	private StockFrame frame;
	private boolean continueDownDb;

	public SyncLocalWorker(StockFrame frame, boolean continueDownDb) {
		this.frame = frame;
		this.continueDownDb = continueDownDb;
	}

	@Override
	public void run() {
		long start = new Date().getTime();
		//如果有zip文件，先删除。
		String zip_path = frame.installZXGRootPath+"/"+Constants.user_path + ".zip";
		FileUtil.delete(zip_path);
		System.out.println("删除zip文件完成。");
		
		try {
			QiniuConstants.downloadDomainURL = QiniuConstants.testDownloadDomainURL;
			QiniuConstants.bucketname = QiniuConstants.testBucketname;
			//下载zip
			QiniuUtil.download(Constants.user_path + ".zip", zip_path);
			//删除文件夹
			FileUtil.deleteFolder(frame.installZXGRootPath+"/"+Constants.user_path);
			//解压zip到指定文件夹
			ZipUtil.decompressZip(zip_path, frame.installZXGRootPath+"/"+Constants.user_path);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long end = new Date().getTime();
		System.out.println("同步本地完成，总共耗时【"+((end-start)/1000)+"】秒。");
		frame.displayLabel.setText("同步本地目录完成。");
		if(continueDownDb){
			frame.performDownDatabase(true);
		}
	}
	

}
