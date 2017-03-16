import java.io.IOException;
import java.util.List;

import util.FileUtil;


public class Test {
		public static void main(String[] args) throws IOException {
			
			String path = "D:/map/Parts";
			
			List<String> list = FileUtil.getFullFileNames(path);
			
			for(String f:list){
				
				
				String pre = f.substring(0, 4);
				
				
				System.out.println("arcpy.CopyRuntimeGdbToFileGdb_conversion(\"D:/map/Parts/"+f+"\", '"+pre+".gdb')");
				
				
			}
			
		}
}
