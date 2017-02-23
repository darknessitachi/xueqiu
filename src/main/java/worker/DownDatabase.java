package worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.List;

import util.Constants;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;

public class DownDatabase implements Runnable {

	private StockFrame frame;

	public DownDatabase(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		try {
			QiniuConstants.downloadDomainURL = QiniuConstants.testDownloadDomainURL;
			QiniuConstants.bucketname = QiniuConstants.testBucketname;
			List<String> fileList = QiniuUtil.fileList(Constants.db_path);
			String latestFile = getLatestFile(fileList);
			
			String localFile = Constants.out_path + Constants.db_path + Constants.db_name;
			
			QiniuUtil.download(latestFile, localFile);
			
			System.out.println("云端共有【"+fileList.size()+"】条记录。");
			frame.displayLabel.setText("最新训练数据库【"+latestFile+"】下载完成。");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private String getLatestFile(List<String> fileList) {
		String max = fileList.get(0);
		for(String key : fileList){
			if(key.compareTo(max)>=0){
				max = key;
			}
		}
		return max;
	}
	
	
}
