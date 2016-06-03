package com.egnore.cluster.model;

import com.egnore.common.model.conf.ConfigurableTreeNode;

public class Group extends ConfigurableTreeNode {

	Group(Role role) {
		this.parent = role;
	}

	public void setRole(Role role) {
		this.parent = role;
	}

	public Role getRole() {
		return (Role)this.parent;
	}

	public void addInstance(Instance instance) {
		children.add(instance);
	}
}
