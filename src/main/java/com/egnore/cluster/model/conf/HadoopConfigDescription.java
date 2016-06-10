package com.egnore.cluster.model.conf;

import com.egnore.cluster.model.RoleType;
import com.egnore.cluster.model.ServiceType;
import com.egnore.common.model.conf.SettingDescription;

public class HadoopConfigDescription extends SettingDescription {

	protected ServiceType service;
	protected RoleType role;

	public HadoopConfigDescription() {
		
	}
			
	public HadoopConfigDescription(String name, String defaultValue, ServiceType service, RoleType role) {
		super(name, defaultValue);
		this.service = service;
		this.role = role;
	}

	@Override
	public String saveToString() {
		return service + "/" + role + "/" + super.saveToString();
	}

	@Override
	public void loadFromString(String s) {
		//HDFS/null/namespace(namespace)=abc
		String[] ss = s.split("/");
		service = ("null".equals(ss[0])) ? null : ServiceType.valueOf(ss[0]);
		if ("null".equals(ss[1]))
			role = null;
		else
			role = RoleType.valueOf(ss[1]);
		super.loadFromString(ss[2]);
	}
}
