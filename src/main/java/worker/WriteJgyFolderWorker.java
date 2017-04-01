package worker;

import gui.StockFrame;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import util.AccessUtil;
import util.CollectionUtil;
import util.Constants;
import util.FileUtil;
import util.MiniDbUtil;
import util.MiniExcelTemplate;
import util.StringUtil;

public class WriteJgyFolderWorker  {
	

	private StockFrame frame;
	private String ksrq;
	private List<String> dbData = new ArrayList<String>();//2017-03-15_all_2017-03-15_600340_0.png
	
	private final static String ALL_FOLDER_NAME = "all";
	private final static String MISTAKE_FOLDER_NAME = "mistake";
	private final static String NOTHING_FOLDER_NAME = "nothing";

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
		
		try {
			
			writeRecord();
			writeNothing();
			writeMistake();
			writeTrain("0101",MISTAKE_FOLDER_NAME);
			writeTrain("0102",NOTHING_FOLDER_NAME);
			
			deleteFile();
			deleteFolder();
			//写入备注
			writeComment();
			writeRoot();
			//文件夹重命名
			renameFolder();
			
			//增量拷贝到百度
			incrementToBaiduYun();
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("写入坚果云完成。");
		frame.displayLabel.setText("写入坚果云完成。");
	}

	private void writeRoot() {
		List<Map<String,Object>> list = MiniDbUtil.query(" select * from result where 1=1  and  day>='"+ksrq+"'  order by  day desc   ");
		List<List<Object>> data1 = new ArrayList<List<Object>>();
		for(Map<String,Object> map:list){
			int up = (int) map.get("upCount");
			int down = (int) map.get("downCount");
			int all = (int) map.get("aCount");
			String fg = (all!=0 && down>=up)?"主阴线反转":"";
			
			List<Object> row = new ArrayList<Object>();
			row.add(map.get("day"));
			row.add("（"+up+"）（"+down+"）");
			row.add(all);
			row.add(fg);
			data1.add(row);
		}
		
		List<List<List<Object>>> allSheetData = new ArrayList<List<List<Object>>>();
    	allSheetData.add(data1);
    	
    	List<String> sheetList = new ArrayList<String>();
    	sheetList.add("统计");
    	
    	String[] titleArr = {"日期","追涨与阴线反转","反弹合计","风格"};
		List<String> title = java.util.Arrays.asList(titleArr);
		
		//导出
		MiniExcelTemplate temp = new MiniExcelTemplate();
    	temp.createExcel(sheetList,title,allSheetData,new int[]{5000,8000,5000,5000});
    	temp.export(Constants.jgy_path+"/stat.xls");
	}

	public void writeTrain(String score, String folderName) {
		//获取天数，从02-17日开始
		List<String> days = MiniDbUtil.queryForList(" select distinct day from (select s.forecastDay day,s.preDay,t.* from train t ,suptrain s where t.supid=s.id and day>='"+ksrq+"' and score='"+score+"') ");
		//遍历每一天
		for(String day:days){
			
			String folderPath = getSecondPath(day,folderName);
			String sql = " select  k.code,s.forecastDay day,s.preDay from train t ,suptrain s left join stock k on t.stockName=k.name where t.supid=s.id and forecastDay='"+day+"' and score='"+score+"'  group by code,day,preDay ";
			List<Map<String,Object>> list = MiniDbUtil.query(sql);
			
			//遍历一天中的数据，写入对应的文件夹
			for(Map<String,Object> map:list){
				String code = (String) map.get("code");
				String preDay = (String) map.get("preDay"); 
				
				if(StringUtil.isEmpty(code)){
					System.err.println(StringUtil.getLineInfo()+":【"+map.get("stockName")+"】未找到code");
					break;
				}
				
				//导出反弹前的图片
				String srcFileName = preDay+"_"+code.substring(1)+".png";
				String targetFileName = day+"_"+code.substring(1)+"_0.png";
				writePicture(srcFileName,targetFileName,day,folderPath,folderName);
				
				//导出反弹后的图片
				srcFileName = day+"_"+code.substring(1)+".png";
				targetFileName = day+"_"+code.substring(1)+"_1.png";
				writePicture(srcFileName,targetFileName,day,folderPath,folderName);
			}
		}
	}

