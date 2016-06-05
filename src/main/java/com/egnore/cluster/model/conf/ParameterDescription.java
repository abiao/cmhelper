package com.egnore.cluster.model.conf;

import com.egnore.cluster.model.RoleType;
import com.egnore.cluster.model.ServiceType;
import com.egnore.common.model.conf.SettingDescription;

public class ParameterDescription extends SettingDescription {

	protected ServiceType service;
	protected RoleType role;

	public ParameterDescription(String id, String name, String defaultValue, ServiceType service, RoleType role) {
		super(id, name, defaultValue);
		this.service = service;
		this.role = role;
	}

	public ParameterDescription() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String dumpToString() {
		return service + "/" + role + "/" + super.dumpToString();
	}

	@Override
	public void readFromString(String s) {
		//HDFS/null/namespace(namespace)=abc
		String[] ss = s.split("/");
		service = ("null".equals(ss[0])) ? null : ServiceType.valueOf(ss[0]);
		if ("null".equals(ss[1]))
			role = null;
		else
			role = RoleType.valueOf(ss[1]);
		super.readFromString(ss[2]);
	}
}
