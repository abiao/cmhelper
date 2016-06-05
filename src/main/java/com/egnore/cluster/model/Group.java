package com.egnore.cluster.model;

import com.egnore.common.model.conf.ConfigurableTreeNode;
import com.egnore.common.model.conf.SettingDescription;

public class Group extends ConfigurableTreeNode {

	Group(Role role) {
		this.parent = role;
	}

	@Override
	public SettingDescription createSettingDescription(String key, String defaultValue) {
		return parent.createSettingDescription(key, defaultValue);
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
}
