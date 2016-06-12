package com.egnore.cluster.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import com.egnore.cluster.model.conf.HadoopConfigDescription;
import com.egnore.cluster.model.conf.HadoopConfigManager;
import com.egnore.common.Dumper;
import com.egnore.common.StringPair;
import com.egnore.common.io.LinedFileReader;
import com.egnore.common.model.conf.ConfigurableTreeNode;
import com.egnore.common.model.conf.SettingDescription;
import com.egnore.common.model.conf.SettingManager;

/**
 * 
 * @author biaochen
 *
 * Cluster -> Service -> Role -> Group -> Instance
 *                                           |
 *                                          Host  
 */
public class Cluster extends ConfigurableTreeNode {

	protected HadoopConfigManager setman;
	protected HostManager	hostman;

	public Cluster() {
		HadoopConfigManager sm = new HadoopConfigManager();
		try {
			sm.init();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setman = sm;
		this.hostman = HostManager.getInstance();
		testAndNewChildren();
	}

	public Cluster(SettingManager setman, HostManager hostman) {
		this.setman = (HadoopConfigManager)setman;
		this.hostman = hostman;
		testAndNewChildren();
	}

	public void setName(String name) {
		this.id = name;
	}

	public String getName() {
		return id;
	}

	@Override
	public SettingManager getSettingManager() {
		return setman;
	}

	public HostManager getHostManager() {
		return hostman;
	}

	@Override
	public SettingDescription createSettingDescription(String key) {
		return new HadoopConfigDescription(key, null, null, null);
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
		Host host = h;
		if (!getHostManager().contains(h)) {
			host = getHostManager().getHost(h.getIp(), h.getFQDN());
		}
	
		Instance i = new Instance(null);
		getDefaultGroup(role).addChild(i);
		i.host = host;
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
		
		getHostManager().addHost(host);

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
		Host h = getHostManager().addHost("172.31.19.49", "ip-172-31-19-49.ap-northeast-2.compute.internal");

		Instance i = addInstance(h, RoleType.DATANODE);
		i.addSetting("name", "value");
		i.addSetting("dfs.data.dir", "value1");
	}
	
	/**
	 * Dump file
	 */
	public void save(String path) {
		Dumper dp = new Dumper(path);
		PrintStream ps = dp.getPrintStream();
		getSettingManager().dumpUserDefinedSettings(ps);
		HostManager.getInstance().dump(ps);
		this.dump(ps);
		dp.close();
	}

	public void load(String path) throws NumberFormatException, IOException {
		LinedFileReader reader = new LinedFileReader(path);
		getSettingManager().loadUserDefinedSettings(reader);
		HostManager.getInstance().loadFromLinedStrings(reader);
		this.loadFromLinedStrings(reader);
		reader.close();
	}

	public List<Host> getHostList() {
		final List<Host> hosts = new ArrayList<Host>();
		new InstanceScanner(this) {
			@Override
			public void process(Instance i) {
				Host thisHost = i.getHost();
				for (Host h : hosts) {
					if (h.equals(thisHost))
						return;
				}
				hosts.add(i.getHost());
			}
		}.execute();
		return hosts;
	}

	/**
	 * Read from hosts
	 * File format:
	 * ip	name	role;role;...
	 * @throws Exception 
	 */
	static public Cluster loadFromFile(String path, SettingManager sm, HostManager hm) throws Exception {
		LinedFileReader reader = new LinedFileReader(path);
		Path p = new Path(path);
		Path root = p.getParent();
		Cluster c = new Cluster(sm, hm);
		c.setName(path.split("\\.")[0].replace("/", "_").replace("\\", "_"));

		/*
		 * Read roles;
		 */
		String s;
		while ((s = reader.next()) != null) {
			String[] ss = s.split("\\t");
			Host h = c.getHostManager().addHost(ss[0], ss[1]);
			String[] roles = ss[2].split(";");
			for (String r : roles) {
				RoleType rt = RoleType.valueOf(r.toUpperCase());
				if (rt == null) {
					/**
					 * 
					 */
					throw new Exception(r + " is not valid role type.");
				}
				c.addInstance(h, rt);
			}
		}
		reader.close();

//		c.loadServiceConfigFromXMLFile(root.toString(), "core-site.xml", ServiceType.HDFS);
		c.loadServiceConfigFromXMLFile(root.toString(), "hdfs-site.xml", ServiceType.HDFS);
		c.loadServiceConfigFromXMLFile(root.toString(), "hbase-site.xml", ServiceType.HBASE);
		c.loadServiceConfigFromXMLFile(root.toString(), "yarn-site.xml", ServiceType.YARN);
		c.loadServiceConfigFromXMLFile(root.toString(), "hive-site.xml", ServiceType.HIVE);
		return c;
	}

	public void loadServiceConfigFromXMLFile(String dir, String filename, ServiceType st) throws Exception {
		File f = new File(dir, filename);
		
		if (!f.exists())
			return;
		for (ConfigurableTreeNode n : children) {
			Service s = (Service)n;
			if (s.getType() == st) {
				setman.loadXMLConfig(f.getAbsolutePath(), s);
			}
		}
	}

}
