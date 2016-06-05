package com.egnore.hadoop.conf;

import com.egnore.common.util.Version;

public class ConfigDescription {
	String name;
	String defaultValue;
	String newName;
	boolean deprecated = false;
	Version minVersion;
	Version maxVersion;

	String service;
	String role;
	String type;
	String fileName;
	
	String valueType;
	String minValue;
	String maxValue;
	String unit;
	String incrementStep;
	
	String displayName;
	String description;
	public boolean isDeprecated() {
		return deprecated;
	}
}
