package com.egnore.cluster.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;

public class Role {

	protected Service parent;
	protected RoleType type;
	protected String id = null; 
	protected Configuration conf = new Configuration();
	protected List<Group> groups = new ArrayList<Group>();
	protected Group defaultGroup;

	Role(Service service, RoleType type) {
		this.parent = service;
		this.type = type;
		defaultGroup = new Group(this);
		groups.add(defaultGroup);
	}

	public Service getService() {
		return parent;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public Group getDefaultGroup() {
		return defaultGroup;
	}

	public List<Group> getGroups() {
		return groups;
	}


	public void setType(RoleType type) {
		this.type = type;
	}

	public RoleType getType() {
		return this.type;
	}
	
	public void addInstance(Host host) {
		Instance i = new Instance();
		i.host = host;
		defaultGroup.addInstance(i);
		i.group = defaultGroup;
	}

	public void addConfig(String name, String value) {
		conf.clear();
		conf.set(name, value);
	}

	public Configuration getConfiguration() {
		return this.conf;
	}
}
