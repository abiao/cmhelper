package com.egnore.common.util;

public class StringUtil {

	static public boolean isValidString(String s) {
		return ((s != null) && (!s.isEmpty()));
	}

	static public String getValidString(String s) {
		return (s == null) ? "" : s;
	}
}
