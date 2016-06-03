package com.egnore.common.model.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingDictionary {
	protected List<SettingDescription> params  = new ArrayList<SettingDescription>();
	protected Map<String, SettingDescription> idLookup = new HashMap<String, SettingDescription>();
	protected Map<String, SettingDescription> nameLookup = new HashMap<String, SettingDescription>();

	public void init() {
	}

	static public boolean isValidString(String s) {
		return ((s != null) && (!s.isEmpty()));
	}

	public void add(SettingDescription p) {
		params.add(p);
		if (isValidString(p.id))	idLookup.put(p.id, p);
		if (isValidString(p.name))	nameLookup.put(p.name, p);
	}
	
	public SettingDescription findByName(String name) {
		if (!isValidString(name)) return null;
		return nameLookup.get(name);
	}
}
