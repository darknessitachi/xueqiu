package worker;

import gui.StockFrame;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import util.AccessUtil;
import util.Constants;
import util.FileUtil;
import util.MiniDbUtil;
import util.StringUtil;

public class WriteJgyFolderWorker  {
	
	public class DayBean{
		public Set<String> record = new HashSet<String>();
		public Set<String> mistake = new HashSet<String>();
	} 

	private StockFrame frame;
	private String ksrq;
	private  Map<String,DayBean> dbData;//日期：data
	private  Map<String,DayBean> fileData;//日期：data

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
		
		dbData = new HashMap<String,DayBean>();
		
		writeRecord();
		writeMistake();
		
		initFileData();
		
		delete();
		
		System.out.println("写入坚果云完成。");
		frame.displayLabel.setText("写入坚果云完成。");
	}
	
	
	private void delete() {
		List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
		//遍历fileData
		for(String day:fileData.keySet()){
			String dayFolder = FileUtil.fileLike(folderList, day);
			//如果dbData的day文件夹为空，则删除文件夹
			DayBean dbBean = dbData.get(day);
			if(dbBean == null || (dbBean.record.size()==0 && dbBean.mistake.size()==0)){
				FileUtil.removeFolder(Constants.jgy_path+"/"+dayFolder);
			}
		}
	}

	private void initFileData() {
		fileData = new HashMap<String,DayBean>();
		//获取文件夹
		List<String> folderList = new ArrayList<String>();
		List<String> temp = FileUtil.getFullFileNames(Constants.jgy_path);
		for(String str:temp){
			if(str.indexOf("-") == 4){
				folderList.add(str);
			}
		}
		//初始化data
		for(String folderName:folderList){
			String day = folderName.substring(0, 10);
			String beforeFolder = Constants.jgy_path + "/"+folderName+"/before";
			String mistakeFolder = Constants.jgy_path + "/"+folderName+"/mistake";
			
			DayBean bean = fileData.get(day);
			if(bean == null){
				bean = new DayBean();
				fileData.put(day, bean);
			}
			
			//如果before文件存在，遍历里面的文件，填充data
			if(FileUtil.exists(beforeFolder)){
				List<String> list = FileUtil.getFullFileNames(beforeFolder);
				for(String str : list){
					String code = str.substring(11, 17);
					bean.record.add(code);
				}
			}
			
			//如果mistake文件存在，遍历里面的文件，填充data
			if(FileUtil.exists(mistakeFolder)){
				List<String> list = FileUtil.getFullFileNames(mistakeFolder);
				for(String str : list){
					String code = str.substring(11, 17);
					bean.mistake.add(code);
				}
			}
		}
	}

	private void writeRecord() {
		//获取所有天数，从02-17日开始
		List<String> days = MiniDbUtil.queryForList(" select distinct day from record where type in ('1','2','3') and day>='"+ksrq+"' ");
		StringBuilder msg = new StringBuilder();
		//遍历每一天
		for(String day:days){
			//获取dayFolder
			List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
			String dayFolder = FileUtil.fileLike(folderList, day);
			dayFolder = dayFolder==null?day:dayFolder;
			//绝对路径
			String folder = Constants.jgy_path+"/"+dayFolder;
			String before = Constants.jgy_path+"/"+dayFolder+"/before";
			String all = Constants.jgy_path+"/"+dayFolder+"/all";
			
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
			
			DayBean bean = dbData.get(day);
			if(bean == null){
				bean = new DayBean();
				dbData.put(day, bean);
			}
			//遍历一天中的数据，写入对应的文件夹
			for(Map<String,Object> map:list){
				String code = (String) map.get("code");
				preDay = (String) map.get("preDay"); 
				
				if(StringUtil.isEmpty(code)){
					msg.append("【"+map.get("stockName")+"】未找到code").append("\n");
					break;
				}
				bean.record.add(code);
				
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
				}else{
					msg.append("资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到").append("\n");
				}
			}
			//如果preDay存在，写入大盘到对应的文件夹
			writeIndex(preDay,day,folder);
			//如果文件夹名发生变化，则对folder进行重命名
			String afterDayFolder = day+"（"+list.size()+"）";
			if(!dayFolder.equals(afterDayFolder)){
				FileUtil.renameDirectory(folder, Constants.jgy_path+"/"+afterDayFolder);
			}
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
			String dayFolder = FileUtil.fileLike(folderList, day);
			dayFolder = dayFolder==null?day:dayFolder;
			
			//绝对路径
			String folder = Constants.jgy_path+"/"+dayFolder;
			String mistake = Constants.jgy_path+"/"+dayFolder+"/mistake";
			
			//如果文件夹不存在，则创建
			if(!FileUtil.exists(mistake)){
				FileUtil.createFolder(mistake);
			}
			
			String sql = " select m.*,s.code from mistake m left join stock s on stockName=name where day='"+day+"' order by xh asc,createDate desc ";
			List<Map<String,Object>> list = MiniDbUtil.query(sql);
			String preDay = null;
			
			//遍历一天中的数据，写入对应的文件夹
			DayBean bean = dbData.get(day);
			if(bean == null){
				bean = new DayBean();
				dbData.put(day, bean);
			}
			for(Map<String,Object> map:list){
				String code = (String) map.get("code");
				preDay = (String) map.get("preDay"); 
				
				if(StringUtil.isEmpty(code)){
					msg.append("【"+map.get("stockName")+"】未找到code").append("\n");
					break;
				}
				bean.mistake.add(code);
				
				//导出反弹前的图片
				String srcFileName = preDay+"_"+code.substring(1)+".png";
				String targetFileName = day+"_"+code.substring(1)+"_0.png";
				if(FileUtil.exists(Constants.out_img_path+"/"+srcFileName)){
					//如果目标不存在，则写入
					if(!FileUtil.exists(mistake+"/"+targetFileName)){
						FileUtil.copy(mistake+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
					}
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
				}else{
					msg.append("资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到").append("\n");
				}
				
			}
			//如果preDay存在，写入大盘到对应的文件夹
			writeIndex(preDay,day,folder);
		}
		System.err.println(msg.toString());
	}



	private void writeIndex(String preDay, String day, String folder) {
		if(!StringUtil.isEmpty(preDay)){
			//最后写入大盘的照片
			if(FileUtil.exists(Constants.out_img_path+"/"+preDay+"_SH.png")  ){
				//如果目标不存在，则写入
				if(!FileUtil.exists(folder+"/"+day+"_000SH_0.png")){
					FileUtil.copy(folder+"/"+day+"_000SH_0.png", new File(Constants.out_img_path+"/"+preDay+"_SH.png"));
				}
			}
			if(FileUtil.exists(Constants.out_img_path+"/"+preDay+"_CYB.png")  ){
				//如果目标不存在，则写入
				if(!FileUtil.exists(folder+"/"+day+"_000CYB_0.png")){
					FileUtil.copy(folder+"/"+day+"_000CYB_0.png", new File(Constants.out_img_path+"/"+preDay+"_CYB.png"));
				}
			}
			if(FileUtil.exists(Constants.out_img_path+"/"+day+"_SH.png")  ){
				//如果目标不存在，则写入
				if(!FileUtil.exists(folder+"/"+day+"_000SH_1.png")){
					FileUtil.copy(folder+"/"+day+"_000SH_1.png", new File(Constants.out_img_path+"/"+day+"_SH.png"));
				}
			}
			if(FileUtil.exists(Constants.out_img_path+"/"+day+"_CYB.png")  ){
				//如果目标不存在，则写入
				if(!FileUtil.exists(folder+"/"+day+"_000CYB_1.png")){
					FileUtil.copy(folder+"/"+day+"_000CYB_1.png", new File(Constants.out_img_path+"/"+day+"_CYB.png"));
				}
			}
		}
	}

	
}
