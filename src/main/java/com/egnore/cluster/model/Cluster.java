package com.egnore.cluster.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.egnore.cluster.model.conf.ParameterDescription;
import com.egnore.cluster.model.conf.ParameterDictionary;
import com.egnore.common.Dumper;
import com.egnore.common.io.LinedFileReader;
import com.egnore.common.model.conf.ConfigurableTreeNode;
import com.egnore.common.model.conf.SettingDescription;
import com.egnore.common.model.conf.SettingDictionary;
import com.egnore.common.model.conf.SettingFactory;

/**
 * 
 * @author biaochen
 *
 * Cluster -> Service -> Role -> Group -> Instance
 *                                           |
 *                                          Host  
 */
public class Cluster extends ConfigurableTreeNode {

	public Cluster() {
	}

	public void setName(String name) {
		this.id = name;
	}

	public String getName() {
		return id;
	}

	protected void setSettingDictionary(SettingDictionary dict) {
		SettingFactory.getInstance().setSettingDictionary(dict);
	}

	@Override
	public SettingFactory getSettingFactory() {
		return SettingFactory.getInstance();
	}

	@Override
	public SettingDescription createSettingDescription(String key, String defaultValue) {
		return new ParameterDescription(key, key, defaultValue, null, null);
	}

	@Override
	protected ConfigurableTreeNode newEmptyChild() {
		return new Service(null, null);
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
		getOrNewChildren().add(s);
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
		if (!getHostList().contains(h)) {
			getHostList().add(h);
		}
	
		Instance i = new Instance(null);
		getDefaultGroup(role).addChild(i);
		i.host = h;
		return i;
	}

	public List<Host> getHostList() {
		return HostManager.getInstance().getHostList();
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
		
		HostManager.addHost(host);

		for (RoleType rt : RoleType.values()) {
			if (rt != RoleType.GATEWAY) {
				addInstance(host, rt);
			}
		}
	}

	public void createTestCluster() {
		createDefaultServices();
		setName("test1");

		Service s = getDefaultService(ServiceType.HDFS);
		s.addSetting("namespace", "abc");
		s.addSetting("name", "abc");
		Host h = new Host();
		h.fqdn = "ip-172-31-19-49.ap-northeast-2.compute.internal";
		h.ip = "172.31.19.49";
		HostManager.addHost(h);

		Instance i = addInstance(h, RoleType.DATANODE);
		i.addSetting("name", "value");
		i.addSetting("dfs.data.dir", "value1");
	}
	
	public void save(String path) {
		Dumper dp = new Dumper(path);
		PrintStream ps = dp.getPrintStream();
		SettingFactory.getInstance().getSettingDictionary().dumpUserDefinedSettings(ps);
		HostManager.getInstance().dump(ps);
		this.dump(ps);
		dp.close();
	}

	public void load(String path) throws NumberFormatException, IOException {
		LinedFileReader reader = new LinedFileReader(path);
		SettingFactory.getInstance().getSettingDictionary().loadUserDefinedSettings(reader);
		HostManager.getInstance().loadFromLinedStrings(reader);
		this.loadFromLinedStrings(reader);
		reader.close();
	}
}
