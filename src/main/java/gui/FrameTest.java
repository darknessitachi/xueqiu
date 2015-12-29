package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class FrameTest extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private int window_width = 610;
	private int window_height = 600;
	

	public JScrollPane jp1;
	public JPanel jp2;
	public JPanel jp3;

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

	private Container container;

	FrameTest(String title) throws ClassNotFoundException {
		super(title);
		super.setSize(window_width, window_height);
		this.setCenterLocation();
		super.setLayout(new BorderLayout());
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initWindow();
		// 显示窗口
		this.setVisible(true);
	}

	private void initWindow() {
		
		container = this.getContentPane();
		container.setLayout(null);
		
		jp1 = new JScrollPane();
		jp2 = new JPanel();
		jp3 = new JPanel();
		
	//	jp1.setLayout(null);
		jp1.setSize(300, 300);
		jp1.setBorder(BorderFactory.createTitledBorder("jp1"));
		
		//JbuttonOk.setBounds(new Rectangle(280, 20, 60, 25));
		jp1.add(JbuttonOk);
		
		
		

		
		container.add(jp1);
		container.add(jp2);
		container.add(jp3);
	}

	private void setCenterLocation() {
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation((width-window_width) /2, (height-window_height)/2 );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	
	public static void main(String[] args) throws ClassNotFoundException {
		new FrameTest("xx");
	}

}