import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.FileUtil;
import util.MiniExcelTemplate;
import util.StringUtil;


public class QdTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {
		List<String> list = FileUtil.getFullFileNames("D:/json");
		
		Map<String,String> map = getMap();
		
		for(String file:list){
			//开始导出一个Excel
			
			String path = "d:/json/"+file;
			String result = FileUtil.read(path);
			JSONArray array = null;
			try {
				array = JSONArray.fromObject(result);
			} catch (Exception e) {
				//e.printStackTrace();
				System.err.println(file+" 读取编码错误，重新读取");
				
				String result2 = FileUtil.read(path,"utf-8");
				array = JSONArray.fromObject(result2);
			} 
			
			
			List<String> title = null;
			List<List<Object>> data1 = new ArrayList<List<Object>>();
			for (int i = 0; i < array.size(); i++) {
				
				JSONObject obj = array.getJSONObject(i); 
				if(title==null){
					title = getListFromSet(obj.keySet());
				}
				
				List<Object> row = new ArrayList<Object>();
				for(String key:title){
					row.add(obj.get(key));
				}
				data1.add(row);
			}
			
			
			
			List<List<List<Object>>> allSheetData = new ArrayList<List<List<Object>>>();
	    	allSheetData.add(data1);
	    	
	    	List<String> sheetList = new ArrayList<String>();
	    	sheetList.add("数据");
			
	    	
	    	List<String> title_zh = getZh(title,map);
			
	    	//导出
			MiniExcelTemplate excel = new MiniExcelTemplate();
	    	excel.createExcel(sheetList,title_zh,allSheetData,null);
	    	
	    	excel.export("d:/excel/"+StringUtil.getFileName(file)+".xls");
			
			
			//导出一个Excel完成
		}
	}
	
	private static Map<String, String> getMap() throws FileNotFoundException {
		List<String> list = FileUtil.readLines("d:/map.txt","utf-8");
		Map<String,String> map = new HashMap<String,String>();
		
		for(String line:list){
			String[] arr = line.split(",");
			map.put(arr[1].toLowerCase().trim(), arr[0].trim());
		}
		return map;
	}

	private static List<String> getZh(List<String> title, Map<String, String> map) throws FileNotFoundException {
		
		List<String> newTitle = new ArrayList<String>();
		for(String t:title){
			String zh = map.get(t.toLowerCase());
			if(StringUtil.isEmpty(zh)){
				zh = t;
			}
			newTitle.add(zh);
		}
		
		return newTitle;
	}


	private static List<String> getListFromSet(Set<String> set) {
		List<String> list = new ArrayList<String>();
		for(String str:set){
			list.add(str);
		}
		return list;
	}

}
