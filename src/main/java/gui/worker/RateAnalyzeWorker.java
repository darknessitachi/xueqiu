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

public class RateAnalyzeWorker implements Runnable {
	
	
	private StockFrame frame;

	public RateAnalyzeWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		sqLiteProcess();
		frame.displayLabel.setText("比率分析完毕。");
	}

	private void sqLiteProcess() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:/"+Constants.out_path + Constants.data_path + Constants.db_name);
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			//先删除表
			try {
				stmt.executeUpdate("drop table record");
				conn.commit();
			} catch (Exception e) {
				//System.out.println("当前没有表record。");
			}
			
			//开始建表
			String tableSQL = FileUtil.read(Constants.out_config_path +	"/" + Constants.table_name);
			stmt.executeUpdate(tableSQL);
			
			//开始插入原始表数据
			SqlUtil.insertData(stmt);
			conn.commit();
			
			SqlUtil.printResult("select * from record order by day asc ",stmt,rset);
			//
			
			//开始查询
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	

	


}