	private void writeRecord() {
		//获取所有天数，从02-17日开始
		List<String> days = MiniDbUtil.queryForList(" select distinct day from record where type in ('1','2','3') and day>='"+ksrq+"' ");
		//遍历每一天
		for(String day:days){
			
			String allPath = getSecondPath(day,ALL_FOLDER_NAME);
			String preDay = null;
			
			String sql = " select r.*,s.code from record r left join stock s on stockName=name where type in ('1','2','3') and day='"+day+"' order by type asc,xh asc,rate desc,createDate desc ";
			List<Map<String,Object>> list = MiniDbUtil.query(sql);
			
			//遍历一天中的数据，写入对应的文件夹
			for(Map<String,Object> map:list){
				String code = (String) map.get("code");
				preDay = (String) map.get("preDay"); 
				
				if(StringUtil.isEmpty(code)){
					System.err.println(StringUtil.getLineInfo()+":【"+map.get("stockName")+"】未找到code");
					break;
				}
				
				//导出反弹前的图片
				String srcFileName = preDay+"_"+code.substring(1)+".png";
				String targetFileName = day+"_"+code.substring(1)+"_0.png";
				writePicture(srcFileName,targetFileName,day,allPath,ALL_FOLDER_NAME);
				
				//导出反弹后的图片
				srcFileName = day+"_"+code.substring(1)+".png";
				targetFileName = day+"_"+code.substring(1)+"_1.png";
				writePicture(srcFileName,targetFileName,day,allPath,ALL_FOLDER_NAME);
				
				//导出分时图片
				srcFileName = day+"_"+code.substring(1)+"_T.png";
				targetFileName = day+"_"+code.substring(1)+"_2.png";
				writePicture(srcFileName,targetFileName,day,allPath,ALL_FOLDER_NAME);
			}
		}
	}

	private void writeMistake() {
		//获取所有天数，从02-17日开始
		List<String> days = MiniDbUtil.queryForList(" select distinct day from mistake where day>='"+ksrq+"' ");
		//遍历每一天
		for(String day:days){
			
			String mistakePath = getSecondPath(day,MISTAKE_FOLDER_NAME);
			String sql = " select m.*,s.code from mistake m left join stock s on stockName=name where day='"+day+"' order by xh asc,createDate desc ";
			List<Map<String,Object>> list = MiniDbUtil.query(sql);
			String preDay = null;
			
			//遍历一天中的数据，写入对应的文件夹
			for(Map<String,Object> map:list){
				String code = (String) map.get("code");
				preDay = (String) map.get("preDay"); 
				
				if(StringUtil.isEmpty(code)){
					System.err.println(StringUtil.getLineInfo()+":【"+map.get("stockName")+"】未找到code");
					break;
				}
				
				//导出反弹前的图片
				String srcFileName = preDay+"_"+code.substring(1)+".png";
				String targetFileName = day+"_"+code.substring(1)+"_0.png";
				writePicture(srcFileName,targetFileName,day,mistakePath,MISTAKE_FOLDER_NAME);
				
				//导出反弹后的图片
				srcFileName = day+"_"+code.substring(1)+".png";
				targetFileName = day+"_"+code.substring(1)+"_1.png";
				writePicture(srcFileName,targetFileName,day,mistakePath,MISTAKE_FOLDER_NAME);
			}
		}
	}
	
	private void writeNothing() {
		//获取所有天数，从02-17日开始
		List<String> days = MiniDbUtil.queryForList(" select distinct day from record where type in ('4') and day>='"+ksrq+"' ");
		//遍历每一天
		for(String day:days){
			
			String nothingPath = getSecondPath(day,NOTHING_FOLDER_NAME);
			
			String sql = " select r.*,s.code from record r left join stock s on stockName=name where type in ('4') and day='"+day+"' order by type asc,xh asc,rate desc,createDate desc ";
			List<Map<String,Object>> list = MiniDbUtil.query(sql);
			String preDay = null;
			//遍历一天中的数据，写入对应的文件夹
			for(Map<String,Object> map:list){
				String code = (String) map.get("code");
				preDay = (String) map.get("preDay"); 
				
				if(StringUtil.isEmpty(code)){
					System.err.println(StringUtil.getLineInfo()+":【"+map.get("stockName")+"】未找到code");
					break;
				}
				
				//导出反弹前的图片
				String srcFileName = preDay+"_"+code.substring(1)+".png";
				String targetFileName = day+"_"+code.substring(1)+"_0.png";
				writePicture(srcFileName,targetFileName,day,nothingPath,NOTHING_FOLDER_NAME);
				
				//导出反弹后的图片
				srcFileName = day+"_"+code.substring(1)+".png";
				targetFileName = day+"_"+code.substring(1)+"_1.png";
				writePicture(srcFileName,targetFileName,day,nothingPath,NOTHING_FOLDER_NAME);
				
				//导出分时图片
				/*srcFileName = day+"_"+code.substring(1)+"_T.png";
				targetFileName = day+"_"+code.substring(1)+"_2.png";
				writePicture(srcFileName,targetFileName,day,nothingPath,NOTHING_FOLDER_NAME);*/
			}
		}
	}
	
