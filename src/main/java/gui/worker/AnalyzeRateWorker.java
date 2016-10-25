package gui.worker;

import gui.core.StockFrame;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import util.Constants;
import util.FileUtil;
import util.SqlUtil;

public class AnalyzeRateWorker implements Runnable {
	
	
	private static final String tableRecordCombineSheetDay = "create table recordCombineSheetDay ("
																	+ "day varchar(64), "
																	+ "rate number, "
																	+ "fileName varchar(64) "
																+ ")";
	
	private StockFrame frame;

	public AnalyzeRateWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		sqLiteProcess();
		frame.displayLabel.setText("比率分析完毕。");
	}

	private void sqLiteProcess() {
		
		FileUtil.delete(Constants.out_path + Constants.data_path + Constants.db_name);
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:/"+Constants.out_path + Constants.data_path + Constants.db_name);
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			//开始建表
			String tableRecord = FileUtil.read(Constants.out_config_path +	"/" + Constants.table_name);
			stmt.executeUpdate(tableRecord);
			stmt.executeUpdate(tableRecordCombineSheetDay);
			
			//开始插入原始表数据
			SqlUtil.insertDataFromLog(stmt);
			conn.commit();
			
			//使用1/2算法合并每个sheet中每天的数据，插入recordCombineSheetDay表
			stmt.executeUpdate(" INSERT INTO recordCombineSheetDay  SELECT day,sum(rate/2.0)/count(day) as rate0,'sheet2' FROM record where  fileName like '%sheet2%'   group by day ");
			stmt.executeUpdate(" INSERT INTO recordCombineSheetDay  SELECT day,sum(rate/2.0)/count(day) as rate0,'sheet3' FROM record where  fileName like '%sheet3%'   group by day ");
			stmt.executeUpdate(" INSERT INTO recordCombineSheetDay  SELECT day,sum(rate/2.0)/count(day) as rate0,'sheet4' FROM record where  fileName like '%sheet4%'   group by day ");
			
			SqlUtil.printSql("SELECT  * from recordCombineSheetDay order by day asc    ",stmt,rset);
			
			traditionOut("sheet2");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	private void traditionOut(String fileName) {
		
	}
	

	


}
