package core.inter;

import java.io.IOException;

import core.domain.Req;

public interface ReqLoad {
	
	public Req getReq();
	
	public void init();
	
	public void print() throws IOException;

}
