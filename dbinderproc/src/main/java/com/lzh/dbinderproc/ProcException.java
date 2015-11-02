package com.lzh.dbinderproc;

import javax.lang.model.element.Element;

public class ProcException extends Exception {
	
	private static final long serialVersionUID = -4849159232594991225L;
	private Element e;

	public ProcException(Element e,String data,Object ...params) {
		super(String.format(data, params));
		this.e = e;
	}
	
	public Element getElement() {
		return e;
	}
}
