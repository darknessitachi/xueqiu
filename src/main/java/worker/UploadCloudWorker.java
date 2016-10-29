package worker;

import java.io.IOException;
import java.util.Date;

import gui.StockFrame;
import util.Constants;
import util.FileUtil;
import util.ZipUtil;
import util.qiniu.QiniuUtil;

public class UploadCloudWorker implements Runnable {

	private StockFrame frame;

	public UploadCloudWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		
		long start = new Date().getTime();
		
		//如果有压缩文件，先删除
		String zip_path = frame.installZXGRootPath+"/"+Constants.user_path + ".zip";
		FileUtil.delete(zip_path);
		
		//压缩T0002目录
		try {
			ZipUtil.compressFile(frame.installZXGRootPath+"/"+Constants.user_path, frame.installZXGRootPath);
			System.out.println("压缩zip文件完成。");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//上传压缩后的zip文件到七牛
		try {
			QiniuUtil.upload(zip_path , Constants.user_path + ".zip");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long end = new Date().getTime();
		System.out.println("上传七牛完成，总共耗时【"+((end-start)/1000)+"】秒。");
		frame.displayLabel.setText("上传七牛完成。");
	}
	
}
