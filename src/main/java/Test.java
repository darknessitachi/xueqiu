import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Test {
		public static void main(String[] args) throws IOException {
			
			//System.out.println(FileUtil.read("d:/test.txt"));
			
			List<String> list = new ArrayList<String>();
			
			list.add("123");
			//list.add("yangr");
			
			StringBuilder sb = new StringBuilder();
			for(String s:list){
				sb.append(",").append(s);
			}
			String resutl = "'"+sb.toString().substring(1).replace(",", "','")+"'";
			System.out.println(resutl);
		}
}
