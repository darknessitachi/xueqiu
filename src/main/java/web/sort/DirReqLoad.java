package web.sort;

import java.io.IOException;

import web.common.ReqLoad;
import web.domain.Req;

public class DirReqLoad implements ReqLoad {
	
	private Req req;
	
	private LoadAndPrint sortReqLoad;

	public DirReqLoad(Req req) {
		this.req = req;
		this.sortReqLoad = new LoadAndPrint(this.req);
	}

	public void init() {
		sortReqLoad.init();
	}

	public void print() throws IOException {
		sortReqLoad.print();
	}

}
