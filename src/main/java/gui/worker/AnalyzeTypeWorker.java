package gui.worker;

import gui.core.StockFrame;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import util.Constants;
import util.FileUtil;
import util.SqlUtil;
import util.StringUtil;

public class AnalyzeTypeWorker implements Runnable {
	
	
	private StockFrame frame;

	public AnalyzeTypeWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		sqLiteProcess();
		frame.displayLabel.setText("类型分析完毕。");
	}

	private void sqLiteProcess() {
		
		FileUtil.delete(Constants.out_path + Constants.data_path + Constants.db_name);
		
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:/"+Constants.out_path + Constants.data_path + Constants.db_name);
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			//开始建表
			String tableSQL = FileUtil.read(Constants.out_config_path +	"/" + Constants.table_name);
			stmt.executeUpdate(tableSQL);
			
			//开始插入数据
			SqlUtil.insertDataFromLog(stmt);
			conn.commit();
			
			//开始查询
			Map<String,String> map = FileUtil.readAsProperties(Constants.out_config_path +	"/" + Constants.sql_name);
			int i=1;
			while(!StringUtil.isEmpty(map.get("title"+i))){
				String title = map.get("title"+i);
				String sql = map.get("sql"+i);
				System.out.println("-------------"+title+"-------------");
				SqlUtil.printCustomSql(sql, stmt);
				i++;
			}
			
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	


}
