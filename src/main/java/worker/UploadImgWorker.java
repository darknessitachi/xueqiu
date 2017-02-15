package worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.List;

import util.Constants;
import util.FileUtil;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;

public class UploadImgWorker implements Runnable {

	@SuppressWarnings("unused")
	private StockFrame frame;

	public UploadImgWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		QiniuConstants.bucketname = QiniuConstants.imageBucketname;
		//先获取云端已有的列表
		List<String> cloudList = QiniuUtil.fileList(QiniuConstants.imgPrefix);
		//获取本地所有文件，遍历，如果不在云端，则上传
		List<String> localList = FileUtil.getFullFileNames(Constants.out_img_path);
		for(String fileName:localList){
			if(!exsit(cloudList,fileName)){
				try {
					QiniuUtil.upload(Constants.out_img_path+"/"+fileName, QiniuConstants.imgPrefix+"/"+ fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private boolean exsit(List<String> list, String fileName) {
		
		for(String str:list){
			if(str.contains(fileName)){
				return true;
			}
		}
		
		return false;
	}
	
}
