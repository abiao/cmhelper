package com.egnore.cluster.model.conf;

import com.egnore.common.model.conf.SettingDescription;
import com.egnore.common.model.conf.SettingDictionary;
import com.egnore.common.model.conf.SettingFactory;

public class ParameterFactory extends SettingFactory {

	public ParameterFactory() {
		super();
		dict = new ParameterDictionary();
	}

	public ParameterFactory(SettingDictionary dict) {
		super(dict);
	}
/*
	@Override
	public SettingDescription newSettingDescription(String name, String defaultValue) {
		return new ParameterDescription(name, name, defaultValue, null, null);
	}
*/
}
