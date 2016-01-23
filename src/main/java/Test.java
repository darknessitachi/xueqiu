import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;








public class Test {

	public static void main(String[] args) throws IOException {
		
		Properties prop = new Properties();
		InputStream fis = new FileInputStream("D:/xueqiu/config/params.properties");
		//从输入流中读取属性列表（键和元素对）
		prop.load(fis);
		System.out.println(prop.get("groupName"));
	}
	

}
