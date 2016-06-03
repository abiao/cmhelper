package com.egnore.cluster.model;

import com.egnore.common.model.conf.ConfigurableTreeNode;

public class Instance extends ConfigurableTreeNode {

	protected Host host;

	public Instance(Group group) {
		this.parent = group;
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
