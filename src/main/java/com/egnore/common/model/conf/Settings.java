package com.egnore.common.model.conf;

import java.util.ArrayList;

/**
 * Map or List? This is a question.
 * @author biaobean
 *
 */
public class Settings extends ArrayList<Setting> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7563225966529271927L;

	public boolean containsKey(String key) {
		for (Setting s : this) {
			if (s.getName() == key)
				return true;
		}
		return false;
	}

	public Setting get(String key) {
		for (Setting s : this) {
			if (s.getName() == key)
				return s;
		}
		return null;
	}
}
