package util;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlUtil {
	/**
	 * 日志数据插入到原始表
	 * @param stmt
	 */
	public static void insertDataFromLog(Statement stmt) {
		insertData(Constants.out_path + Constants.data_path + "sheet2.txt",stmt);
		insertData(Constants.out_path + Constants.data_path + "sheet3.txt",stmt);
		insertData(Constants.out_path + Constants.data_path + "sheet4.txt",stmt);
	}
	
	/**
	 * day字段要先转换，后插入
	 * @param filePath
	 * @param stmt
	 */
	private static void insertData(String filePath, Statement stmt) {
		try {
			List<String> list = FileUtil.readLines(filePath);
			for(String line : list){
				
				String updateSQL = "INSERT INTO record VALUES( ${VALUES} )";
				
				String[] arr = line.split(",");
				StringBuilder sb = new StringBuilder();
				int i=1;
				for(String val : arr){
					if(i == 1){
						sb.append("'"+CustStringUtil.getFormatDay(val)+"'").append(",");
					}else if(StringUtil.isNumeric(val)){
						sb.append(val).append(",");
					}else{
						sb.append("'"+val+"'").append(",");
					}
					i++;
				}
				sb.append("'"+filePath+"'");
				updateSQL = updateSQL.replace("${VALUES}", sb.toString());
				//System.out.println(updateSQL);
				stmt.executeUpdate(updateSQL);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * 获取查询语句中有多少列
	 * @param stmt
	 * @param rset
	 * @param sql 
	 * @return
	 */
	public static int getColumnNum(Statement stmt, String sql) {
		int columnCount = 0;
		try {
			ResultSet rset = stmt.executeQuery(sql); 
			ResultSetMetaData rsmd = rset.getMetaData() ; 
			columnCount = rsmd.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columnCount;
	}

	public static void printSql(String sql, Statement stmt) throws SQLException {
		int columnNum = getColumnNum(stmt,sql);
		
		ResultSet rset = stmt.executeQuery(sql);
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
	
	/**
	 * 把结果集包装成list
	 * @param monthSql
	 * @param stmt
	 * @return
	 * @throws SQLException 
	 */
	public static List<String> getList(String sql, Statement stmt) throws SQLException {
		List<String> result = new ArrayList<String>();
		
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			result.add(rset.getString(1));
		}
		if (rset != null) {
			rset.close();
			rset = null;
		}
		return result;
	}

}
