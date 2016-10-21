package gui.worker;

import gui.core.StockFrame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import util.Constants;
import util.core.ProjectUtil;
import core.domain.DayRecordInfo;

public class LogAnalyzeWorker implements Runnable {

	private StockFrame frame;
	private String param;

	public LogAnalyzeWorker(StockFrame frame, String param) {
		this.frame = frame;
		this.param = param;
	}

	@Override
	public void run() {
		File sheet2 = new File(Constants.out_path + Constants.data_path + "sheet2.txt");
		File sheet3 = new File(Constants.out_path + Constants.data_path + "sheet3.txt");
		File sheet4 = new File(Constants.out_path + Constants.data_path + "sheet4.txt");
		
		System.out.println("-----采用每日平均算法-----");
		printOut(sheet2,"【错过】：");
		if(param.toLowerCase().startsWith("all")){
			printOut(sheet3,"【意外（追涨）】：");
			printOut(sheet4,"【意外（首阴）】：");
		}
		
		frame.displayLabel.setText("分析完毕。");
	}

	private void printOut(File file, String title) {
		try {
			
			Map<String,List<DayRecordInfo>> sheet2Data = ProjectUtil.readLog(file);
			TreeSet<String> orderSet = ProjectUtil.getTreeSet(sheet2Data.keySet());
			System.out.println(title);
			for(String key : orderSet){
				System.out.println(key+"月预期收益 : "+ProjectUtil.caculateMonthRate(sheet2Data.get(key)));
			}
			System.out.println();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}