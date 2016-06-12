package com.egnore.common;


public class StringPair implements StringSerDeObject {
	protected String id; ///< TODO: More complex id means
	protected String value;
//	protected boolean isFinal = false; ///< TODO: to support

	///< For load only
	public StringPair() {
	}
	
	public StringPair(String name, String value) {
		this.id = name;
		this.value = value;
	}

	public String getName() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public String getNomalizedValue() {
		return value;
	}

	public String saveToString() {
		return id + "=" + getNomalizedValue();
	}

	public void loadFromString(String s) {
		String[] ss = s.split("=");
		this.id = ss[0];
		this.value = ss[1];
	}
}
