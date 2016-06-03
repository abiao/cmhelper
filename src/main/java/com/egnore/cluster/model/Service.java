package com.egnore.cluster.model;

import com.egnore.common.model.conf.ConfigurableTreeNode;

public class Service extends ConfigurableTreeNode {

	protected ServiceType type;

	Service(Cluster cluster, ServiceType type) {
		this.parent = cluster;
		this.type = type;
		for (RoleType rt : RoleType.values()) {
			if (rt.getServiceType() == type) {
				Role r = new Role(this, rt);
				children.add(r);
			}
		}
	}

	public Cluster getCluster() {
		return (Cluster)parent;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
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
