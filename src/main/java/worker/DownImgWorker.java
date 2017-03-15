package worker;

import gui.StockFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import util.Constants;
import util.FileUtil;
import util.qiniu.QiniuConstants;
import util.qiniu.QiniuUtil;

public class DownImgWorker implements Runnable {

	@SuppressWarnings("unused")
	private StockFrame frame;

	public DownImgWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		QiniuConstants.downloadDomainURL = QiniuConstants.imageDownloadDomainURL;
		QiniuConstants.bucketname = QiniuConstants.imageBucketname;
		//先获取云端已有的列表
		List<String> cloudList = QiniuUtil.fileList(QiniuConstants.imgPrefix);
		//获取本地所有文件，遍历，如果不在云端，则上传
		List<String> localList = FileUtil.getFullFileNames(Constants.out_img_path);
		List<String> newFileList = new ArrayList<String>();
		for(String cloudFile:cloudList){
			if(!exsit(localList,cloudFile)){
				newFileList.add(cloudFile);
			}
		}
		
		System.out.println("有【"+newFileList.size()+"】条数据需要下载。");
		
		for(String cloudFile:newFileList){
			try {
				QiniuUtil.download(cloudFile, Constants.out_img_path+"/"+cloudFile.substring(4));
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("图片下载完成。");
		
	}

	private boolean exsit(List<String> list, String fileName) {
		
		fileName = fileName.substring(4);
		
		for(String str:list){
			if(str.contains(fileName)){
				return true;
			}
		}
		
		return false;
	}
	
}
