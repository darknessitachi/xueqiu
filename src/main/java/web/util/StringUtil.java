package web.util;

public class StringUtil {

	public static boolean isEmpty(String maxDate) {
		return maxDate==null || "".equals(maxDate);
	}

	public static String number2word(int i) {
		String result = null;
		
		switch (i) {
		case 1:
			result = "һ";
			break;
		case 2:
			result = "��";
			break;
		case 3:
			result = "��";
			break;
		case 4:
			result = "��";
			break;
		case 5:
			result = "��";
			break;
		case 6:
			result = "��";
			break;
		default:
			break;
		}
		
		if(result == null){
			System.err.println("��ѯ����û�ж�Ӧ�����ĺ��֡�");
		}
		return result;
	}
	/**
	 * �����Ʊ������3���֣�����������ո�
	 * @param name
	 * @return
	 */
	public static String formatStockName(String name) {
		if(name.length() == 3){
			return name+"  ";
		}
		return name;
	}

}