	private void writePicture(String srcFileName, String targetFileName,
			String day, String path, String folderName) {
		if(FileUtil.exists(Constants.out_img_path+"/"+srcFileName)){
			//如果目标不存在，则写入
			if(!FileUtil.exists(path+"/"+targetFileName)){
				FileUtil.copy(path+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
			}
			dbData.add(day+"_"+folderName+"_"+targetFileName);
		}else{
			System.err.println(StringUtil.getLineInfo()+":资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到");
		}
	}

	private String getSecondPath(String day, String secondFolderName) {
		//获取dayFolder
		List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
		String dayFolderName = FileUtil.fileLike(folderList, day);
		dayFolderName = dayFolderName==null?day:dayFolderName;
		//绝对路径
		String path = Constants.jgy_path+"/"+dayFolderName+"/"+secondFolderName;
		//如果文件夹不存在，则创建
		if(!FileUtil.exists(path)){
			FileUtil.createFolder(path);
		}
		return path;
	}

	private void deleteFile() {
		List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
		for(String folder : folderList){
			if(folder.indexOf("-") == 4){
				String day = folder.substring(0, 10);
				
				String allFolder = Constants.jgy_path+"/"+folder+"/"+ALL_FOLDER_NAME;
				String mistakeFolder = Constants.jgy_path+"/"+folder+"/"+MISTAKE_FOLDER_NAME;
				String nothingFolder = Constants.jgy_path+"/"+folder+"/"+NOTHING_FOLDER_NAME;
				
				//遍历目录，删除没有在数据库中的文件
				commonDeleteFile(day,ALL_FOLDER_NAME,allFolder);
				commonDeleteFile(day,MISTAKE_FOLDER_NAME,mistakeFolder);
				commonDeleteFile(day,NOTHING_FOLDER_NAME,nothingFolder);
			}
		}
	}
	
	private void deleteFolder(){
		List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
		//如果三个目录同时为空，则删除整个目录
		for(String folder : folderList){
			if(folder.indexOf("-") == 4){
				String allFolder = Constants.jgy_path+"/"+folder+"/"+ALL_FOLDER_NAME;
				String mistakeFolder = Constants.jgy_path+"/"+folder+"/"+MISTAKE_FOLDER_NAME;
				String nothingFolder = Constants.jgy_path+"/"+folder+"/"+NOTHING_FOLDER_NAME;
				
				List<String> allList = FileUtil.getFullFileNames(allFolder);
				List<String> mistakeList = FileUtil.getFullFileNames(mistakeFolder);
				List<String> nothingList = FileUtil.getFullFileNames(nothingFolder);
				
				if(allList.size() == 0){
					if(FileUtil.exists(allFolder)){
						FileUtil.removeFolder(allFolder);
					}
				}
				if(mistakeList.size() == 0){
					if(FileUtil.exists(mistakeFolder)){
						FileUtil.removeFolder(mistakeFolder);
					}
				}
				if(nothingList.size() == 0){
					if(FileUtil.exists(nothingFolder)){
						FileUtil.removeFolder(nothingFolder);
					}
				}
				
				if(allList.size() == 0 && mistakeList.size() == 0){
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
				
				String allPath = Constants.jgy_path+"/"+folderName+"/"+ALL_FOLDER_NAME;
				String mistakePath = Constants.jgy_path+"/"+folderName+"/"+MISTAKE_FOLDER_NAME;
				
				int recordCount = 0;
				int mistakeCount = 0;
				
				if(FileUtil.exists(allPath)){
					recordCount = (new File(allPath).list().length)/3;
				}
				if(FileUtil.exists(mistakePath)){
					mistakeCount = (new File(mistakePath).list().length)/2;
				}
				
				String newFolderName = day+"（"+recordCount+"）";
				if(mistakeCount!=0){
					newFolderName = newFolderName +"（"+mistakeCount+"）";
				}
				if(!newFolderName.equals(folderName)){
					FileUtil.renameDirectory(Constants.jgy_path+"/"+folderName, Constants.jgy_path+"/"+newFolderName);
				}
			}
		}
	}
	
	private void writeComment() throws ParseException, IOException {
		List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
		for(String folderName : folderList){
			if(folderName.indexOf("-") == 4){
				String day = folderName.substring(0, 10);
				
				writeReadme(folderName,day);
				
				writeIndex(MiniDbUtil.getPreDay(day), day, Constants.jgy_path+"/"+folderName);
				
			}
		}
	}
	
	private void writeReadme(String folderName, String day) throws IOException {
		
		int up = MiniDbUtil.count(" select * from record where type in ('1','2','3') and day='"+day+"' and stockType in ( select code from dict where typeCode='DICT_ALL_TYPE' and sub='1') ");
		int down = MiniDbUtil.count(" select * from record where type in ('1','2','3') and day='"+day+"' and stockType in ( select code from dict where typeCode='DICT_ALL_TYPE' and sub='2') ");
	
		List<String> list = MiniDbUtil.queryForList(" select desc from note n,dict d where 1=1 and day='"+day+"' and typeCode='DICT_FEELING_TYPE' and feel=code  order by  n.day desc ,n.xh asc,createDate  ");
		String feel = CollectionUtil.toLineString(list);
		
		//写入readme.txt
		String content = "追涨【"+up+"】，阴线反转【"+down+"】。\n\n"+feel;
		FileUtil.write(Constants.jgy_path+"/"+folderName+"/readme.txt", content);
	}

	private void writeIndex(String preDay, String day, String folder) {
		
		String before_SH_CODE = "000SH_0";
		String after_SH_CODE  = "000SH_1";
		
		String before_CYB_CODE = "001CYB_0";
		String after_CYB_CODE  = "001CYB_1";
		
		//最后写入大盘的照片
		if(FileUtil.exists(Constants.out_img_path+"/"+preDay+"_SH.png")  ){
			//如果目标不存在，则写入
			if(!FileUtil.exists(folder+"/"+day+"_"+before_SH_CODE+".png")){
				FileUtil.copy(folder+"/"+day+"_"+before_SH_CODE+".png", new File(Constants.out_img_path+"/"+preDay+"_SH.png"));
			}
		}
		if(FileUtil.exists(Constants.out_img_path+"/"+preDay+"_CYB.png")  ){
			//如果目标不存在，则写入
			if(!FileUtil.exists(folder+"/"+day+"_"+before_CYB_CODE+".png")){
				FileUtil.copy(folder+"/"+day+"_"+before_CYB_CODE+".png", new File(Constants.out_img_path+"/"+preDay+"_CYB.png"));
			}
		}
		if(FileUtil.exists(Constants.out_img_path+"/"+day+"_SH.png")  ){
			//如果目标不存在，则写入
			if(!FileUtil.exists(folder+"/"+day+"_"+after_SH_CODE+".png")){
				FileUtil.copy(folder+"/"+day+"_"+after_SH_CODE+".png", new File(Constants.out_img_path+"/"+day+"_SH.png"));
			}
		}else{
			System.err.println(StringUtil.getLineInfo()+":资源【"+Constants.out_img_path+"/"+day+"_SH.png】未找到");
		}
		if(FileUtil.exists(Constants.out_img_path+"/"+day+"_CYB.png")  ){
			//如果目标不存在，则写入
			if(!FileUtil.exists(folder+"/"+day+"_"+after_CYB_CODE+".png")){
				FileUtil.copy(folder+"/"+day+"_"+after_CYB_CODE+".png", new File(Constants.out_img_path+"/"+day+"_CYB.png"));
			}
		}else{
			System.err.println(StringUtil.getLineInfo()+":资源【"+Constants.out_img_path+"/"+day+"_CYB.png】未找到");
		}
	}
	
	private void incrementToBaiduYun() throws IOException {
		if(!FileUtil.exists(Constants.baidu_path)){
			System.out.println("未找到目录【"+Constants.baidu_path+"】");
			return;
		}
		System.out.println("开始增量写入百度云");
		String baiduPath = Constants.baidu_path+"/"+getMaxVersion();
		//获取百度云中缺少的文件夹
		List<String> diff = getNeedFolder(baiduPath); 
		for(String dayFolder:diff){
			FileUtil.copyDirectiory(Constants.jgy_path+"/"+dayFolder, baiduPath+"/"+dayFolder);
			System.out.println("【"+Constants.jgy_path+"/"+dayFolder+"】------拷贝到------【"+baiduPath+"/"+dayFolder+"】");
		}
	}

	private List<String> getNeedFolder(String baiduPath) {
		List<String> list1 = FileUtil.getFullFileNames(Constants.jgy_path);
		List<String> list2 = FileUtil.getFullFileNames(baiduPath);
		
		List<String> result = CollectionUtil.different(list1, list2);
		return result;
	}

	private String getMaxVersion() {
		List<String> list = FileUtil.getFullFileNames(Constants.baidu_path);
		int max = 0;
		for(String str:list){
			if(str.startsWith("version")){
				String s = str.replace("version", "");
				int num = Integer.parseInt(s);
				if(num>max){
					max = num;
				}
			}
		}
		return "version"+max;
	}
	

	
}
