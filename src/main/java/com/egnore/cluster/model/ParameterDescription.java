package com.egnore.cluster.model;

public class ParameterDescription {
	public String id;
	public String name;
	public String defaultValue;
	public ServiceType service;
	public RoleType role;
	public ParameterType type;
	public ParameterDescription(String id, String name, String defaultValue, ServiceType service, RoleType role) {
		this.id = id;
		this.name = name;
		this.defaultValue = defaultValue;
		this.service = service;
		this.role = role;
	}
	public String toString() {
		return "id=" + id
				+",name=" + name
				+",service=" + service
				+",role=" + role;
	}
}
