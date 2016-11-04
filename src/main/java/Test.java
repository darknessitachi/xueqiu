import util.CustNumberUtil;


public class Test {

		
		public static void main(String[] args) {
			double d = 8.37;
			for(int i=0;i<5;i++){
				d = CustNumberUtil.calculateLimitUp(d);
				System.out.println(d);
			}
		}

}
