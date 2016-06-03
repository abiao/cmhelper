package com.egnore.common.model.conf;

public class Configurable {
	protected String id;
	protected boolean enabled;
	protected Settings conf = new Settings();

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void addSetting(Setting s) {
		conf.add(s);
	}
	
	public boolean hasSetting(String name) {
		 return conf.containsKey(name);
	}

	public Setting getSetting(String name) {
		return conf.get(name);
	}

	public Settings getSettings() {
		return conf;
	}
}
