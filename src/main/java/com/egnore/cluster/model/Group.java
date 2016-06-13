package com.egnore.cluster.model;

import com.egnore.common.model.conf.ConfigurableTreeNode;

public class Group extends ConfigurableTreeNode {

	Group(Role role) {
		this.parent = role;
	}

	@Override
	public String getTypeString() {
		return "GROUP";
	}

	@Override
	protected ConfigurableTreeNode newEmptyChild() {
		return new Instance(null);
	}

	public void setRole(Role role) {
		this.parent = role;
	}

	public Role getRole() {
		return (Role)this.parent;
	}
	
	public boolean isDefaultGroup() {
		return ((Role)this.parent).getDefaultGroup() == this;
	}
}
