package worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.Constants;
import util.FileUtil;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;

public class UploadImgWorker  {

	@SuppressWarnings("unused")
	private StockFrame frame;

	public UploadImgWorker(StockFrame frame) {
		this.frame = frame;
	}

	public void run() {
		//先获取云端已有的列表
		QiniuUtil qn = new QiniuUtil(QiniuConstants.imageBucketname, QiniuConstants.imageDownloadDomainURL);
		List<String> cloudList = qn.fileList(QiniuConstants.imgPrefix);
		//获取本地所有文件，遍历，如果不在云端，则上传
		List<String> localList = FileUtil.getFullFileNames(Constants.out_img_path);
		List<String> newFileList = new ArrayList<String>();
		for(String fileName:localList){
			if(!exsit(cloudList,fileName)){
				newFileList.add(fileName);
			}
		}
		
		System.out.println("有【"+newFileList.size()+"】条数据需要上传。");
		
		for(String fileName:newFileList){
			try {
				qn.upload(Constants.out_img_path+"/"+fileName, QiniuConstants.imgPrefix+"/"+ fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("图片上传完成。");
		
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
