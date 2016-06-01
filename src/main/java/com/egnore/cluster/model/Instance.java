package com.egnore.cluster.model;

import org.apache.hadoop.conf.Configuration;

public class Instance {
	protected String id;
	protected Host host;
	protected Group group;
	protected Configuration conf = new Configuration();
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Group getGroup() {
		return this.group;
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
	
	public void addConfig(String name, String value) {
		conf.clear();
		conf.set(name, value);
	}

	public Configuration getConfiguration() {
		return this.conf;
	}

}
