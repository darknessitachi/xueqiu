import java.io.FileNotFoundException;

import util.CustNumberUtil;
import util.FileUtil;


public class Test {

		
		public static void main(String[] args) throws FileNotFoundException {
			System.out.println(FileUtil.read("d:/test.txt"));
		}

}
