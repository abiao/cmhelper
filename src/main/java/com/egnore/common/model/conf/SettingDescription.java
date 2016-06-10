package com.egnore.common.model.conf;

import com.egnore.common.StringKeyedValue;
import com.egnore.common.StringSerDeObject;

public class SettingDescription implements StringSerDeObject, StringKeyedValue {
	protected String name;
	protected String valueType;
	protected String defaultValue;
	protected SettingType type = SettingType.USER_DEFINED;

	public SettingDescription() {
	}

	public SettingDescription(String name) {
		this.name = name;
	}

	public SettingDescription(String name, String defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public SettingType getType() {
		return type;
	}

	public boolean isUserDefined() {
		return this.type == SettingType.USER_DEFINED;
	}

	public String saveToString() {
		return name;
	}

	public void loadFromString(String s) {
		name = s;
	}

	public String getKey() {
		return name;
	}
}
