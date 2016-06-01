package com.egnore.cluster.model;

import java.util.ArrayList;
import java.util.List;

public class Host {
	protected List<Instance> instances = new ArrayList<Instance>();
	protected String fqdn;
	protected String ip;
	protected String id = null;

	public void setFQDN(String fqdn) {
		this.fqdn = fqdn;
	}

	public String getFQDN() {
		return this.fqdn;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getIp() {
		return this.ip;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void addInstance(Instance role) {
		instances.add(role);
	}

	public List<Instance> getInstances() {
		return instances;
	}
}
