package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.AccessUtil;
import util.Constants;
import util.CustNumberUtil;
import util.FileUtil;
import util.MiniDbUtil;
import util.ProjectUtil;
import util.StringUtil;
import worker.ClearCloudWorker;
import worker.DownBackupWorker;
import worker.DownDatabase;
import worker.DownImgWorker;
import worker.LoginWorker;
import worker.StatisWorker;
import worker.UploadBackupWorker;
import worker.UploadDatabaseWorker;
import worker.UploadImgWorker;
import worker.UploadXueqiuWorker;
import worker.WriteJgyFolderWorker;
import bean.Req.ReqHead;

public class StockFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private int window_width = 460;
	private int scroll_height = 50;
	private int window_height = scroll_height + 260;;

	private static final int GridLayoutColumn = 4;

	private boolean isSelectAll = false;

	private JPanel jp1 = new JPanel();
	private JPanel jp2 = new JPanel();
	private JPanel jp3 = new JPanel();

	private JPanel jp_custom = new JPanel();
	private JPanel jp_concept = new JPanel();
	private JPanel jp_industry = new JPanel();

	private JMenuItem loginItem = new JMenuItem("登录");
	
	private JMenuItem uploadDbItem = new JMenuItem("上传数据库");
	private JMenuItem uploadImgItem = new JMenuItem("上传图片");
	private JMenuItem uploadCloudItem = new JMenuItem("上传备份");
	private JMenuItem uploadXqItem = new JMenuItem("上传雪球");
	private JMenuItem uploadJgyItem = new JMenuItem("写入坚果云");
	private JMenuItem uploadBothItem = new JMenuItem("一键上传");
	
	private JMenuItem downDatabaseItem = new JMenuItem("下载数据库");
	private JMenuItem downImgItem = new JMenuItem("下载图片");
	private JMenuItem downLocalItem = new JMenuItem("下载备份");
	private JMenuItem downBothItem = new JMenuItem("一键下载");
	
	private JMenuItem clearItem = new JMenuItem("清理");

	private JButton okBtn = new JButton("统计");
	private JButton priceBtn = new JButton("计算");

	private JButton chooseBtn = new JButton("导入EBK");
	private JButton autoChooseBtn = new JButton("自动导入");
	private JButton deleteBtn = new JButton("删除EBK");
	private JButton selectAllBtn = new JButton("全选");

	private JComboBox<String> dayCombo = new JComboBox<String>();
	private JTextField price = new JTextField(8);
	
	public JTextField displayLabel = new JTextField(30);

	public String installZXGPath = null;
	public String installZXGRootPath = null;
	
	private List<String> installZXG_FileList = null;
	private String ZXG_NAME = null;
	private String A2_NAME = null;
	private String A3_NAME = null;

	private Map<String, JCheckBox> group = new HashMap<String, JCheckBox>();

	private Properties params;
	private List<String> customContent;
	private List<String> conceptContent;
	private List<String> industryContent;

	public StockFrame(String title) throws ClassNotFoundException {
		super(title);
		super.setSize(window_width, window_height);
		this.setCenterLocation();
		super.setLayout(new BorderLayout());
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initWindow();
		this.setVisible(true);

		validateAndInitData();
	}

	private void validateAndInitData() {

		int i = setInstallZXGPath();
		if (i == 0) {
			showMsgBox("没有找到券商软件安装目录。");
			return;
		}else if(i > 1){
			showMsgBox("找到多个券商安装目录。");
			return;
		}

		installZXG_FileList = FileUtil.getFullFileNames(installZXGPath);
		boolean originalFileExist = ProjectUtil.validateFileCount(installZXG_FileList);
		if (!originalFileExist) {
			showMsgBox("本地证券软件目录下ZXG、A1、A2、A3的文件个数不对。");
		}

		ZXG_NAME = FileUtil.fileLike(installZXG_FileList, "ZXG.blk");
		A2_NAME = FileUtil.fileLike(installZXG_FileList, "A2");
		A3_NAME = FileUtil.fileLike(installZXG_FileList, "A3");

	}

	private void initWindow() {
		initParams();
		initContentData();

		initMenuBar();
		initJPanel1();
		initJPanel2();
		initJPanel3();
		
		addActionListener();

		super.add(jp1, BorderLayout.NORTH);
		super.add(jp2, BorderLayout.CENTER);
		super.add(jp3, BorderLayout.SOUTH);

	}

	private void addActionListener() {
		
		loginItem.addActionListener(this);
		uploadXqItem.addActionListener(this);
		uploadJgyItem.addActionListener(this);
		uploadCloudItem.addActionListener(this);
		uploadDbItem.addActionListener(this);
		uploadBothItem.addActionListener(this);
		uploadImgItem.addActionListener(this);
		downImgItem.addActionListener(this);
		clearItem.addActionListener(this);
		downLocalItem.addActionListener(this);
		downDatabaseItem.addActionListener(this);
		downBothItem.addActionListener(this);
		
		okBtn.addActionListener(this);
		selectAllBtn.addActionListener(this);
		chooseBtn.addActionListener(this);
		autoChooseBtn.addActionListener(this);
		deleteBtn.addActionListener(this);
		priceBtn.addActionListener(this);
	}
	/**
	 * 会阻塞程序的执行，直到点击确定按钮
	 * @param msg
	 */
	private void showMsgBox(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}

	private void initMenuBar() {
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menu = new JMenu("菜单");
		menu.add(loginItem);
		
		JMenu menuUp = new JMenu("上传");
		menuUp.add(uploadDbItem);
		menuUp.add(uploadImgItem);
		menuUp.add(uploadCloudItem);
		menuUp.add(uploadXqItem);
		menuUp.add(uploadJgyItem);
		menuUp.addSeparator();
		menuUp.add(uploadBothItem);
		
		JMenu menuDown = new JMenu("下载");
		menuDown.add(downDatabaseItem);
		menuDown.add(downImgItem);
		menuDown.add(downLocalItem);
		menuDown.addSeparator();
		menuDown.add(downBothItem);
		
		JMenu clear = new JMenu("操作");
		clear.add(clearItem);
		
		menuBar.add(menu);
		menuBar.add(menuUp);
		menuBar.add(menuDown);
		menuBar.add(clear);
		this.setJMenuBar(menuBar);
		
	}

	private void initParams() {
		this.params = AccessUtil.readParams();
	}

	private void setCenterLocation() {
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation((width - window_width) / 2,
				(height - window_height) / 2);
	}

	private void initJPanel1() {
		jp1.setBorder(BorderFactory.createTitledBorder("操作"));
		jp1.add(okBtn);
		jp1.add(priceBtn);
	}

	private void initJPanel2() {
		jp2.setBorder(BorderFactory.createTitledBorder("参数"));
		jp2.add(new JLabel("day:"));
		jp2.add(dayCombo);
		jp2.add(new JLabel("price:"));
		jp2.add(price);
		
		initDefaultParams();

		jp2.add(displayLabel);
		displayLabel.setEditable(false);
		displayLabel.setText("请选择。");
	}

	private void initDefaultParams() {
		initCombo(dayCombo,params.getProperty("day"));
	}

	private void initCombo(JComboBox<String> combo, String param) {
		if (!StringUtil.isEmpty(param)) {
			String[] array = param.split(",");
			String widthBlank = getWidthBlank();
			for (String e : array) {
				combo.addItem(widthBlank + e + widthBlank);
			}
		}
	}

	private String getWidthBlank() {
		int width = Integer.parseInt(params.getProperty("dayFieldWidth"));
		String fillWord = "";
		for (int i = 0; i < width; i++) {
			fillWord = fillWord + " ";
		}
		return fillWord;
	}

	private void initJPanel3() {
		jp3.setLayout(new BorderLayout());

		JPanel jp3_btn = get_jp3_btn();
		ScrollPane jp3_content = get_jp3_content();

		jp3.add(jp3_btn, BorderLayout.NORTH);
		jp3.add(jp3_content, BorderLayout.CENTER);
		
		String showBelowPanel = (String) params.get("showBelowPanel");
		if (!StringUtil.isEmpty(showBelowPanel) && showBelowPanel.equals("true")) {
			jp3.setVisible(true);
		}else{
			jp3.setVisible(false);
		}
	}

	private ScrollPane get_jp3_content() {
		JPanel jp3_content_temp = new JPanel();
		jp3_content_temp.setLayout(new BorderLayout());

		initContentJPanel(jp_custom, this.customContent, "自选", "custom");
		String showOtherPanel = (String) params.get("showOtherPanel");
		if (!StringUtil.isEmpty(showOtherPanel) && showOtherPanel.equals("true")) {
			initContentJPanel(jp_concept, this.conceptContent, "概念", "concept");
			initContentJPanel(jp_industry, this.industryContent, "行业","industry");
		}

		jp3_content_temp.add(jp_custom, BorderLayout.NORTH);
		jp3_content_temp.add(jp_concept, BorderLayout.CENTER);
		jp3_content_temp.add(jp_industry, BorderLayout.SOUTH);

		ScrollPane jp3_content = new ScrollPane();
		jp3_content.setSize(window_width, scroll_height);
		jp3_content.add(jp3_content_temp);

		return jp3_content;
	}

	private JPanel get_jp3_btn() {
		JPanel jp3_btn = new JPanel();
		// jp3_btn.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp3_btn.add(autoChooseBtn);
		jp3_btn.add(chooseBtn);
		jp3_btn.add(deleteBtn);
		jp3_btn.add(selectAllBtn);
		return jp3_btn;
	}

	private void initContentJPanel(JPanel jpanel, List<String> content,
			String showName, String name) {

		jpanel.setName(name);
		jpanel.setBorder(BorderFactory.createTitledBorder(showName));
		jpanel.setLayout(new GridLayout(0, GridLayoutColumn));

		int i = 0;
		String currentGroup = null;
		for (String element : content) {
			// element 有前缀，没后缀，如：A1自选股
			String elementGroup = element.substring(0, 1);
			// realName没有前缀（A1）和后缀（.EBK）
			String realName = element.substring(2, element.length());
			// 板块名字后面加上个股数量
			String realNameWithNum = null;
			try {
				realNameWithNum = addSuffixWithNum(name, element);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			JCheckBox cb = new JCheckBox(realNameWithNum);
			cb.setName(element);
			if (currentGroup == null || elementGroup.equals(currentGroup)) {
				// System.out.println("因为【"+element+"】和上一个是同一组，所以直接添加。");
				currentGroup = elementGroup;
				jpanel.add(cb);
				i++;
			}
			// 如果elementGroup不等于currentGroup，说明开始了另外一组，把当前组后面的留白补充完整
			if (!elementGroup.equals(currentGroup)) {
				// 计算要补几个空缺
				int blankNum = (GridLayoutColumn - i % GridLayoutColumn);
				if (blankNum == GridLayoutColumn) {
					blankNum = 0;
				}
				// System.out.println("因为【"+element+"】和上一组不同，所以需要把之前的空白补全，空白数【"+blankNum+"】。");
				// 补完空白之后还需要再换一行
				blankNum = blankNum + GridLayoutColumn;
				for (int k = 0; k < blankNum; k++) {
					jpanel.add(new JLabel());
				}
				i = 1;
				jpanel.add(cb);
				currentGroup = elementGroup;
			}
			group.put(realName, cb);
		}
	}

	private String addSuffixWithNum(String subpath, String element)
			throws FileNotFoundException {
		String path = Constants.out_path + Constants.code_path + subpath + "/"
				+ element + ".EBK";
		String realName = element.substring(2, element.length());
		int num = 0;

		if (realName.equals("自选股")) {
			num = ProjectUtil.readValidLineNum(path, true);
		} else {
			num = ProjectUtil.readValidLineNum(path, false);
		}
		return realName + "（" + num + "）";
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == okBtn) {
			try {
				performOk();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (e.getSource() == selectAllBtn) {
			performSelectAll();
		}
		if (e.getSource() == chooseBtn) {
			performChoose();
		}
		if (e.getSource() == autoChooseBtn) {
			performAutoChoose();
		}
		if (e.getSource() == deleteBtn) {
			performDel();
		}
		

		if (e.getSource() == loginItem) {
			performLogin();
		}
		
		if (e.getSource() == uploadXqItem) {
			performImport(false);
		}
		if (e.getSource() == uploadJgyItem) {
			performUploadJgy();
		}
		
		if (e.getSource() == uploadCloudItem) {
			performUploadCloud();
		}
		if (e.getSource() == uploadDbItem) {
			performUploadDb();
		}
		
		if (e.getSource() == uploadBothItem) {
			performImport(true);
		}
		if (e.getSource() == uploadImgItem) {
			performUploadImg();
		}
		if (e.getSource() == downImgItem) {
			performDownImg();
		}
		
		if (e.getSource() == clearItem) {
			performClearCloud();
		}
		
		if (e.getSource() == downLocalItem) {
			performSyncLocal(false);
		}
		if (e.getSource() == downDatabaseItem) {
			performDownDatabase(false);
		}
		if (e.getSource() == downBothItem) {
			performSyncLocal(true);
		}
		
		if (e.getSource() == priceBtn) {
			performPrice();
		}
	}

	public void performUploadJgy() {
		final StockFrame _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				new WriteJgyFolderWorker(_this).run();
			}
		}).start();
	}

	private void performClearCloud() {
		final StockFrame _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				new ClearCloudWorker(_this).run();
			}
		}).start();
	}

	public void performDownImg() {
		final StockFrame _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				new DownImgWorker(_this).run();
			}
		}).start();
	}

	public void performUploadImg() {
		final StockFrame _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				new UploadImgWorker(_this).run();
			}
		}).start();
	}

	public void performDownDatabase(final boolean removeAlert) {
		final StockFrame _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(removeAlert){
					new DownDatabase(_this).run();
					return;
				}
				int res = JOptionPane.showConfirmDialog(null, "请确认云端的训练数据库是最新的。要继续执行同步吗？", null,JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (res == JOptionPane.YES_OPTION) {
					displayLabel.setText("正在执行本地同步……");
					new DownDatabase(_this).run();
				}else{
					displayLabel.setText("取消本地同步。");
				}
			}
		}).start();
	}

	public void performUploadDb() {
		final StockFrame _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				new UploadDatabaseWorker(_this).run();
			}
		}).start();
	}

	private void performPrice() {
		String text = price.getText();
		if(StringUtil.isEmpty(text)){
			displayLabel.setText("请输入价格。");
			return;
		}
		if(!StringUtil.isNumeric(text)){
			displayLabel.setText("请输入数字。");
			return;
		}
		double f = Double.parseDouble(text);
		displayLabel.setText(CustNumberUtil.calculateLimitUp(f)+"");
	}

	public void performUploadCloud() {
		final StockFrame _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				new UploadBackupWorker(_this).run();
			}
		}).start();
	}


	public void performAutoChoose() {
		// 先隐藏，然后再显示，解决下拉框被自选股覆盖的问题。
		dayCombo.setVisible(false);

		// 先删除自选股
		for (String name : this.customContent) {
			FileUtil.delete(Constants.out_custom_path + "/" + name + ".EBK");
		}
		// 拷贝自选股
		copyStockFile();
		// 刷新组件
		refreshCustomPanel();
		dayCombo.setVisible(true);
		isSelectAll = false;
		displayLabel.setText("自动导入完成。");

	}

	/**
	 * 同步雪球的自选股到本地的板块
	 * @param continueDownDb 
	 */
	private void performSyncLocal(final boolean continueDownDb) {
		final StockFrame _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				String msg = "请确认当前是备用机。要继续执行同步吗？";
				if(continueDownDb){
					msg = "请确认当前是备用机，并且云端的数据库是最新的吗？";
				}
				int res = JOptionPane.showConfirmDialog(null, msg, null,JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (res == JOptionPane.YES_OPTION) {
					displayLabel.setText("正在执行本地同步……");
					new DownBackupWorker(_this,continueDownDb).run();
				}else{
					displayLabel.setText("取消本地同步。");
				}
			}
		}).start();
	}

	/**
	 * 获取本地券商软件安装目录
	 * 
	 * @return
	 */
	private int setInstallZXGPath() {

		String installPath = params.getProperty("tdxInstallPath");
		String[] array = installPath.split(";");
		int i = 0;
		for (String path : array) {
			String zxg_path = path + "/" + Constants.zxg_path;
			File folder = new File(zxg_path);
			if (folder.exists()) {
				this.installZXGPath = zxg_path;
				this.installZXGRootPath = path;
				i++;
			}
		}
		return i;
	}

	/**
	 * 拷贝安装目录下的自选股A1，A2，A3到custom目录
	 * 
	 * @param path
	 */
	private void copyStockFile() {
		FileUtil.copy(Constants.out_custom_path + "/A1自选股.EBK", new File(
				installZXGPath + "/" + ZXG_NAME));
		FileUtil.copy(Constants.out_custom_path + "/A2目标股.EBK", new File(
				installZXGPath + "/" + A2_NAME));
		FileUtil.copy(Constants.out_custom_path + "/A3第二天看好.EBK", new File(
				installZXGPath + "/" + A3_NAME));
	}

	

	private void performLogin() {
		String username = params.getProperty("username");
		String password = params.getProperty("password");
		if (StringUtil.isEmpty(username)) {
			System.err.println("params.properties缺少用户登录信息。");
		}
		new Thread(new LoginWorker(username, password, this)).start();
	}

	private void performDel() {
		// 获取选中的板块
		List<String> names = getSelectNames();

		boolean justCustom = justCustom(names);
		if (!justCustom) {
			displayLabel.setText("只能删除【自选】板块。");
			return;
		}

		if (names.size() > 0) {
			for (String name : names) {
				FileUtil.delete(name);
			}
			refreshCustomPanel();
		} else {
			displayLabel.setText("请选择要删除的板块。");
		}
	}

	private boolean justCustom(List<String> names) {
		for (String str : names) {
			if (!str.contains("custom")) {
				return false;
			}
		}
		return true;
	}

	private void performChoose() {

		String path = ProjectUtil.getComputerHomeDir();

		JFileChooser fc = new JFileChooser(path);
		// 是否可多选
		fc.setMultiSelectionEnabled(true);
		// 选择模式，可选择文件和文件夹
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		// 设置是否显示隐藏文件
		fc.setFileHidingEnabled(true);
		fc.setAcceptAllFileFilterUsed(false);
		// 设置文件筛选器
		fc.setFileFilter(new CustomFileFilter("EBK"));

		int returnValue = fc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			for (File file : files) {
				FileUtil.copy(Constants.out_custom_path + "/" + file.getName(),
						file);
			}
			refreshCustomPanel();
		}
	}

	private void refreshCustomPanel() {
		Container con = this.getContentPane();

		con.invalidate();
		jp_custom.removeAll();
		delCustomGroup();
		con.validate();

		con.invalidate();
		this.customContent = FileUtil.getFileNames(Constants.out_custom_path);
		initContentJPanel(jp_custom, this.customContent, "自选", "custom");
		con.validate();
	}

	private void delCustomGroup() {
		for (String str : customContent) {
			group.remove(str.substring(2));
		}
	}

	/**
	 * 执行统计
	 * 
	 * @throws IOException
	 */
	private void performOk() throws IOException {
		// 获取选中的板块
		List<String> names = getSelectNames();
		if (names.size() > 0) {
			displayLabel.setText("正在执行统计……");
			ReqHead head = getReqHead();
			new Thread(new StatisWorker(head, names, this)).start();
		} else {
			displayLabel.setText("请选择要统计的板块。");
		}
	}

	/**
	 * 执行导入
	 * @param continueUploadCloud 
	 * 
	 * @param del
	 */
	public void performImport(final boolean continueUploadCloud) {
		final StockFrame _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				String forecastDay = StringUtil.getForecastDay();
				
				int count = MiniDbUtil.count(" select * from backup where forecastDay='"+forecastDay+"' ");
				if(count == 0){
					showMsgBox("没有备份backup，不能上传。");
					return;
				}
				//先自动导入
				performAutoChoose();
				//全选中
				performSelectAll();
				// 获取选中的板块
				List<String> names = getSelectNames();
				if (names.size() > 0) {
					displayLabel.setText("正在执行上传……");
					new UploadXueqiuWorker(names, _this,continueUploadCloud).run();
				} else {
					displayLabel.setText("请选择要上传的板块。");
				}
			}
		}).start();
	}

	private void performSelectAll() {
		if (!isSelectAll) {
			for (String key : group.keySet()) {
				group.get(key).setSelected(true);
			}
			isSelectAll = true;
		} else {
			for (String key : group.keySet()) {
				group.get(key).setSelected(false);
			}
			isSelectAll = false;
		}
	}

	private ReqHead getReqHead() throws IOException {

		ReqHead head = new ReqHead();

		head.day = Integer.parseInt(dayCombo.getSelectedItem().toString().trim());
		head.sleep = Integer.parseInt(params.getProperty("sleep"));
		head.threadNum = Integer.parseInt(params.getProperty("thread"));
		head.errWaitTime = Integer.parseInt(params.getProperty("errWaitTime"));
		head.addTime = Integer.parseInt(params.getProperty("addTime"));

		return head;
	}

	/**
	 * 返回绝对路径
	 * 
	 * @return
	 */
	private List<String> getSelectNames() {
		List<String> result = new ArrayList<String>();
		for (String key : group.keySet()) {
			JCheckBox jb = group.get(key);
			if (jb.isSelected()) {
				String parentName = jb.getParent().getName();
				String absolutePath = Constants.out_path + Constants.code_path
						+ parentName + "/" + jb.getName() + ".EBK";
				result.add(absolutePath);
			}
		}
		return result;
	}

	/**
	 * content的内容格式是：["A1自选股","A4新股"]，有前缀，没有后缀
	 */
	private void initContentData() {
		this.customContent = FileUtil.getFileNames(Constants.out_custom_path);
		this.conceptContent = FileUtil.getFileNames(Constants.out_concept_path);
		this.industryContent = FileUtil
				.getFileNames(Constants.out_industry_path);
	}



}
