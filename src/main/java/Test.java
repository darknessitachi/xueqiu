
public class Test {

	public static void main(String args[]) {
		String weekPath = "2017第3周";
		String weekStr = weekPath.replaceAll("第", "-");
		weekStr = weekStr.replaceAll("周", "");
		System.out.println(weekStr);
	}
	
	
	


}
