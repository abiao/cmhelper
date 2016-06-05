package com.egnore.common.model.conf;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egnore.common.io.LinedFileReader;
import com.egnore.common.util.StringUtil;

public class SettingDictionary {
	protected List<SettingDescription> params  = new ArrayList<SettingDescription>();
	protected Map<String, SettingDescription> idLookup = new HashMap<String, SettingDescription>();
	protected Map<String, SettingDescription> nameLookup = new HashMap<String, SettingDescription>();

	protected List<SettingDescription> ud  = new ArrayList<SettingDescription>();

	public void init() {
	}

	public void add(SettingDescription p) {
		params.add(p);
		if (StringUtil.isValidString(p.id))	idLookup.put(p.id, p);
		if (StringUtil.isValidString(p.name))	nameLookup.put(p.name, p);
		if (p.type == SettingType.USER_DEFINED) {
			ud.add(p);
		}
	}
	
	public SettingDescription findById(String id) {
		if (!StringUtil.isValidString(id)) return null;
		return idLookup.get(id);
	}

	public SettingDescription findByName(String name) {
		if (!StringUtil.isValidString(name)) return null;
		return nameLookup.get(name);
	}

	public void dumpUserDefinedSettings(PrintStream ps) {
		ps.println(ud.size());
		for (SettingDescription sd : ud) {
			ps.println(sd.dumpToString());
		}
	}

	protected SettingDescription newSettingDescription() {
		return new SettingDescription();
	}

	public void loadUserDefinedSettings(LinedFileReader reader) throws NumberFormatException, IOException {
		int n = Integer.parseInt(reader.next());
		for (int i = 0; i < n; i++) {
			SettingDescription p = newSettingDescription();
			p.readFromString(reader.next());
			p.type = SettingType.USER_DEFINED;
			add(p);
		}
	}
}
