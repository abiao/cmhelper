package com.egnore.cluster.model;

import com.egnore.common.model.conf.ConfigurableTreeNode;

public class Role extends ConfigurableTreeNode {

	protected RoleType type;
	protected Group defaultGroup;

	Role(Service service, RoleType type) {
		this.parent = service;
		this.type = type;
		defaultGroup = new Group(this);
		children.add(defaultGroup);
	}

	public Service getService() {
		return (Service)parent;
	}

	public Group getDefaultGroup() {
		return defaultGroup;
	}

	public void setType(RoleType type) {
		this.type = type;
	}

	public RoleType getType() {
		return this.type;
	}
	
	public void addInstance(Host host) {
		Instance i = new Instance(defaultGroup);
		i.host = host;
		defaultGroup.addInstance(i);
	}
}
