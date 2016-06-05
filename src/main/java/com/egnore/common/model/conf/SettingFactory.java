package com.egnore.common.model.conf;

public class SettingFactory {

	static SettingFactory inst = new SettingFactory();

	protected SettingDictionary dict;

	static public SettingFactory getInstance() {
		return inst;
	}

	public SettingFactory() {
		this.dict = null;
	}

	public SettingFactory(SettingDictionary dict) {
		this.dict = dict;
	}

	public void setSettingDictionary(SettingDictionary dict) {
		this.dict = dict;
	}

	public SettingDictionary getSettingDictionary() {
		return this.dict;
	}

	public SettingDescription getSettingDescription(String key) {
		return this.dict.findById(key);
	}

	public boolean hasSettingDescription(String key) {
		return this.dict.idLookup.containsKey(key);
	}
/*	
	public SettingDescription newSettingDescription(String name) {
		return newSettingDescription(name, null);
	}

	public SettingDescription newSettingDescription(String name, String defaultValue) {
		return new SettingDescription(name, name, defaultValue);
	}

	public Setting newSetting(String name, String value) {
		return newSetting(name, value, false);
	}

	public Setting newSetting(String name, String value, boolean noNew) {
		SettingDescription pd;
		if (dict != null) {
			pd = dict.findByName(name);
			if (pd == null) {
				if (noNew)
					return null;
				else {
					pd = newSettingDescription(name, value);
				}
			}
		} else {
			pd = new SettingDescription(name, name, value);
		}
		return new Setting(pd, value);
	}
*/
}
