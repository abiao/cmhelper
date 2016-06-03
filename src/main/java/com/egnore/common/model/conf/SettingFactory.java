package com.egnore.common.model.conf;

public class SettingFactory {

	protected SettingDictionary dict;
	public SettingFactory(SettingDictionary dict) {
		this.dict = dict;
	}

	public Setting newSetting(String name, String value) {
		SettingDescription pd = dict.findByName(name);
		if (pd == null) {
			return null;
		}
		return new Setting(pd, value);
	}
}
