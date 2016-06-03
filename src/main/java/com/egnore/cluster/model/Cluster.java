package com.egnore.cluster.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.egnore.common.Dumper;
import com.egnore.common.model.conf.ConfigurableTreeNode;

/**
 * 
 * @author biaochen
 *
 * Cluster -> Service -> Role -> Group -> Instance
 *                                           |
 *                                          Host  
 */
public class Cluster extends ConfigurableTreeNode {

	protected List<Host> nodes = new ArrayList<Host>();

	public void setName(String name) {
		this.id = name;
	}

	public String getName() {
		return id;
	}

	public void addInstance(Host host) {
		nodes.add(host);
	}

	public List<Host> getHostList() {
		return nodes;
	}

	public void addService(Service service) {
		children.add(service);
	}

	public List<ConfigurableTreeNode> getServices() {
		return getChildren();
	}

	public void createDefaultServices() {
		for (ServiceType st : ServiceType.values()) {
			newService(st);
		}
	}

	protected Service newService(ServiceType st) {
		Service s = new Service(this, st);
		children.add(s);
		return s;
	}

	public Service getDefaultService(ServiceType st) {
		for (ConfigurableTreeNode n : children) {
			Service s = (Service)n;
			if (s.getType() == st) {
				return s;
			}
		}
		return newService(st);
	}

	public void build() {
		
	}

	public Group getDefaultGroup(RoleType role) {
		Service s = getDefaultService(role.getServiceType());
		return s.getRole(role).getDefaultGroup();
	}

	public Instance addInstance(Host h, RoleType role) {
		if (!nodes.contains(h)) {
			nodes.add(h);
		}
	
		Instance i = new Instance(getDefaultGroup(role));
		i.host = h;
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
	
	public void save(String path) {
		Dumper dp = new Dumper(path);
		PrintStream ps = dp.getPrintStream();
		for (Service s : services) {
			ps.println(s.getType().toString());
		}
		dp.close();
	}
}
