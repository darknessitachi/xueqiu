package gui.core;

import func.domain.Req.ReqHead;
import gui.worker.ExportWorker;
import gui.worker.ImportWorker;
import gui.worker.ImportGroupWorker;
import gui.worker.StatisWorker;

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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.Constants;
import util.FileUtil;
import util.ProjectUtil;

public class StockFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final String lastCustomPrefix = "A9";
	private static final String groupName = "top";

	private int window_width = 700;
	private int window_height = 600;

	private int scroll_width = window_width;
	private int scroll_height = 370;

	private static final int GridLayoutColumn = 5;

	private boolean isSelectAll = false;

	private JPanel jp1 = new JPanel();
	private JPanel jp2 = new JPanel();
	private JPanel jp3 = new JPanel();

	private JPanel jp_custom = new JPanel();
	private JPanel jp_concept = new JPanel();
	private JPanel jp_industry = new JPanel();

	private JButton JbuttonOk = new JButton("统计");
	private JButton JbuttonDelImport = new JButton("删除上传");
	private JButton JbuttonImport = new JButton("上传雪球");
	private JButton JbuttonImportGroup = new JButton("上传分组");
	private JButton JbuttonEmport = new JButton("下载雪球");
	private JButton JbuttonChoose = new JButton("导入EBK");
	private JButton JbuttonDel = new JButton("删除EBK");
	private JButton JbuttonSelectAll = new JButton("全选");

	private JTextField fieldDay = new JTextField(5);
	private JTextField fieldSleep = new JTextField(5);
	private JTextField fieldGroupName = new JTextField(5);
	public JTextField displayLabel = new JTextField(25);

	private Map<String, JCheckBox> group = new HashMap<String, JCheckBox>();

	private List<String> customContent;
	private List<String> conceptContent;
	private List<String> industryContent;

	private Map<String, String> prefixMap = new HashMap<String, String>();

	public StockFrame(String title) throws ClassNotFoundException {
		super(title);
		super.setSize(window_width, window_height);
		this.setCenterLocation();
		super.setLayout(new BorderLayout());
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		initWindow();
		this.setVisible(true);
	}

	private void initWindow() {

		initContentData();

		initJPanel1();
		initJPanel2();
		initJPanel3();

		super.add(jp1, BorderLayout.NORTH);
		super.add(jp2, BorderLayout.CENTER);
		super.add(jp3, BorderLayout.SOUTH);

	}

	private void setCenterLocation() {
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation((width - window_width) / 2,
				(height - window_height) / 2);
	}

	private void initJPanel1() {
		jp1.setBorder(BorderFactory.createTitledBorder("按钮"));
		jp1.add(JbuttonOk);
		jp1.add(JbuttonDelImport);
		jp1.add(JbuttonImport);
		jp1.add(JbuttonEmport);
		jp1.add(JbuttonImportGroup);

		JbuttonOk.addActionListener(this);
		JbuttonImport.addActionListener(this);
		JbuttonImportGroup.addActionListener(this);
		JbuttonEmport.addActionListener(this);
		JbuttonSelectAll.addActionListener(this);
		JbuttonChoose.addActionListener(this);
		JbuttonDelImport.addActionListener(this);
		JbuttonDel.addActionListener(this);
	}

	private void initJPanel2() {
		jp2.setBorder(BorderFactory.createTitledBorder("输入参数"));
		jp2.add(fieldDay);
		jp2.add(fieldSleep);
		jp2.add(fieldGroupName);
		jp2.add(displayLabel);

		fieldDay.setText("1");
		fieldSleep.setText("1000");
		fieldGroupName.setText(groupName);
		displayLabel.setEditable(false);
		displayLabel.setText("请选择。");

	}

	private void initJPanel3() {
		jp3.setLayout(new BorderLayout());

		JPanel jp3_btn = get_jp3_btn();
		ScrollPane jp3_content = get_jp3_content();

		jp3.add(jp3_btn, BorderLayout.NORTH);
		jp3.add(jp3_content, BorderLayout.CENTER);
	}

	private ScrollPane get_jp3_content() {
		JPanel jp3_content_temp = new JPanel();
		jp3_content_temp.setLayout(new BorderLayout());

		initContentJPanel(jp_custom, this.customContent, "自选", "custom");
		initContentJPanel(jp_concept, this.conceptContent, "概念", "concept");
		initContentJPanel(jp_industry, this.industryContent, "行业", "industry");

		setDefaultPrefixMap();

		jp3_content_temp.add(jp_custom, BorderLayout.NORTH);
		jp3_content_temp.add(jp_concept, BorderLayout.CENTER);
		jp3_content_temp.add(jp_industry, BorderLayout.SOUTH);

		ScrollPane jp3_content = new ScrollPane();
		jp3_content.setSize(scroll_width, scroll_height);
		jp3_content.add(jp3_content_temp);

		return jp3_content;
	}

	private JPanel get_jp3_btn() {
		JPanel jp3_btn = new JPanel();
		// jp3_btn.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp3_btn.add(JbuttonChoose);
		jp3_btn.add(JbuttonDel);
		jp3_btn.add(JbuttonSelectAll);
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
			String prefix = element.substring(0, 2);
			// realName没有前缀（A1）和后缀（.EBK）
			String realName = element.substring(2, element.length());
			// 设置前缀映射
			prefixMap.put(realName, prefix);
			//板块名字后面加上个股数量
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
			// 如果是自选股，默认选中
			/*
			 * if(realName.equals("自选股")){ cb.setSelected(true); }
			 */
		}
	}

	private String addSuffixWithNum(String subpath, String element) throws FileNotFoundException {
		String path = Constants.out_path + Constants.code_path + subpath + "/"
				+ element + ".EBK";
		String realName = element.substring(2, element.length());
		int num = 0;
		if (realName.equals("自选股") || realName.equals("垃圾回收站")) {
			num = FileUtil.readValidLineNum(path, true);
		}else{
			num = FileUtil.readValidLineNum(path, false);
		}
		return realName + "（" + num + "）";
	}

	private void setDefaultPrefixMap() {
		this.prefixMap.put("自选股", "A1");
		this.prefixMap.put("垃圾回收站", "A2");
		this.prefixMap.put("强势股", "A3");
		this.prefixMap.put("新股", "A4");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == JbuttonOk) {
			try {
				performOk();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (e.getSource() == JbuttonImport) {
			performImport(false);
		}

		if (e.getSource() == JbuttonImportGroup) {
			performImportGroup();
		}

		if (e.getSource() == JbuttonSelectAll) {
			performSelectAll();
		}

		if (e.getSource() == JbuttonEmport) {
			performExport();
		}

		if (e.getSource() == JbuttonChoose) {
			performChoose();
		}
		if (e.getSource() == JbuttonDel) {
			performDel();
		}
		if (e.getSource() == JbuttonDelImport) {
			performImport(true);
		}

	}

	private void performImportGroup() {
		// 获取选中的板块
		List<String> names = getSelectNames();
		if (names.size() > 0) {
			displayLabel.setText("正在执行上传分组……");

			String groupName = fieldGroupName.getText();
			new Thread(new ImportGroupWorker(names, groupName, this)).start();
		} else {
			displayLabel.setText("请选择要上传的板块。");
		}
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

		String path = getHistoryPath();
		if (path == null) {
			path = ProjectUtil.getComputerHomeDir();
		}

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
				String fileName = addPrefix(file.getName());
				// System.out.println(file.getAbsolutePath());
				FileUtil.copy(Constants.out_custom_path + "/" + fileName, file);
			}
			refreshCustomPanel();
		}
	}

	/**
	 * 获取最近打开的目录，config/temp.txt
	 * 
	 * @return
	 */
	private String getHistoryPath() {
		String temp_path = Constants.out_config_path + "/"
				+ Constants.temp_name;
		File file = new File(temp_path);
		if (file.exists()) {
			try {
				return FileUtil.read(temp_path);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void refreshCustomPanel() {
		Container con = this.getContentPane();

		con.invalidate();
		jp_custom.removeAll();
		delCustomGroup();
		con.validate();

		con.invalidate();
		this.customContent = FileUtil
				.getFileFromFolder(Constants.out_custom_path);
		initContentJPanel(jp_custom, this.customContent, "自选", "custom");
		con.validate();
	}

	private void delCustomGroup() {
		for (String str : customContent) {
			group.remove(str.substring(2));
		}
	}

	/**
	 * 参数name的格式是：自选股.EBK
	 * 
	 * @param name
	 * @return
	 */
	private String addPrefix(String name) {
		String realName = name.split("\\.")[0];
		String prefix = prefixMap.get(realName);
		if (prefix == null) {
			System.out.println("导入的EBK文件没有对应的序号，增加默认序号【" + lastCustomPrefix
					+ "】。");
			prefix = lastCustomPrefix;
		}
		return prefix + name;
	}

	private void performExport() {
		displayLabel.setText("正在执行下载……");
		new Thread(new ExportWorker(this)).start();
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
	 * 
	 * @param del
	 */
	private void performImport(boolean del) {
		// 获取选中的板块
		List<String> names = getSelectNames();
		if (names.size() > 0) {
			displayLabel.setText("正在执行上传……");
			new Thread(new ImportWorker(names, del, this)).start();
		} else {
			displayLabel.setText("请选择要上传的板块。");
		}
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
		head.day = Integer.parseInt(fieldDay.getText());
		head.sleep = Integer.parseInt(fieldSleep.getText());

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
		this.customContent = FileUtil
				.getFileFromFolder(Constants.out_custom_path);
		this.conceptContent = FileUtil
				.getFileFromFolder(Constants.out_concept_path);
		this.industryContent = FileUtil
				.getFileFromFolder(Constants.out_industry_path);
	}

}