package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.filechooser.FileSystemView;

import bean.MyComparator;
import bean.Stock;


public class ProjectUtil {
	
	/**
	 * 获取工程的路径
	 * @return
	 */
	public static String getProjectPath() {
		File directory = new File("");
		String outputPath = directory.getAbsolutePath();
		return outputPath;
	}
	
	/**
	 * 获取工程class文件的路径
	 * @return
	 */
	public static String getSrcPath() {
		return getProjectPath()+"/src/main/java";
	}
	
	/**
	 * 获取工程class文件的路径
	 * @return
	 */
	public static String getClasspath() {
		return ProjectUtil.class.getClassLoader().getResource("").getPath();
	}
	
	/**
	 * 获取电脑桌面绝对路径
	 * @return
	 */
	public static String getComputerHomeDir() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		return fsv.getHomeDirectory().getAbsolutePath();
	}
	
	public static void main(String[] args) {
		System.out.println(getSrcPath());
		System.out.println(getClasspath());
		System.out.println(getProjectPath());
	}
	
	
	
	public static String getSearchUrl(Stock stock, int page) {
		int count = 20;
		String href = "http://xueqiu.com/statuses/search.json?count="+count+"&comment=0&symbol="+ stock.code+ "&hl=0&source=all&sort=time&page="
				+ page+ "&_="+new Date().getTime();
		return href;
	}
	
	
	
	
	
	/**
	 * 读取内容的行数，过滤不正确的行
	 * @param path
	 * @param filterIndex
	 * @return
	 * @throws FileNotFoundException
	 */
	public static int readValidLineNum(String path, boolean filterIndex) throws FileNotFoundException {
		int num = 0;
		//System.out.println(path);
		FileReader fr = new FileReader(new File(path));
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if (!StringUtil.isEmpty(line)) {
					if(filterIndex){
						if(!CustStringUtil.isStockIndex(line)){
							num++;
						}
					}else{
						num++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return num;
	}

	
	/**
	 * 验证工程需要的文件夹是否存在，不存在则建立
	 */
	public static void validate() {
		validateFolder();
	}
	
	private static void validateFolder() {
		if(!FileUtil.exists(Constants.out_path)){
			try {
				FileUtil.copyDirectiory(ProjectUtil.getProjectPath() +"/"+Constants.folder_name,Constants.out_path);
				System.out.println("拷贝文件夹【xueqiu】完成。");
				login();
				System.out.println("如果需要使用训练数据库，请更新。");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void login() {
		Properties params = AccessUtil.readParams();
		String username = params.getProperty("username");
		String password = params.getProperty("password");
		if (StringUtil.isEmpty(username)) {
			System.err.println("params.properties缺少用户登录信息。");
		}
		XueqiuUtil xq = new XueqiuUtil();
		String cookies = xq.login(username,password);
		try {
			FileUtil.write(Constants.out_config_path+"/"+Constants.req_cookie_name, cookies);
			System.out.println("登录成功。");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static TreeSet<String> getTreeSet(Set<String> keySet) {
		TreeSet<String> result = new TreeSet<String>(new MyComparator());
		for(String e : keySet){
			result.add(e);
		}
		return result;
	}
	
	public static boolean validateFileCount(List<String> list) {

		boolean result = true;

		int count = 0;
		for (String fileName : list) {
			if (fileName.equalsIgnoreCase("ZXG.blk") || fileName.startsWith("A1") || fileName.startsWith("A2") || fileName.startsWith("A3")  ) {
				count++;
			}
		}

		if (count != 4) {
			result = false;
		}
		return result;
	}

	public static List<String> getStockListByA3() {
		
		String installZXGPath = getInstallZXGPath();
		List<String> installZXG_FileList = FileUtil.getFullFileNames(installZXGPath);
		String A3_NAME = FileUtil.fileLike(installZXG_FileList, "A3");
		if(StringUtil.isEmpty(A3_NAME)){
			System.err.println("A3文件未找到。");
			return null;
		}
		
		List<String> result = new ArrayList<String>();
		try {
			List<String> source = FileUtil.readLines(installZXGPath + "/" + A3_NAME);
			for(String code : source){
				String completeCode = CustStringUtil.tdxCode2StandardCode(code);
				if(completeCode!=null){
						String name = TranslateUtil.getNameByCode(completeCode);
						if(TranslateUtil.isValidName(name)){
							result.add(name);
						}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public static String getInstallZXGPath() {
		
		String result = "";
		Properties params = AccessUtil.readParams();
		String installPath = params.getProperty("tdxInstallPath");
		String[] array = installPath.split(";");
		int i = 0;
		for (String path : array) {
			String zxg_path = path + "/" + Constants.zxg_path;
			File folder = new File(zxg_path);
			if (folder.exists()) {
				result = zxg_path;
				i++;
			}
		}
		 if(i > 1){
			System.err.println("找到多个券商安装目录。");
			result = "";
		}
		
		return result;
	}


}
