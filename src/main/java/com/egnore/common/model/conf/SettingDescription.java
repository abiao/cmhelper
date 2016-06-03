package com.egnore.common.model.conf;

public class SettingDescription {
	protected String id;
	protected String name;
	protected String valueType;
	protected String category;
	protected String defaultValue;
	
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

	public String toString() {
		return "id=" + id
				+",name=" + name;
	}
}
