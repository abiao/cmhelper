package com.egnore.common.model.conf;

public class SettingDescription {
	protected String id;
	protected String name;
	protected String valueType;
	protected String category;
	protected String defaultValue;
	protected SettingType type = SettingType.USER_DEFINED;

	public SettingDescription() {
	}

	public SettingDescription(String id, String name, String defaultValue) {
		this.id = id;
		this.name = name;
		this.defaultValue = defaultValue;
	}

	public String getId() {
		return id;
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

	public String dumpToString() {
		return id + "(" + name + ")";
	}

	public void readFromString(String s) {
		String[] ss = s.split("\\(");
		id = ss[0];
		name = ss[1].substring(0, ss[1].length() - 1);
	}
}
