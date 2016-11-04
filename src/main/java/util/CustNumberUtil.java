package util;

public class CustNumberUtil {
	
	public static double calculateLimitUp(double value) {
		return  Math.round(value*100*1.1)/100.0;
	}

}
