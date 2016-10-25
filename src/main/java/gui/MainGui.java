package gui;

import gui.core.StockFrame;
import util.ProjectUtil;



public class MainGui {

	public static void main(String[] args) throws ClassNotFoundException {
		ProjectUtil.validate();
		new StockFrame("stock");
	}


}
