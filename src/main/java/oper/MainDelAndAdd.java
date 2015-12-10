package oper;

import java.io.IOException;

public class MainDelAndAdd {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		StockOpertion oper = new StockOpertion();
		
		oper.delAll();
		oper.addAll();
		
	}

}
