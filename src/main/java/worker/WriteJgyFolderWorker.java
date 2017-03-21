package worker;

import gui.StockFrame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import util.AccessUtil;
import util.Constants;
import util.FileUtil;
import util.MiniDbUtil;
import util.StringUtil;

public class WriteJgyFolderWorker  {
	

	private StockFrame frame;
	private String ksrq;
	private List<String> dbData = new ArrayList<String>();//2017-03-15_all_2017-03-15_600340_0.png

	public WriteJgyFolderWorker(StockFrame frame) {
		this.frame = frame;
	}

	public void run() {
		if(!FileUtil.exists(Constants.jgy_path)){
			System.out.println("文件夹【"+Constants.jgy_path+"】不存在");
			return;
		}
		
		Properties params = AccessUtil.readParams();
		ksrq = params.getProperty("jgyKsrq").trim();
		
		writeRecord();
		writeMistake();
		
		delete();
		renameFolder();
		
		System.out.println("写入坚果云完成。");
		frame.displayLabel.setText("写入坚果云完成。");
	}
	
	
	


	private void writeRecord() {
		//获取所有天数，从02-17日开始
		List<String> days = MiniDbUtil.queryForList(" select distinct day from record where type in ('1','2','3') and day>='"+ksrq+"' ");
		StringBuilder msg = new StringBuilder();
		//遍历每一天
		for(String day:days){
			//获取dayFolder
			List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
			String dayFolderName = FileUtil.fileLike(folderList, day);
			dayFolderName = dayFolderName==null?day:dayFolderName;
			//绝对路径
			String dayFolderPath = Constants.jgy_path+"/"+dayFolderName;
			String before = Constants.jgy_path+"/"+dayFolderName+"/before";
			String all = Constants.jgy_path+"/"+dayFolderName+"/all";
			//如果文件夹不存在，则创建
			if(!FileUtil.exists(before)){
				FileUtil.createFolder(before);
			}
			if(!FileUtil.exists(all)){
				FileUtil.createFolder(all);
			}
			String preDay = null;
			
			String sql = " select r.*,s.code from record r left join stock s on stockName=name where type in ('1','2','3') and day='"+day+"' order by type asc,xh asc,rate desc,createDate desc ";
			List<Map<String,Object>> list = MiniDbUtil.query(sql);
			
			//遍历一天中的数据，写入对应的文件夹
			for(Map<String,Object> map:list){
				String code = (String) map.get("code");
				preDay = (String) map.get("preDay"); 
				
				if(StringUtil.isEmpty(code)){
					msg.append("【"+map.get("stockName")+"】未找到code").append("\n");
					break;
				}
				
				//导出反弹前的图片
				String srcFileName = preDay+"_"+code.substring(1)+".png";
				String targetFileName = day+"_"+code.substring(1)+"_0.png";
				if(FileUtil.exists(Constants.out_img_path+"/"+srcFileName)){
					//如果目标不存在，则写入
					if(!FileUtil.exists(all+"/"+targetFileName)){
						FileUtil.copy(all+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
					}
					if(!FileUtil.exists(before+"/"+targetFileName)){
						FileUtil.copy(before+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
					}
					dbData.add(day+"_all_"+targetFileName);
					dbData.add(day+"_before_"+targetFileName);
				}else{
					msg.append("资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到").append("\n");
				}
				
				//导出反弹后的图片
				srcFileName = day+"_"+code.substring(1)+".png";
				targetFileName = day+"_"+code.substring(1)+"_1.png";
				if(FileUtil.exists(Constants.out_img_path+"/"+srcFileName)){
					//如果目标不存在，则写入
					if(!FileUtil.exists(all+"/"+targetFileName)){
						FileUtil.copy(all+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
					}
					dbData.add(day+"_all_"+targetFileName);
				}else{
					msg.append("资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到").append("\n");
				}
				
				//导出分时图片
				srcFileName = day+"_"+code.substring(1)+"_T.png";
				targetFileName = day+"_"+code.substring(1)+"_2.png";
				if(FileUtil.exists(Constants.out_img_path+"/"+srcFileName)){
					//如果目标不存在，则写入
					if(!FileUtil.exists(all+"/"+targetFileName)){
						FileUtil.copy(all+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
					}
					dbData.add(day+"_all_"+targetFileName);
				}else{
					msg.append("资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到").append("\n");
				}
			}
			//如果preDay存在，写入大盘到对应的文件夹
			writeIndex(preDay,day,dayFolderPath);
		}
		System.err.println(msg.toString());
	}
	
	

	private void writeMistake() {
		//获取所有天数，从02-17日开始
		List<String> days = MiniDbUtil.queryForList(" select distinct day from mistake where day>='"+ksrq+"' ");
		StringBuilder msg = new StringBuilder();
		//遍历每一天
		for(String day:days){
			
			//获取dayFolder
			List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
			String dayFolderName = FileUtil.fileLike(folderList, day);
			dayFolderName = dayFolderName==null?day:dayFolderName;
			
			//绝对路径
			String dayFolderPath = Constants.jgy_path+"/"+dayFolderName;
			String mistake = Constants.jgy_path+"/"+dayFolderName+"/mistake";
			
			//如果文件夹不存在，则创建
			if(!FileUtil.exists(mistake)){
				FileUtil.createFolder(mistake);
			}
			
			String sql = " select m.*,s.code from mistake m left join stock s on stockName=name where day='"+day+"' order by xh asc,createDate desc ";
			List<Map<String,Object>> list = MiniDbUtil.query(sql);
			String preDay = null;
			
			//遍历一天中的数据，写入对应的文件夹
			for(Map<String,Object> map:list){
				String code = (String) map.get("code");
				preDay = (String) map.get("preDay"); 
				
				if(StringUtil.isEmpty(code)){
					msg.append("【"+map.get("stockName")+"】未找到code").append("\n");
					break;
				}
				
				//导出反弹前的图片
				String srcFileName = preDay+"_"+code.substring(1)+".png";
				String targetFileName = day+"_"+code.substring(1)+"_0.png";
				if(FileUtil.exists(Constants.out_img_path+"/"+srcFileName)){
					//如果目标不存在，则写入
					if(!FileUtil.exists(mistake+"/"+targetFileName)){
						FileUtil.copy(mistake+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
					}
					dbData.add(day+"_mistake_"+targetFileName);
				}else{
					msg.append("资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到").append("\n");
				}
				
				//导出反弹后的图片
				srcFileName = day+"_"+code.substring(1)+".png";
				targetFileName = day+"_"+code.substring(1)+"_1.png";
				if(FileUtil.exists(Constants.out_img_path+"/"+srcFileName)){
					//如果目标不存在，则写入
					if(!FileUtil.exists(mistake+"/"+targetFileName)){
						FileUtil.copy(mistake+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
					}
					dbData.add(day+"_mistake_"+targetFileName);
				}else{
					msg.append("资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到").append("\n");
				}
			}
			//如果preDay存在，写入大盘到对应的文件夹
			writeIndex(preDay,day,dayFolderPath);
		}
		System.err.println(msg.toString());
	}

	private void writeIndex(String preDay, String day, String folder) {
		
		String SH_CODE = "000SH";
		String CYB_CODE = "001CYB";
		
		if(!StringUtil.isEmpty(preDay)){
			//最后写入大盘的照片
			if(FileUtil.exists(Constants.out_img_path+"/"+preDay+"_SH.png")  ){
				//如果目标不存在，则写入
				if(!FileUtil.exists(folder+"/"+day+"_"+SH_CODE+"_0.png")){
					FileUtil.copy(folder+"/"+day+"_"+SH_CODE+"_0.png", new File(Constants.out_img_path+"/"+preDay+"_SH.png"));
				}
			}
			if(FileUtil.exists(Constants.out_img_path+"/"+preDay+"_CYB.png")  ){
				//如果目标不存在，则写入
				if(!FileUtil.exists(folder+"/"+day+"_"+CYB_CODE+"_0.png")){
					FileUtil.copy(folder+"/"+day+"_"+CYB_CODE+"_0.png", new File(Constants.out_img_path+"/"+preDay+"_CYB.png"));
				}
			}
			if(FileUtil.exists(Constants.out_img_path+"/"+day+"_SH.png")  ){
				//如果目标不存在，则写入
				if(!FileUtil.exists(folder+"/"+day+"_"+SH_CODE+"_1.png")){
					FileUtil.copy(folder+"/"+day+"_"+SH_CODE+"_1.png", new File(Constants.out_img_path+"/"+day+"_SH.png"));
				}
			}
			if(FileUtil.exists(Constants.out_img_path+"/"+day+"_CYB.png")  ){
				//如果目标不存在，则写入
				if(!FileUtil.exists(folder+"/"+day+"_"+CYB_CODE+"_1.png")){
					FileUtil.copy(folder+"/"+day+"_"+CYB_CODE+"_1.png", new File(Constants.out_img_path+"/"+day+"_CYB.png"));
				}
			}
		}
	}
	
	private void delete() {
		List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
		for(String folder : folderList){
			if(folder.indexOf("-") == 4){
				String day = folder.substring(0, 10);
				
				String allFolder = Constants.jgy_path+"/"+folder+"/all";
				String beforeFolder = Constants.jgy_path+"/"+folder+"/before";
				String mistakeFolder = Constants.jgy_path+"/"+folder+"/mistake";
				//遍历目录，删除没有在数据库中的文件
				commonDeleteFile(day,"all",allFolder);
				commonDeleteFile(day,"before",beforeFolder);
				commonDeleteFile(day,"mistake",mistakeFolder);
			}
		}
		//如果三个目录同时为空，则删除整个目录
		for(String folder : folderList){
			if(folder.indexOf("-") == 4){
				String allFolder = Constants.jgy_path+"/"+folder+"/all";
				String beforeFolder = Constants.jgy_path+"/"+folder+"/before";
				String mistakeFolder = Constants.jgy_path+"/"+folder+"/mistake";
				
				List<String> allList = FileUtil.getFullFileNames(allFolder);
				List<String> beforeList = FileUtil.getFullFileNames(beforeFolder);
				List<String> mistakeList = FileUtil.getFullFileNames(mistakeFolder);
				
				if(mistakeList.size() == 0){
					if(FileUtil.exists(mistakeFolder)){
						FileUtil.removeFolder(mistakeFolder);
					}
				}
				
				if(allList.size() == 0 && beforeList.size() == 0 && mistakeList.size() == 0){
					FileUtil.removeFolder(Constants.jgy_path+"/"+folder);
				}
			}
		}
	}

	private void commonDeleteFile(String day, String folderName, String path) {
		List<String> list = FileUtil.getFullFileNames(path);
		for(String fileName:list){
			String element = day+"_"+folderName+"_"+fileName;
			//如果该文件，不包含在dbData中，则删除
			if(!dbData.contains(element)){
				String filePath = path+"/"+fileName;
				FileUtil.delete(filePath);
			}
		}
	}
	

	private void renameFolder() {
		
		List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
		for(String folderName : folderList){
			if(folderName.indexOf("-") == 4){
				String day = folderName.substring(0, 10);
				
				String beforePath = Constants.jgy_path+"/"+folderName+"/before";
				String mistakePath = Constants.jgy_path+"/"+folderName+"/mistake";
				
				String recordCount = "0";
				String mistakeCount = "-";
				if(FileUtil.exists(beforePath)){
					recordCount = (new File(beforePath).list().length)+"";
				}
				if(FileUtil.exists(mistakePath)){
					mistakeCount = ((new File(mistakePath).list().length)/2)+"";
				}
				
				String newFolderName = day+"（"+recordCount+"）（"+mistakeCount+"）";
				if(!newFolderName.equals(folderName)){
					FileUtil.renameDirectory(Constants.jgy_path+"/"+folderName, Constants.jgy_path+"/"+newFolderName);
				}
			}
		}
	}

	
}
