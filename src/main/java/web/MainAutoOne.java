package web;

import java.io.IOException;
import java.util.Date;

import translate.MainTrans;
import translate.StockInterface;
import web.common.StockCommand;
import config.Constants;

/**
 * ֻ��Ҫ��ԭʼ�ļ�����code�У��Ϳ���ִ�и÷�����
 * @author Administrator
 *
 */
public class MainAutoOne {
	

	public static void main(String[] args) throws IOException {
		autoOne(Constants.ZXG_FILE_NAME);
	}

	public static void autoOne(String fileName) throws IOException {
		//�ȷ��룬�ѷ�����ļ�д��request_body��
		StockInterface sm = new StockInterface();
		sm.useLog = MainTrans.useLog ;
		sm.translate(fileName);
		
		//��ִ�в�ѯ�ȶȷ���
		long start = new Date().getTime();
		StockCommand c = new StockCommand(Constants.business_sort);
		c.start();
		long end = new Date().getTime();
		System.out.println("��ʱ��"+(end-start)/1000+"��");
	}

}