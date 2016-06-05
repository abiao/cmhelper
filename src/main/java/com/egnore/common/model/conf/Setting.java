package com.egnore.common.model.conf;

import com.egnore.common.StringSerDeObject;

public class Setting {
	protected SettingDescription key;
	protected String value;

	Setting(SettingDescription key, String value) {
		this.key = key;
		this.value = value;
	}

	public SettingDescription getParameterDescription() {
		return key;
	}

	public String getName() {
		return key.getName();
	}

	public String getValue() {
		return value;
	}

	public String getNomalizedValue() {
		return value;
	}

	public String dumpToString() {
		return key.getId() + "=" + getNomalizedValue();
	}

	static public Setting loadFromString(String s) {
		String[] ss = s.split("=");
		SettingDescription key = SettingFactory.getInstance().getSettingDescription(ss[0]);
		String value = ss[1];
		return new Setting(key, value);
	}
}
