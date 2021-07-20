package Server;

import java.io.Serializable;

public class Response implements Serializable {
	private int code;
	private Object[] objs;

	public Response(int code) {
		this.code = code;
	}

	public Response(int code, Object[] objs) {
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
