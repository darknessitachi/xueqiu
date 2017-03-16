package worker;

import gui.StockFrame;

import java.io.File;
import java.util.List;
import java.util.Map;

import util.Constants;
import util.FileUtil;
import util.MiniDbUtil;
import util.StringUtil;

public class WriteJgyFolderWorker  {

	private StockFrame frame;

	public WriteJgyFolderWorker(StockFrame frame) {
		this.frame = frame;
	}

	public void run() {
		if(!FileUtil.exists(Constants.jgy_path)){
			System.out.println("文件夹【"+Constants.jgy_path+"】不存在");
			return;
		}
		
		//获取所有天数，从02-17日开始
		List<String> days = MiniDbUtil.queryForList(" select distinct day from record where type in ('1','2','3') and day>='2017-02-17' ");
		StringBuilder msg = new StringBuilder();
		for(String day:days){
			String folder = Constants.jgy_path+"/"+day;
			//如果文件夹不存在，则创建后写入
			if(!FileUtil.exists(folder)){
				//创建文件夹
				FileUtil.createFolder(folder);
				
				String preDay = null;
				
				String sql = " select r.*,s.code from record r left join stock s on stockName=name where type in ('1','2','3') and day='"+day+"' order by type asc,xh asc,rate desc,createDate desc ";
				List<Map<String,Object>> list = MiniDbUtil.query(sql);
				
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
						FileUtil.copy(folder+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
					}else{
						msg.append("资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到").append("\n");
					}
					
					//导出反弹后的图片
					srcFileName = day+"_"+code.substring(1)+".png";
					targetFileName = day+"_"+code.substring(1)+"_1.png";
					if(FileUtil.exists(Constants.out_img_path+"/"+srcFileName)){
						FileUtil.copy(folder+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
					}else{
						msg.append("资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到").append("\n");
					}
					
					//导出分时图片
					srcFileName = day+"_"+code.substring(1)+"_T.png";
					targetFileName = day+"_"+code.substring(1)+"_2.png";
					if(FileUtil.exists(Constants.out_img_path+"/"+srcFileName)){
						FileUtil.copy(folder+"/"+targetFileName, new File(Constants.out_img_path+"/"+srcFileName));
					}else{
						msg.append("资源【"+Constants.out_img_path+"/"+srcFileName+"】未找到").append("\n");
					}
				}
				
				if(!StringUtil.isEmpty(preDay)){
					//最后写入大盘的照片
					FileUtil.copy(folder+"/"+day+"_SH_0.png", new File(Constants.out_img_path+"/"+preDay+"_SH.png"));
					FileUtil.copy(folder+"/"+day+"_CYB_0.png", new File(Constants.out_img_path+"/"+preDay+"_CYB.png"));
					
					FileUtil.copy(folder+"/"+day+"_SH_1.png", new File(Constants.out_img_path+"/"+day+"_SH.png"));
					FileUtil.copy(folder+"/"+day+"_CYB_1.png", new File(Constants.out_img_path+"/"+day+"_CYB.png"));
				}
				
			}
		}
		
		System.err.println(msg.toString());
		System.out.println("写入坚果云完成。");
		frame.displayLabel.setText("写入坚果云完成。");
	}

	
}
