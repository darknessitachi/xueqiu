package worker;

import java.io.IOException;

import util.Constants;
import util.FileUtil;
import util.XueqiuUtil;
import gui.StockFrame;

public class LoginWorker implements Runnable {
	
	private StockFrame frame;
	private String password;
	private String username;
	
	public LoginWorker(String username, String password, StockFrame stockFrame) {
		this.username = username;
		this.password = password;
		this.frame = stockFrame;
	}

	@Override
	public void run() {
		XueqiuUtil xq = new XueqiuUtil();
		String cookies = xq.login(username,password);
		try {
			FileUtil.write(Constants.out_config_path+"/"+Constants.req_cookie_name, cookies);
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.displayLabel.setText("登录成功");
	}

}
