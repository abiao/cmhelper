package com.egnore.common;

import java.util.ArrayList;

/**
 * Map or List? This is a question.
 * @author biaobean
 *
 */
public class StringPairs extends ArrayList<StringPair> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7563225966529271927L;

	public boolean containsKey(String key) {
		for (StringPair s : this) {
			if (key.equals(s.getName()))
				return true;
		}
		return false;
	}

	public StringPair get(String key) {
		for (StringPair s : this) {
			if (key.equals(s.getName()))
				return s;
		}
		return null;
	}
}
