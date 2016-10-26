package gui.worker;

import gui.core.StockFrame;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import util.Constants;
import util.FileUtil;
import util.ProjectUtil;
import util.SqlUtil;
import util.StringUtil;

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
			
			//使用1/2算法合并每个sheet中每天的数据，并插入recordCombineSheetDay表
			stmt.executeUpdate(" INSERT INTO recordCombineSheetDay  SELECT day,sum(rate/2.0)/count(day) as rate0,'sheet2' FROM record where  fileName like '%sheet2%'   group by day ");
			stmt.executeUpdate(" INSERT INTO recordCombineSheetDay  SELECT day,sum(rate/2.0)/count(day) as rate0,'sheet3' FROM record where  fileName like '%sheet3%'   group by day ");
			stmt.executeUpdate(" INSERT INTO recordCombineSheetDay  SELECT day,sum(rate/2.0)/count(day) as rate0,'sheet4' FROM record where  fileName like '%sheet4%'   group by day ");
			
			System.out.println("-----采用每日平均算法-----");
			traditionOut("sheet2","【错过】：",stmt);
			traditionOut("sheet3","【意外（追涨）】：",stmt);
			traditionOut("sheet4","【意外（首阴）】：",stmt);
			
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

	private void traditionOut(String fileName, String title, Statement stmt) throws SQLException {
		System.out.println(title);
		
		String monthSql = "select substr(day,0 ,8) month from recordCombineSheetDay where fileName like '%"+fileName+"%' group by month order by  month asc   ";
		List<String> monthList = SqlUtil.getList(monthSql,stmt);
		
		for(String month : monthList){
			String sql = "select * from recordCombineSheetDay where fileName like '%"+fileName+"%' and day like '%"+month+"%'  order by day asc   ";
			Map<String,String> data = getCalculateData(sql,stmt);
			TreeSet<String> dayOrder = ProjectUtil.getTreeSet(data.keySet());
			float baseHalf = 1;
			for(String day : dayOrder){
				float rate = new Float(data.get(day))/100;
				baseHalf = baseHalf		*	(	1	+  rate	);
			}
			String resultHalf = StringUtil.formatNumber2((baseHalf-1)*100)+"%";
			System.out.println(month+"月预期收益 : "+resultHalf);
		}
		System.out.println();
	}
	
	
	/**
	 * 获取每天的比率数据
	 * @param sql
	 * @param stmt
	 * @return
	 * @throws SQLException 
	 */
	private Map<String, String> getCalculateData(String sql, Statement stmt) throws SQLException {
		
		Map<String,String> result = new HashMap<String, String>();
		
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			result.put(rset.getString("day"), rset.getFloat("rate")+"");
		}
		if (rset != null) {
			rset.close();
			rset = null;
		}
		return result;
		
	}


}
