package web.domain;

import java.util.ArrayList;
import java.util.List;

public class Req {
	
	public String maxDate;
	
	public boolean combine = false;
	
	//ÿ���߳�������һҳ���ݺ�˯�ߵĺ�����
	public int sleep = 0;

	public Boolean filterNotice = true;
	
	public String cookie;
	
	//Ҫ��ѯ�����ڣ�Ĭ�����ڣ��Ӵ�С����
	public List<String> mapKey = new ArrayList<String>();
	
	public List<Stock> list = new ArrayList<Stock>();
	
	//Ҫ��ѯ�İ������
	public String bodyName = "δ����";
	
}
