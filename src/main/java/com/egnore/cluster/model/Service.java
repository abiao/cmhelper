package com.egnore.cluster.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;

public class Service {
	protected ServiceType type;
	protected String id = null;
	protected Cluster parent;
	protected Configuration conf = new Configuration();
	protected List<Role> roles = new ArrayList<Role>();
	protected boolean enabled;

	Service(Cluster cluster, ServiceType type) {
		this.parent = cluster;
		this.type = type;
		for (RoleType rt : RoleType.values()) {
			if (rt.getServiceType() == type) {
				Role r = new Role(this, rt);
				roles.add(r);
			}
		}
	}

	public Cluster getCluster() {
		return parent;
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

	public List<Role> getRoles() {
		return roles;
	}

	public Role getRole(RoleType rt) {
		for (Role r : roles) {
			if (r.getType() == rt) {
				return r;
			}
		}
		return null; ///< Should not reach here.
	}

	public void addConfig(String name, String value) {
		conf.clear();
		conf.set(name, value);
	}

	public Configuration getConfiguration() {
		return this.conf;
	}

}
