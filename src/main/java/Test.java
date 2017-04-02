import java.util.List;

import util.Constants;
import util.FileUtil;
import util.StringUtil;

public class Test {

	public static void main(String args[]) {
		System.out.println("1".indexOf("."));
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
