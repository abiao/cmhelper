package com.egnore.cluster.model.conf;

import java.util.ArrayList;
import java.util.List;

import com.egnore.cluster.model.RoleType;
import com.egnore.cluster.model.ServiceType;
import com.egnore.common.model.conf.SettingDescription;
import com.egnore.common.model.conf.SettingDictionary;

public class ParameterDictionary extends SettingDictionary {

	protected List<List<ParameterDescription> > services;
	protected List<List<ParameterDescription> > roles;

	public ParameterDictionary() {
		int len;
		
		len = ServiceType.values().length;
		services = new ArrayList<List<ParameterDescription>>(len);
		for (int i = 0; i < len; i++) {
			services.add(new ArrayList<ParameterDescription>());
		}

		len = RoleType.values().length;
		roles = new ArrayList<List<ParameterDescription>>(len);
		for (int i = 0; i < len; i++) {
			roles.add(new ArrayList<ParameterDescription>());
		}
	}

	static public boolean isValidString(String s) {
		return ((s != null) && (!s.isEmpty()));
	}

	@Override
	protected SettingDescription newSettingDescription() {
		return new ParameterDescription();
	}

	@Override
	public void add(SettingDescription sd) {
		super.add(sd);
		ParameterDescription p = (ParameterDescription)sd;

		if (p.service != null) services.get(p.service.ordinal()).add(p);
		if (p.role != null) roles.get(p.role.ordinal()).add(p);
	}
}
