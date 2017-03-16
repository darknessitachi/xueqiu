package worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import util.Constants;
import util.ProjectUtil;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;

public class DownDatabase {

	private StockFrame frame;

	public DownDatabase(StockFrame frame) {
		this.frame = frame;
	}

	public void run() {
		try {
			QiniuUtil qn = new QiniuUtil(QiniuConstants.testBucketname, QiniuConstants.testDownloadDomainURL);
			List<String> fileList = qn.fileList(Constants.db_path);
			TreeSet<String> treeSet = ProjectUtil.getOrderTreeSet(fileList);
			
			String localFile = Constants.out_path + Constants.db_path + Constants.db_name;
			
			qn.download(treeSet.last(), localFile);
			
			System.out.println("云端共有【"+fileList.size()+"】条记录。");
			frame.displayLabel.setText("最新训练数据库【"+treeSet.last()+"】下载完成。");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
}
