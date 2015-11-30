package sina;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import web.Constants;
import web.util.DateUtil;
import web.util.HttpUtil;

public class StockInterface {
	
	private static final String[] stockIndex = new String[]{"1999999","0399005","0399006","0399001"};

	public  void readSource(String file) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		String readPath = Constants.classpath + file;
		String nowDate = getNowDate(); 
		String writePath = Constants.outPath  + "/EBK-" + nowDate+".txt";
		//�ȶ�ȡ�ļ�
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(readPath));
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 ) {
					String completeCode = completeTdxCode(line);
					if(completeCode!=null){
						String name = getNameByCode(completeCode);
						sb.append(completeCode).append(",").append(name).append("\n");
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
		//д���ļ�
		writeResult(writePath,sb.toString().toUpperCase());
	}

	private String getNowDate() {
		String nowDate = DateUtil.formatDate(new Date(), DateUtil.yyyyMMdd_HHmmss);;
		return nowDate.replace(":", "��");
	}

	private  void writeResult(String writePath, String result) throws IOException {
		System.out.println("д���ļ���"+writePath);
		System.out.println("д�����ݣ�\n"+result);
		File f = new File(writePath);
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(result);
		bw.close();
		System.out.println("д����ɣ�");
	}

	private  String getNameByCode(String completeCode) throws IOException {
		String httpReqUrl = Constants.inter_url+completeCode;
		String result = HttpUtil.getResult(httpReqUrl,"GBK");
		//��ͨ��=�ָ��ַ���
		String content = result.split("=")[1];
		//��ͨ�����ָ��ַ���
		String name = content.split(",")[0];
		return name.substring(1);
	}
	/**
	 * ͨ���ŵ�����ѡ�ɱ��룬0��ͷ����sz��1��ͷ����sh
	 * @param code
	 * @return
	 */
	private  String completeTdxCode(String code) {
		//���˵�ָ��
		for(String s:stockIndex){
			if(s.equals(code)){
				return null;
			}
		}
		if(code.startsWith("1")){
			return "sh"+code.substring(1);
		}else{
			return "sz"+code.substring(1);
		}
	}

}
