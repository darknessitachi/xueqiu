package gui.worker;

import bean.Req;
import bean.Req.ReqHead;
import gui.core.StockFrame;

import java.io.IOException;
import java.util.List;

import util.Constants;
import util.StatisticUtil;
import util.TranslateUtil;

public class StatisWorker implements Runnable {

	private List<String> names;
	private StockFrame frame;
	private ReqHead head;
	private boolean delImport;

	public StatisWorker(ReqHead head, List<String> names, StockFrame frameFirst, boolean delImport) {
		this.names = names;
		this.frame = frameFirst;
		this.head = head;
		this.delImport = delImport;
	}

	@Override
	public void run() {
		
		if(delImport){
			frame.performImport(true);
		}
		
		for(String name : names){
			//获取每个板块的路径
			try {
				Req req = new Req();
				req.body = TranslateUtil.translate(name);
				req.head = this.head;
				StatisticUtil.statistic(req);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		frame.displayLabel.setText("统计完成，输出目录【"+Constants.out_result_path+"】");
		
	}

}
