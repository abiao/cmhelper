package com.egnore.cluster.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;

public class Group {
	protected String id = null; 
	protected Role parent;
	protected Configuration conf = new Configuration();
	protected List<Instance> instances = new ArrayList<Instance>();

	Group(Role role) {
		this.parent = role;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setRole(Role role) {
		this.parent = role;
	}

	public Role getRole() {
		return this.parent;
	}

	public void addConfig(String name, String value) {
		conf.clear();
		conf.set(name, value);
	}

	public Configuration getConfiguration() {
		return this.conf;
	}

	public void addInstance(Instance instance) {
		instances.add(instance);
		instance.group = this;
	}
	
	public List<Instance> getInstances() {
		return instances;
	}
}
