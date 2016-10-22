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

	public LogAnalyzeWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		File sheet2 = new File(Constants.out_path + Constants.data_path + "sheet2.txt");
		File sheet3 = new File(Constants.out_path + Constants.data_path + "sheet3.txt");
		File sheet4 = new File(Constants.out_path + Constants.data_path + "sheet4.txt");
		
		System.out.println("-----采用每日平均算法-----");
		printOut(sheet2,"【错过】：");
		printOut(sheet3,"【意外（追涨）】：");
		printOut(sheet4,"【意外（首阴）】：");
		
		frame.displayLabel.setText("分析完毕。");
	}

	private void printOut(File file, String title) {
		try {
			
			Map<String,List<DayRecordInfo>> data = ProjectUtil.readLog(file);
			TreeSet<String> dateOrderSet = ProjectUtil.getTreeSet(data.keySet());
			System.out.println(title);
			for(String key : dateOrderSet){
				System.out.println(key+"月预期收益 : "+ProjectUtil.caculateMonthRate(data.get(key)));
			}
			System.out.println();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
