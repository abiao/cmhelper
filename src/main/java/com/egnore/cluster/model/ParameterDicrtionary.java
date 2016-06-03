package com.egnore.cluster.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterDicrtionary {
	protected List<ParameterDescription> params  = new ArrayList<ParameterDescription>();
	protected Map<String, ParameterDescription> idLookup = new HashMap<String, ParameterDescription>();
	protected Map<String, ParameterDescription> nameLookup = new HashMap<String, ParameterDescription>();

	protected List<List<ParameterDescription> > services;
	protected List<List<ParameterDescription> > roles;

	public void init() {
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

	public void add(ParameterDescription p) {
		params.add(p);
		if (isValidString(p.id))	idLookup.put(p.id, p);
		if (isValidString(p.name))	nameLookup.put(p.name, p);

		if (p.service != null) services.get(p.service.ordinal()).add(p);
		if (p.role != null) roles.get(p.role.ordinal()).add(p);
	}
	
	public ParameterDescription findByName(String name) {
		if (!isValidString(name)) return null;
		return nameLookup.get(name);
	}
}
