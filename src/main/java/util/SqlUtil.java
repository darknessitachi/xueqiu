package util;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SqlUtil {
	/**
	 * 日志数据插入到原始表
	 * @param stmt
	 */
	public static void insertData(Statement stmt) {
		insertData(Constants.out_path + Constants.data_path + "sheet2.txt",stmt);
		insertData(Constants.out_path + Constants.data_path + "sheet3.txt",stmt);
		insertData(Constants.out_path + Constants.data_path + "sheet4.txt",stmt);
	}
	
	
	private static void insertData(String filePath, Statement stmt) {
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
				
				//System.out.println(updateSQL);
				
				stmt.executeUpdate(updateSQL);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
