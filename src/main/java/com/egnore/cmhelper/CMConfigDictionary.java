package com.egnore.cmhelper;

import java.util.ArrayList;
import java.util.List;

import com.egnore.cluster.model.RoleType;
import com.egnore.cluster.model.ServiceType;

public class CMConfigDictionary {
	List<CMConfig> configs = new ArrayList<CMConfig>();
	
	protected void addConfig(String name, String relatedName, String defaultValue, ServiceType st, RoleType rt) {
		configs.add(new CMConfig(name, relatedName, defaultValue, st, rt));
	}

	public void init() { }

	public CMConfig findByRelatedName(String key) {
		for (CMConfig c : configs) {
			if (key.equals(c.getRelatedName()))
				return c;
		}
		return null;
	}
}
