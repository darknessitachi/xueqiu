package gui.core;

import gui.worker.ExportWorker;
import gui.worker.ImportWorker;
import gui.worker.ImportWorkerGroup;
import gui.worker.StatisWorker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

import util.FileUtil;
import util.StringUtil;
import config.Constants;

public class StockFrame extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private int window_width = 610;
	private int window_height = 600;
	
	private int scroll_width = 610;
	private int scroll_height = 370;

	private static final int GridLayoutColumn = 5;
	
	private static final String lastCustomPrefix = "A9";

	public boolean isSelectAll = false;
	
	private static final String groupName = "top";

	public JPanel jp1 = new JPanel();
	public JPanel jp2 = new JPanel();
	public JPanel jp3 = new JPanel();
	public ScrollPane scrollPane = new ScrollPane();

	public JButton JbuttonOk = new JButton("统计");
	public JButton JbuttonDelImport = new JButton("删除上传");
	public JButton JbuttonImport = new JButton("上传雪球");
	public JButton JbuttonImportGroup = new JButton("上传分组");
	public JButton JbuttonEmport = new JButton("下载雪球");
	public JButton JbuttonChoose = new JButton("导入EBK");
	public JButton JbuttonDel = new JButton("删除EBK");
	public JButton JbuttonSelectAll = new JButton("全选");

	public JTextField fieldDay = new JTextField(5);
	public JTextField fieldSleep = new JTextField(5);
	public JTextField fieldGroupName = new JTextField(5);
	public JTextField displayLabel = new JTextField(20);

	Map<String,JCheckBox> group = new HashMap<String,JCheckBox>();

	private List<String> customContent;
	private List<String> conceptContent;
	private List<String> industryContent;
	
	private Map<String,String> prefixMap;

	private JPanel jp_custom;
	private JPanel jp_concept;
	private JPanel jp_industry;
	

	public StockFrame(String title) throws ClassNotFoundException {
		super(title);
		super.setSize(window_width, window_height);
		this.setCenterLocation();
		super.setLayout(new BorderLayout());
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		prefixMap = new HashMap<String, String>();
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
		super.add(scrollPane, BorderLayout.SOUTH);

	}

	private void setCenterLocation() {
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation((width-window_width) /2, (height-window_height)/2 );
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
		scrollPane.setSize(scroll_width, scroll_height);
		jp3.setLayout(new BorderLayout());
		
		//scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		JPanel jp3_btn = new JPanel();
		//jp3_btn.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp3_btn.add(JbuttonChoose);
		jp3_btn.add(JbuttonDel);
		jp3_btn.add(JbuttonSelectAll);
		
		JPanel jp3_content = new JPanel();
		jp3_content.setLayout(new BorderLayout());
		
		jp_custom = new JPanel();
		jp_concept = new JPanel();
		jp_industry = new JPanel();
		initContentJPanel(jp_custom,this.customContent,"自选","custom");
		initContentJPanel(jp_concept,this.conceptContent,"概念","concept");
		initContentJPanel(jp_industry,this.industryContent,"行业","industry");
		
		setDefaultPrefixMap();

		jp3_content.add(jp_custom, BorderLayout.NORTH);
		jp3_content.add(jp_concept, BorderLayout.CENTER);
		jp3_content.add(jp_industry, BorderLayout.SOUTH);
		
		jp3.add(jp3_btn, BorderLayout.NORTH);
		jp3.add(jp3_content, BorderLayout.CENTER);
		scrollPane.add(jp3);
	}
	
	private void initContentJPanel(JPanel jpanel,
			List<String> content, String showName, String name) {
		
		jpanel.setName(name);
		jpanel.setBorder(BorderFactory.createTitledBorder(showName));
		jpanel.setLayout(new GridLayout(0,GridLayoutColumn));
		
		int i = 0;
		String currentGroup = null;
		for (String element : content) {
			String elementGroup = element.substring(0,1);
			String prefix = element.substring(0,2);
			//realName没有前缀（A1）和后缀（.EBK）
			String realName = element.substring(2,element.length());
			//设置前缀映射
			prefixMap.put(realName, prefix);
			
			JCheckBox cb = new JCheckBox(realName);
			cb.setName(element);
			if(currentGroup == null || elementGroup.equals(currentGroup)){
				//	System.out.println("因为【"+element+"】和上一个是同一组，所以直接添加。");
				currentGroup = elementGroup;
				jpanel.add(cb);
				i++;
			}
			//如果elementGroup不等于currentGroup，说明开始了另外一组，把当前组后面的留白补充完整
			if(!elementGroup.equals(currentGroup)){
				//计算要补几个空缺
				int blankNum = (GridLayoutColumn -  i % GridLayoutColumn) ;
				if(blankNum == GridLayoutColumn){
					blankNum = 0;
				}
				//System.out.println("因为【"+element+"】和上一组不同，所以需要把之前的空白补全，空白数【"+blankNum+"】。");
				//补完空白之后还需要再换一行
				blankNum = blankNum + GridLayoutColumn;
				for(int k=0;k<blankNum;k++){
					jpanel.add(new JLabel());
				}
				i = 1;
				jpanel.add(cb);
				currentGroup = elementGroup;
			}
			group.put(realName, cb);
			//如果是自选股，默认选中
			if(element.equals("A1自选股")){
				cb.setSelected(true);
			}
		}
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
			new Thread(new ImportWorkerGroup(names,groupName, this)).start();
		} else {
			displayLabel.setText("请选择要上传的板块。");
		}
	}

	private void performDel() {
		// 获取选中的板块
		List<String> names = getSelectNames();
		
		boolean justCustom = justCustom(names);
		if(!justCustom){
			displayLabel.setText("只能删除【自选】板块。");
			return;
		}
		
		if (names.size() > 0) {
			for(String name : names){
				FileUtil.delete(Constants.classpath+name);
			}
			refreshCustomPanel();
		} else {
			displayLabel.setText("请选择要删除的板块。");
		}
	}

	private boolean justCustom(List<String> names) {
		for(String str : names){
			if(!str.contains("custom")){
				return false;
			}
		}
		return true;
	}

	private void performChoose() {
		
		String path = StringUtil.getComputerHomeDir();
		
		JFileChooser fc = new JFileChooser(path);  
        //是否可多选  
        fc.setMultiSelectionEnabled(true);  
        //选择模式，可选择文件和文件夹  
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);  
        //设置是否显示隐藏文件  
        fc.setFileHidingEnabled(true);  
        fc.setAcceptAllFileFilterUsed(false);  
        //设置文件筛选器  
        fc.setFileFilter(new CustomFileFilter("EBK"));  
          
        int returnValue = fc.showOpenDialog(null);  
        if (returnValue == JFileChooser.APPROVE_OPTION)  
        {  
            File[] files = fc.getSelectedFiles();
            for(File file : files){
            	String fileName = addPrefix(file.getName());
            	FileUtil.copy(Constants.custom_path +"/"+fileName,file);
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
		this.customContent = FileUtil.getFileFromFolder(Constants.custom_path);
		initContentJPanel(jp_custom,this.customContent,"自选","custom");
		con.validate();
	}

	private void delCustomGroup() {
		for(String str : customContent){
			group.remove(str.substring(2));
			//System.out.println("删除key:"+jb);
		}
	}
	/**
	 * 参数name的格式是：自选股.EBK
	 * @param name
	 * @return
	 */
	private String addPrefix(String name) {
		String realName = name.split("\\.")[0];
		String prefix = prefixMap.get(realName);
		if(prefix == null){
			System.out.println("导入的EBK文件没有对应的序号，增加默认序号【"+lastCustomPrefix+"】。");
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
			writeRequestHead();
			new Thread(new StatisWorker(names, this)).start();
		} else {
			displayLabel.setText("请选择要统计的板块。");
		}
	}

	/**
	 * 执行导入
	 * @param del 
	 */
	private void performImport(boolean del) {
		// 获取选中的板块
		List<String> names = getSelectNames();
		if (names.size() > 0) {
			displayLabel.setText("正在执行上传……");
			new Thread(new ImportWorker(names,del, this)).start();
		} else {
			displayLabel.setText("请选择要上传的板块。");
		}
	}

	private void performSelectAll() {
		if (!isSelectAll) {
			for(String key:group.keySet()){
				group.get(key).setSelected(true);
			}
			isSelectAll = true;
		} else {
			for(String key:group.keySet()){
				group.get(key).setSelected(false);
			}
			isSelectAll = false;
		}
	}
	
	private void writeRequestHead() throws IOException {

		String day = fieldDay.getText();
		String sleep = fieldSleep.getText();

		String request_head_path = Constants.classpath
				+ Constants.REQ_HEAD_NAME;

		StringBuilder sb = new StringBuilder();
		sb.append("#").append("\n");
		sb.append("day=" + day).append("\n");
		sb.append("combine=true").append("\n");
		sb.append("sleep=" + sleep).append("\n");
		sb.append("filterNotice=true").append("\n");

		FileUtil.write(request_head_path, sb.toString());
	}
	/**
	 * 返回的数据格式带完整路径的：["code/custom/A1自选股.EBK","code/custom/A2垃圾回收站.EBK"]
	 * @return
	 */
	private List<String> getSelectNames() {
		List<String> result = new ArrayList<String>();
		for(String key:group.keySet()){
			JCheckBox jb = group.get(key);
			if (jb.isSelected()) {
				String parentName = jb.getParent().getName();
				String path = Constants.CODE_PATH + parentName + "/" + jb.getName()+".EBK";
				result.add(path);
			}
		}
		return result;
	}
	/**
	 * content的内容格式是：["A1自选股","A4新股"]，有前缀，没有后缀
	 */
	private void initContentData() {
		this.customContent = FileUtil.getFileFromFolder(Constants.custom_path);
		this.conceptContent = FileUtil.getFileFromFolder(Constants.concept_path);
		this.industryContent = FileUtil.getFileFromFolder(Constants.industry_path);
	}

}