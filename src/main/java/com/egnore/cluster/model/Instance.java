package com.egnore.cluster.model;

import com.egnore.common.model.conf.ConfigurableTreeNode;
import com.egnore.common.model.conf.SettingDescription;

public class Instance extends ConfigurableTreeNode {

	protected Host host;

	public Instance(Group group) {
		this.parent = group;
	}

	@Override
	public SettingDescription createSettingDescription(String key, String defaultValue) {
		return parent.createSettingDescription(key, defaultValue);
	}

	@Override
	protected void loadType(String s) {
		this.host = HostManager.getInstance().getHostbyIp(s);
	}
	
	@Override
	public String getTypeString() {
		return this.host.getIp();
	}

	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}

	public void setGroup(Group group) {
		this.parent = group;
	}

	public Group getGroup() {
		return (Group)this.parent;
	}

	public Role getRole() {
		return this.getGroup().getRole();
	}

	public Service getService() {
		return this.getRole().getService();
	}

	public Cluster getCluster() {
		return this.getService().getCluster();
	}
}
