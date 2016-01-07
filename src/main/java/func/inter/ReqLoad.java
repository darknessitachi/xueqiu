package func.inter;

import java.io.IOException;

import func.domain.Req;

public interface ReqLoad {
	
	public Req getReq();
	
	public void init();
	
	public void print() throws IOException;

}
