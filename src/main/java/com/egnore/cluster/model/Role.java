package com.egnore.cluster.model;

import com.egnore.cluster.model.conf.ParameterDescription;
import com.egnore.common.model.conf.ConfigurableTreeNode;
import com.egnore.common.model.conf.SettingDescription;

public class Role extends ConfigurableTreeNode {

	protected RoleType type;
	protected Group defaultGroup;

	Role(Service service, RoleType type) {
		this.parent = service;
		this.type = type;
		defaultGroup = new Group(this);
		getOrNewChildren().add(defaultGroup);
	}

	@Override
	public SettingDescription createSettingDescription(String key, String defaultValue) {
		return new ParameterDescription(key, key, defaultValue, getService().type, this.type);
	}

	@Override
	protected ConfigurableTreeNode newEmptyChild() {
		return new Group(null);
	}

	@Override
	protected void loadType(String s) {
		this.type = RoleType.valueOf(s);
	}
	
	@Override
	public String getTypeString() {
		return this.type.toString();
	}

	public int getInstanceNumber() {
		int nu = 0;
		for(ConfigurableTreeNode g : children) {
			nu += g.getChildNumber();
		}
		return nu;
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
		Instance i = new Instance(null);
		i.host = host;
		defaultGroup.addChild(i);
	}
}
