package com.egnore.cluster.model;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	protected String name = "cluster_ex";
	protected List<Host> nodes = new ArrayList<Host>();
	protected List<Service> services = new ArrayList<Service>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addInstance(Host host) {
		nodes.add(host);
	}

	public List<Host> getHostList() {
		return nodes;
	}

	public void addService(Service service) {
		services.add(service);
	}

	public List<Service> getServiceList() {
		return services;
	}

	public void createDefaultServices() {
		for (ServiceType st : ServiceType.values()) {
			newService(st);
		}
	}

	protected Service newService(ServiceType st) {
		Service s = new Service(this, st);
		services.add(s);
		return s;
	}

	public Service getDefaultService(ServiceType st) {
		for (Service s : services) {
			if (s.getType() == st) {
				return s;
			}
		}
		return newService(st);
	}

	public void build() {
		
	}

	public Instance addInstance(Host h, RoleType role) {
		if (!nodes.contains(h)) {
			nodes.add(h);
		}
	
		Instance i = new Instance();
		i.host = h;
		Service s = getDefaultService(role.getServiceType());
		s.getRole(role).getDefaultGroup().addInstance(i);
		return i;
	}

	public Host createSingleNodeFullCluster(String clusterName, String fqdn, String ip) {
		Host h = new Host();
		h.fqdn = fqdn;
		h.ip = ip;
		createSingleNodeFullCluster(clusterName, h);
		return h;
	}

	public void createSingleNodeFullCluster(String clusterName, Host host) {
		createDefaultServices();
		setName(clusterName);
		
		nodes.add(host);

		for (RoleType rt : RoleType.values()) {
			if (rt != RoleType.GATEWAY) {
				addInstance(host, rt);
			}
		}
	}

	public void createTestCluster() {
		createDefaultServices();
		setName("test1");

		Host h = new Host();
		h.fqdn = "ip-172-31-19-49.ap-northeast-2.compute.internal";
		h.ip = "172.31.19.49";
		nodes.add(h);

		Instance i = addInstance(h, RoleType.DATANODE);
		i.addConfig("name", "value");
		i.addConfig("dfs.data.dir", "value1");
	}
}
