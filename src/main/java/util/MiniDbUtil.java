package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MiniDbUtil {
	
	public static final String dbPath = "D:/xueqiu/database/test.db";
	
	
	public static List<Map<String,Object>> query(String sql) {
		
		List<Map<String,Object>> result = null;
		
		Connection conn = null;
		Statement stmt = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:/"+MiniDbUtil.dbPath);
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			result = MiniDbUtil.query(sql, stmt);
			//logger.info(sql.trim());
			System.out.println(sql.trim());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
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
		return result;
	}
	
	
	public static List<String> queryForList(String sql) {

		List<String> result = new ArrayList<String>();
		
		List<Map<String,Object>> data = query(sql);
		for(Map<String,Object> map : data){
			Object[] arr = map.keySet().toArray();
			String key = (String) arr[0];
			String value = (String) map.get(key);
			result.add(value);
		}
		
		return result;
	}
	
	public static Object queryForOne(String sql) {
		List<Map<String,Object>> list = query(sql);
		if(list!=null && list.size()>0){
			Map<String,Object> map = list.get(0);
			String key = (String) (map.keySet().toArray())[0];
			return map.get(key);
		}
		return null;
	}
	
	public static void execute(String sql) {
		
		Connection conn = null;
		Statement stmt = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:/"+MiniDbUtil.dbPath);
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			stmt.executeUpdate(sql);
			conn.commit();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
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
	

	private static List<Map<String,Object>> query(String sql,Statement stmt) {
		
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		
		try {
			ResultSet rset = stmt.executeQuery(sql); 
			List<String> fieldList = getFieldList(rset);
			
			while (rset.next()) {
				Map<String,Object> map = new HashMap<String, Object>();
				for(String f:fieldList){
					map.put(f, rset.getObject(f));
				}
				result.add(map);
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	private static List<String> getFieldList(ResultSet rset) {
		
		List<String> fieldList = new ArrayList<String>();
		
		try {
			ResultSetMetaData rsmd = rset.getMetaData() ; 
			int columnCount = rsmd.getColumnCount();
			for(int i=1;i<=columnCount;i++){
				fieldList.add(rsmd.getColumnName(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fieldList;
	}
	
	
	
	
	public static String createID() {
		 String uuid = UUID.randomUUID().toString(); 
	     uuid = uuid.replace("-", "");               
	     return uuid;
	}

	/**
	 * ²åÈë±í
	 * @param string
	 * @param data
	 */
	public static void insert(String tableName, Map<String, Object> body) {
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		for(String key:body.keySet()){
			fields.append(",").append(key);
			Object value = body.get(key);
			if(value!=null){
				if(value instanceof String){
					values.append(",").append("'").append(value).append("'");
				}else{
					values.append(",").append(value);
				}
			}
		}
		String f = fields.substring(1);
		String v = values.substring(1);
		String sql = " INSERT INTO "+tableName+" ("+f+") VALUES ("+v+") ;";
		execute(sql);
		
	}
	
	public static void batchInsert(String tableName, List<Map<String, Object>> list) {
		
		StringBuilder sb = new StringBuilder();
		
		for(Map<String, Object> body : list){
			StringBuilder fields = new StringBuilder();
			StringBuilder values = new StringBuilder();
			for(String key:body.keySet()){
				fields.append(",").append(key);
				Object value = body.get(key);
				if(value!=null){
					if(value instanceof String){
						values.append(",").append("'").append(value).append("'");
					}else{
						values.append(",").append(value);
					}
				}
			}
			String f = fields.substring(1);
			String v = values.substring(1);
			String sql = " INSERT INTO "+tableName+" ("+f+") VALUES ("+v+") ;";
			sb.append(sql);
		}
		execute(sb.toString());
	}
	
	private static String getUpdateSQL(String tableName,Map<String, Object> body, Map<String, Object> pk) {
		
		StringBuilder update = new StringBuilder();
		StringBuilder where = new StringBuilder();
		
		for(String key:body.keySet()){
			Object value = body.get(key);
			if(value!=null){
				if(value instanceof String){
					update.append(",").append(key).append("='").append(value).append("'");
				}else{
					update.append(",").append(key).append("=").append(value);
				}
			}
			
		}
		
		for(String key:pk.keySet()){
			Object value = pk.get(key);
			if(value!=null){
				if(value instanceof String){
					where.append(" and ").append(key).append("='").append(value).append("'");
				}else{
					where.append(" and ").append(key).append("=").append(value);
				}
			}
		}
		
		String u = update.toString().substring(1);
		String w = where.toString();
		
		String sql = " update "+tableName+" set "+u+" where 1=1  "+w +" ;";
		return sql;
	}
	
	private static String getUpdateSQL(String tableName,Object[] body,Object[] pk) {
		
		if(body.length%2 != 0 || pk.length%2 != 0){
			System.err.println("参数长度不正确");
			return null;
		}
		
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		Map<String, Object> pkMap = new HashMap<String, Object>();
		
		for(int i=0;i<body.length;){
			bodyMap.put((String) body[i], body[i+1]);
			i = i+2;
		}
		for(int i=0;i<pk.length;){
			pkMap.put((String) pk[i], pk[i+1]);
			i = i+2;
		}
		return getUpdateSQL(tableName,bodyMap,pkMap);
	}

	/**
	 * update record set day='day',stockName='Å£ÄÌ',rate=33.33 where id='1234567'
	 * @param tableName
	 * @param body
	 * @param pk
	 */
	public static void update(String tableName, Map<String, Object> body,
			Map<String, Object> pk) {
		String sql = getUpdateSQL(tableName,body,pk);
		execute(sql);
	}


	public static void update(String tableName,Object[] body,Object[] pk) {
		String sql = getUpdateSQL(tableName, body, pk);
		execute(sql);
	}
	
	
	public static void batchUpdateById(String tableName, List<Map<String, Object>> list){
		StringBuilder resultSQL = new StringBuilder();
		for(Map<String,Object> map : list){
			
			StringBuilder update = new StringBuilder();
			
			for(String key:map.keySet()){
				Object value = map.get(key);
				if(!"id".equals(key) && value!=null){
					if(value instanceof String){
						update.append(",").append(key).append("='").append(value).append("'");
					}else{
						update.append(",").append(key).append("=").append(value);
					}
				}
				
			}
			String u = update.toString().substring(1);
			String sql = " update "+tableName+" set "+u+" where id='"+map.get("id")+"';";
			resultSQL.append(sql);
		}
		execute(resultSQL.toString());
	}
	
	
	
	public static void delete(String tableName, Map<String, Object> pk) {
		
		StringBuilder where = new StringBuilder();
		for(String key:pk.keySet()){
			Object value = pk.get(key);
			if(value!=null){
				if(value instanceof String){
					where.append(" and ").append(key).append("='").append(value).append("'");
				}else{
					where.append(" and ").append(key).append("=").append(value);
				}
			}
		}
		String w = where.toString();
		String sql = " delete from "+tableName+" where 1=1  "+w;
		execute(sql);
	}
	
	public static void deleteById(String tableName, String id) {
		String sql = " delete from "+tableName+" where id='"+id+"' ";
		execute(sql);
	}
	
	public static void main(String[] args) {
		
		Integer max = (Integer) MiniDbUtil.queryForOne("select max(xh) maxXh from note where day='2016-12-07' ");
		System.out.println(max == null);
	}

	

	public static Integer count(String sql) {
		String newSQL = "select count(*) from (  "+sql+"  )";
		return (Integer) queryForOne(newSQL);
	}


	public static Map<String, Object> queryObj(String sql) {
		List<Map<String, Object>> list = query(sql);
		if(list.size()>0){
			return list.get(0);
		}
		return new HashMap<String, Object>();
	}

	


	


	

}