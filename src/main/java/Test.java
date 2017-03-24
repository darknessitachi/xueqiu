import java.util.List;

import util.Constants;
import util.FileUtil;
import util.StringUtil;
import util.http.WebSpider;

public class Test {

	public static void main(String args[]) {
		
		String str = WebSpider.getContentByClass("https://www.taoguba.com.cn/Article/1655031/1", "p_wenz");

		for(String s:StringUtil.pattern(str, StringUtil.STOCK_PATTERN)){
			System.out.println(s);
		}
	}
	
	

	public static void removeFolder() {

		List<String> folderList = FileUtil.getFullFileNames(Constants.jgy_path);
		for (String folderName : folderList) {
			if (folderName.indexOf("-") == 4) {

				List<String> list = FileUtil
						.getFullFileNames(Constants.jgy_path + "/" + folderName);

				String file = FileUtil.fileLike(list, "down-");

				if (!StringUtil.isEmpty(file)) {
					FileUtil.delete(Constants.jgy_path + "/" + folderName + "/"
							+ file);
				}
			}
		}

	}

}
