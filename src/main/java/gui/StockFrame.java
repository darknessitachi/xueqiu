package gui;

import gui.worker.ImportWorker;
import gui.worker.StatisWorker;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.FileUtil;
import config.Constants;

public class StockFrame extends JFrame implements ActionListener{
	
	

	private static final long serialVersionUID = 1L;
	
	public boolean isSelectAll = false;
	
	public JPanel jp1 = new JPanel();
	public JPanel jp2 = new JPanel();
	public JPanel jp3 = new JPanel();
	

	public JButton JbuttonOk = new JButton("统计");
	public JButton JbuttonImport = new JButton("导入雪球");
	public JButton JbuttonSelectAll = new JButton("全选");
	
	
	public JTextField field1 = new JTextField(20);
	public JTextField field2 = new JTextField(20);
	public JTextField displayLabel = new JTextField(20);
	
	List<JCheckBox> group = new ArrayList<JCheckBox>();
	
	private List<String> groupContent;

	private int window_width = 400;
	private int window_height = 500;

	StockFrame(String title) throws ClassNotFoundException {
		
		super(title);
		initWindow();
		// 显示窗口
		this.setVisible(true);
	}
	
	private void initWindow() {
		
		super.setSize(window_width , window_height);
		initWindowLocation();
		super.setLayout(new BorderLayout(10,10));
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		jp1.add(JbuttonOk);
		jp1.add(JbuttonImport);
		jp1.add(JbuttonSelectAll);
		
		jp2.add(field1);
		jp2.add(field2);
		jp2.add(displayLabel);
		
		JbuttonOk.addActionListener(this);
		JbuttonImport.addActionListener(this);
		JbuttonSelectAll.addActionListener(this);
		
		field1.setText("2");
		field2.setText("1000");
		displayLabel.setEditable(false);
		displayLabel.setText("请选择。");
		
		jp1.setBorder(BorderFactory.createTitledBorder("按钮"));
		jp2.setBorder(BorderFactory.createTitledBorder("输入参数"));
		jp3.setBorder(BorderFactory.createTitledBorder("板块"));
		
		jp1.setSize(window_width, 100);
		jp2.setSize(window_width, 100);
		jp3.setSize(window_width, 250);
		
		super.add(jp1,BorderLayout.NORTH);
		super.add(jp2,BorderLayout.CENTER);
		super.add(jp3,BorderLayout.SOUTH);
		
		initGroup();
	}


	
	private void initWindowLocation() {
		super.setLocation(450, 120);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == JbuttonOk){
			try {
				performOk();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if(e.getSource() == JbuttonImport){
			performImport();
		}
		
		if(e.getSource() == JbuttonSelectAll){
			performSelectAll();
		}
		
	}

	/**
	 * 执行统计
	 * @throws IOException 
	 */
	private void performOk() throws IOException {
		//获取选中的板块
		List<String> names =  getSelectNames();
		if(names.size() > 0){
			displayLabel.setText("正在执行统计……");
			writeRequestHead();
			new Thread(new StatisWorker(names, this)).start();
		}else{
			displayLabel.setText("请选择1个或多个板块。");
		}
	}
	
	private void writeRequestHead() throws IOException {
		
		String day = field1.getText();
		String sleep = field2.getText();
		
		String request_head_path = Constants.classpath + Constants.REQ_HEAD_NAME;
		
		StringBuilder sb = new StringBuilder();
		sb.append("#").append("\n");
		sb.append("day="+day).append("\n");
		sb.append("combine=true").append("\n");
		sb.append("sleep="+sleep).append("\n");
		sb.append("filterNotice=true").append("\n");
		
		FileUtil.write(request_head_path, sb.toString());
	}

	/**
	 * 执行导入
	 */
	private void performImport() {
		
		//获取选中的板块
		List<String> names =  getSelectNames();
		if(names.size() > 0){
			displayLabel.setText("正在执行导入……");
			new Thread(new ImportWorker(names, this)).start();
		}else{
			displayLabel.setText("请选择1个或多个板块。");
		}
	}
	

	private void performSelectAll() {
		if(!isSelectAll){
			for(JCheckBox jb : group){
				jb.setSelected(true);
			}
			isSelectAll = true;
		}else{
			for(JCheckBox jb : group){
				jb.setSelected(false);
			}
			isSelectAll = false;
		}
	}

	private List<String> getSelectNames() {
		List<String> result = new ArrayList<String>();
		for(JCheckBox jb : group){
			if(jb.isSelected()){
				result.add(jb.getText());
			}
		}
		return result;
	}

	private void initGroup() {
		jp3.setLayout(new GridLayout(0, 3));
		initGroupContent();
		for(String name : groupContent){
			JCheckBox cb = new JCheckBox(name);
			group.add(cb);
			jp3.add(cb);
		}
	}

	private void initGroupContent() {
		List<String> result = new ArrayList<String>();
		String path = Constants.classpath + Constants.CODE_PATH;
		File file = new File(path);
		File[] tempList = file.listFiles();
		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
				String name = tempList[i].getName().split("\\.")[0];
				result.add(name);
			}
		}
		this.groupContent = result;
	}

	

	

}