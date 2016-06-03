package com.egnore.hadoop.conf;

import com.egnore.common.util.Version;

public class ConfigDescription {
	String name;
	String defaultValue;
	String newName;
	boolean deprecated = false;
	Version minVersion;
	Version maxVersion;
	
	public boolean isDeprecated() {
		return deprecated;
	}
}
