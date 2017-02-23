package worker;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import gui.StockFrame;
import util.Constants;
import util.ProjectUtil;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;

public class ClearCloudWorker implements Runnable {

	private StockFrame frame;

	public ClearCloudWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		QiniuConstants.bucketname = QiniuConstants.testBucketname;
		
		//清理多余数据库
		List<String> fileList = QiniuUtil.fileList(Constants.db_path);
		TreeSet<String> treeSet = ProjectUtil.getOrderTreeSet(fileList);
		clearCloud(treeSet,Constants.DB_NUM);
		
		//清理多余备份
		List<String> backList = QiniuUtil.fileList(Constants.user_path);
		TreeSet<String> backSet = ProjectUtil.getOrderTreeSet(backList);
		clearCloud(backSet,Constants.BACKUP_NUM);
		
		frame.displayLabel.setText("清理完成。");
	}
	
	private void clearCloud(TreeSet<String> tree, int keepNum) {
		int clearNum = tree.size() - keepNum;
		if(clearNum>0){
			int i = 0;
			Iterator<String> iterator = tree.iterator();
			while (iterator.hasNext()) { 
				if(i == clearNum){
	            	break;
	            }
	            QiniuUtil.delete(iterator.next());
	            i++;
	        }  
		}
	}
	
}