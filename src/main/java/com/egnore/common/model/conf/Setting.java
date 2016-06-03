package com.egnore.common.model.conf;

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
}
