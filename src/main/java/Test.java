import java.io.IOException;

import net.sf.json.JSONObject;


public class Test {

	public static void main(String[] args) throws IOException {
		JSONObject json = JSONObject.fromObject("{'hash':'lhHdRDYHujZixfyqca2jJ1T12cGU','key':'T0002.zip'}");
		System.out.println(json.get("key"));
	}

}
