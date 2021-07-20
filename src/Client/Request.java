package Client;

import java.io.Serializable;

public class Request implements Serializable {
	private int code;
	private Object[] objs;

	public Request(int code) {
		this.code = code;
	}

	public Request(int code, Object[] objs) {
		this(code);
		this.objs = objs;
	}

	public int getCode() {
		return code;
	}

	public Object[] getObjs() {
		return objs;
	}
}
