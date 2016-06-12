package com.egnore.cluster.model;

import com.egnore.cluster.model.conf.HadoopConfigDescription;
import com.egnore.common.model.conf.ConfigurableTreeNode;
import com.egnore.common.model.conf.SettingDescription;

public class Service extends ConfigurableTreeNode {

	protected ServiceType type;

	Service(Cluster cluster, ServiceType type) {
		this.parent = cluster;
		this.type = type;
		testAndNewChildren();
		for (RoleType rt : RoleType.values()) {
			if (rt.getServiceType() == type) {
				Role r = new Role(this, rt);
				children.add(r);
			}
		}
	}

	@Override
	public SettingDescription createSettingDescription(String key) {
		return new HadoopConfigDescription(key, null, this.type, null);
	}

	@Override
	protected ConfigurableTreeNode newEmptyChild() {
		return new Role(null, null);
	}

	@Override
	protected void loadType(String s) {
		this.type = ServiceType.valueOf(s);
	}
	
	@Override
	public String getTypeString() {
		return this.type.toString();
	}

	public Cluster getCluster() {
		return (Cluster)parent;
	}

	public void setType(ServiceType type) {
		this.type = type;
	}

	public ServiceType getType() {
		return this.type;
	}

	public Role getRole(RoleType rt) {
		for (ConfigurableTreeNode n : children) {
			Role r = (Role)n;
			if (r.getType() == rt) {
				return r;
			}
		}
		return null; ///< Should not reach here.
	}
}
