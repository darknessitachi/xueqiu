package gui.worker;

import gui.core.StockFrame;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import util.Constants;
import util.FileUtil;
import util.StringUtil;

public class TypeAnalyzeWorker implements Runnable {
	
	
	private String tableSQL = "create table record("
			+ "day varchar(64), "
			+ "indexType varchar(64), "
			+ "chartType varchar(64), "
			+ "stockType varchar(64), "
			+ "compare varchar(64), "
			+ "phase varchar(64), "
			+ "rate number,"
			+ "fileName varchar(64) "
			+ ")";

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
			//先删除表，再建表
			stmt.executeUpdate("drop table record");
			System.out.println("删表成功。");
			conn.commit();
			//开始建表
			stmt.executeUpdate(tableSQL);
			System.out.println("建表成功。");
			//开始插入数据
			insertData(Constants.out_path + Constants.data_path + "sheet2.txt",stmt);
			insertData(Constants.out_path + Constants.data_path + "sheet3.txt",stmt);
			insertData(Constants.out_path + Constants.data_path + "sheet4.txt",stmt);
			conn.commit();
			//开始查询
			rset = stmt.executeQuery("SELECT day FROM record ");
			while (rset.next()) {
				System.out.println(rset.getString("day"));
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private void insertData(String filePath, Statement stmt) {
		try {
			List<String> list = FileUtil.readLines(filePath);
			for(String line : list){
				
				String updateSQL = "INSERT INTO record VALUES( ${VALUES} )";
				
				String[] arr = line.split(",");
				StringBuilder sb = new StringBuilder();
				for(String val : arr){
					if(StringUtil.isNumeric(val)){
						sb.append(val).append(",");
					}else{
						sb.append("'"+val+"'").append(",");
					}
				}
				sb.append("'"+filePath+"'");
				
				updateSQL = updateSQL.replace("${VALUES}", sb.toString());
				System.out.println(updateSQL);
				stmt.executeUpdate(updateSQL);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
