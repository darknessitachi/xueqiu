package gui.worker;

import gui.core.StockFrame;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import util.Constants;
import util.FileUtil;
import util.SqlUtil;
import util.StringUtil;

public class TypeAnalyzeWorker implements Runnable {
	
	
	private StockFrame frame;

	public TypeAnalyzeWorker(StockFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		sqLiteProcess();
		frame.displayLabel.setText("类型分析完毕。");
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
			}
			
			//开始建表
			String tableSQL = FileUtil.read(Constants.out_config_path +	"/" + Constants.table_name);
			stmt.executeUpdate(tableSQL);
			
			//开始插入数据
			SqlUtil.insertData(stmt);
			conn.commit();
			
			//开始查询
			Map<String,String> map = FileUtil.readAsProperties(Constants.out_config_path +	"/" + Constants.sql_name);
			int i=1;
			while(!StringUtil.isEmpty(map.get("title"+i))){
				String title = map.get("title"+i);
				String sql = map.get("sql"+i);
				System.out.println("-------------"+title+"-------------");
				businessQuery(stmt,rset,sql);
				i++;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 查询
	 * @param stmt
	 * @param rset
	 * @param sql 
	 * @param columnNum 
	 * @throws SQLException
	 */
	private void businessQuery(Statement stmt, ResultSet rset, String sql) throws SQLException {
		int columnNum = SqlUtil.getColumnNum(stmt,rset,sql);
		
		rset = stmt.executeQuery(sql);
		while (rset.next()) {
			StringBuilder row = new StringBuilder();
			for(int i=1;i<=columnNum;i++){
				row.append(",").append(rset.getString(i));
			}
			System.out.println(row.toString().substring(1));
		}
		if (rset != null) {
			rset.close();
			rset = null;
		}
	}



}
