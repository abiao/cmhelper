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

	public String toString() {
		return super.toString() + "service=" + service
				+",role=" + role;
	}
}
