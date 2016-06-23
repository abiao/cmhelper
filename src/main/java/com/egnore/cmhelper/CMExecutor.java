package com.egnore.cmhelper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.DataView;
import com.cloudera.api.model.ApiCluster;
import com.cloudera.api.model.ApiClusterList;
import com.cloudera.api.model.ApiClusterVersion;
import com.cloudera.api.model.ApiCommand;
import com.cloudera.api.model.ApiConfig;
import com.cloudera.api.model.ApiConfigList;
import com.cloudera.api.model.ApiEnableNnHaArguments;
import com.cloudera.api.model.ApiHost;
import com.cloudera.api.model.ApiHostList;
import com.cloudera.api.model.ApiHostRef;
import com.cloudera.api.model.ApiJournalNodeArguments;
import com.cloudera.api.model.ApiRole;
import com.cloudera.api.model.ApiRoleConfigGroup;
import com.cloudera.api.model.ApiRoleConfigGroupList;
import com.cloudera.api.model.ApiService;
import com.cloudera.api.model.ApiServiceConfig;
import com.cloudera.api.model.ApiServiceList;
import com.cloudera.api.model.ApiServiceState;
import com.cloudera.api.v1.RolesResource;
import com.cloudera.api.v1.ServicesResource;
import com.cloudera.api.v2.ClustersResourceV2;
import com.cloudera.api.v2.RolesResourceV2;
import com.cloudera.api.v2.ServicesResourceV2;
import com.cloudera.api.v3.RoleConfigGroupsResource;
import com.cloudera.api.v7.RootResourceV7;
import com.cloudera.api.v7.ServicesResourceV7;
import com.egnore.cluster.model.Cluster;
import com.egnore.cluster.model.Group;
import com.egnore.cluster.model.Host;
import com.egnore.cluster.model.HostManager;
import com.egnore.cluster.model.Instance;
import com.egnore.cluster.model.InstanceScanner;
import com.egnore.cluster.model.Role;
import com.egnore.cluster.model.RoleType;
import com.egnore.cluster.model.Service;
import com.egnore.cluster.model.ServiceType;
import com.egnore.common.StringPair;
import com.egnore.common.StringPairs;
import com.egnore.common.log.Dumper;
import com.egnore.common.log.Log;
import com.egnore.common.model.conf.ConfigurableTreeNode;

public class CMExecutor {
	Log log = Log.getInstance(this.getClass());

	RootResourceV7 apiRoot;
	protected String hostName;
	protected String user = "admin";
	protected String passwd = "admin";
	public CMExecutor(String hostname) {
		this.hostName = hostname;

		apiRoot = new ClouderaManagerClientBuilder().withHost(hostname)
				.withUsernamePassword("admin", "admin").build().getRootV7();
	}

	public RootResourceV7 getApi() {
		return apiRoot;
	}

	public void createDefault() {
		ApiCluster item = new ApiCluster();
		final String DEFAULT_CLUSTER_NAME="cluster_ex";
		item.setName(DEFAULT_CLUSTER_NAME);
		item.setDisplayName(DEFAULT_CLUSTER_NAME);
		item.setVersion(ApiClusterVersion.CDH5);
		item.setFullVersion("5.1.3");

		List<ApiService> services = new ArrayList<ApiService>();
		for (ServiceType st : ServiceType.values()) {
			ApiService s = new ApiService();
			s.setType(st.name());
			s.setName(st.name().toLowerCase());
			s.setDisplayName(st.name().toLowerCase() + "1");
			services.add(s);
			break;
		}
		//item.set.setServices(services);
		ApiClusterList c = new ApiClusterList();
		c.add(item);
		//api.createClusters(c);
		ServicesResource sr = apiRoot.getClustersResource().getServicesResource(DEFAULT_CLUSTER_NAME);
		//sr.hdfsEnableHaCommand(arg0, arg1)
		ApiServiceList sl = sr.readServices(DataView.FULL);//new ApiServiceList();
		for (ServiceType st : ServiceType.values()) {
			ApiService s = new ApiService();
			s.setType(st.name());
			s.setName(st.name().toLowerCase());
			s.setDisplayName(st.name().toLowerCase() + "1");
			sl.add(s);
			break;
		}
		sr.createServices(sl);
	}

	public void createIdforCluster(Cluster cluster) {
		for (Host h : cluster.getHostList()) {
			String id = h.getIp().replaceAll("\\.", "");
			h.setId("i-" + id);
		}

		for (ConfigurableTreeNode n : cluster.getServices()) {
			Service s = (Service) n;
			String id = cluster.getName() + s.getType().toString();
			s.setId(id);
		}
		
		new InstanceScanner(cluster) {
			@Override
			public void process(Instance i) {
				String id = i.getService().getType().toString()
						+ "-" + i.getRole().getType().toString()
						+ "-" + i.getHost().getIp().replaceAll("\\.", "");
				i.setId(id);
			}
		}.execute();
	}

	public void enableHdfsNameNodeHA(Cluster cluster, NameNodeHAInfo ha) {
		String clusterName = cluster.getName();
		Service s = ha.hdfsService;
		String nameservice = s.getLocalSetting("dfs.nameservices").getValue();
		Host nn1 = ha.nn1.getHost();
		Host nn2 = ha.nn2.getHost();
		List<ConfigurableTreeNode> jns = s.getRole(RoleType.JOURNALNODE).getDefaultGroup().getChildren();
			//EditsDir 
		ApiEnableNnHaArguments args = new ApiEnableNnHaArguments();
		
		args.setActiveFcName(nn1.getFQDN());//activeNnName	activeNnName (string)	Name of the NameNode role that is going to be made Highly Available.
		args.setStandbyNnName(nn2.getFQDN());//standbyNnName	standbyNnName (string)	Name of the new Standby NameNode role that will be created during the command (Optional).
		args.setStandbyNnHostId(nn2.getId());// standbyNnHostId	standbyNnHostId (string)	Id of the host on which new Standby NameNode will be created.
		//standbyNameDirList	array of standbyNameDirList (string)	List of directories for the new Standby NameNode. If not provided then it will use same dirs as Active NameNode.
		args.setNameservice(nameservice);//nameservice	nameservice (string)	Nameservice to be used while enabling Highly Available. It must be specified if Active NameNode isn't configured with it. If Active NameNode is already configured, then this need not be specified. However, if it is still specified, it must match the existing config for the Active NameNode.
		args.setQjName(nameservice);//qjName	qjName (string)	Name of the journal located on each JournalNodes' filesystem. This can be optionally provided if the config hasn't already been set for the Active NameNode. If this isn't provided and Active NameNode doesn't also have the config, then nameservice is used by default. If Active NameNode already has this configured, then it much match the existing config.
		//activeFcName	activeFcName (string)	Name of the FailoverController role to be created on Active NameNode's host (Optional).
		//standbyFcName	standbyFcName (string)	Name of the FailoverController role to be created on Standby NameNode's host (Optional).
		//zkServiceName	zkServiceName (string)	Name of the ZooKeeper service to be used for Auto-Failover. This MUST be provided if HDFS doesn't have a ZooKeeper dependency. If the dependency is already set, then this should be the name of the same ZooKeeper service, but can also be omitted in that case.
		Set<ApiJournalNodeArguments> apijns = new HashSet<ApiJournalNodeArguments>();
		if (jns.isEmpty())
			throw new IllegalArgumentException("Journal Node must be there.");
		for (ConfigurableTreeNode i : jns) {
			Host h = ((Instance)i).getHost();
			ApiJournalNodeArguments apijn = new ApiJournalNodeArguments();
			apijn.setJnHostId(h.getId());
			apijns.add(apijn);
		}
		args.setJns(apijns);
		//jnName	jnName (string)	Name of new JournalNode to be created. (Optional)
		//jnHostId	jnHostId (string)	ID of the host where the new JournalNode will be created.
		//jnEditsDir	jnEditsDir (string)	Path to the JournalNode edits directory. Need not be specified if it is already set at RoleConfigGroup level.
		//jns	array of jns (apiJournalNodeArguments)	Arguments for the JournalNodes to be created during the command. Must be provided only if JournalNodes don't exist already in HDFS.
		args.setForceInitZNode(false);//forceInitZNode	forceInitZNode (boolean)	Boolean indicating if the ZNode should be force initialized if it is already present. Useful while re-enabling High Availability. (Default: TRUE)
		args.setClearExistingStandbyNameDirs(false);//clearExistingStandbyNameDirs	clearExistingStandbyNameDirs (boolean)	Boolean indicating if the existing name directories for Standby NameNode should be cleared during the workflow. Useful while re-enabling High Availability. (Default: TRUE)
		args.setClearExistingJnEditsDir(false);//clearExistingJnEditsDir	clearExistingJnEditsDir (boolean)	Boolean indicating if the existing edits directories for the JournalNodes for the specified nameservice should be cleared during the workflow. Useful while re-enabling High Availability. (Default: TRUE)

		ApiCommand com = apiRoot.getClustersResource().getServicesResource(clusterName).hdfsEnableNnHaCommand(s.getId(), args);
	//	ApiCommand{id=348, name=EnableNNHA, startTime=Tue Jun 14 11:45:04 CST 2016, endTime=Tue Jun 14 11:45:04 CST 2016, active=false, success=false, resultMessage=Command failed to run because service examples_demo_clusterHDFS has invalid configuration. Review and correct its configuration. First error: There is more than one NameNode and none are configured with a nameservice, serviceRef=ApiServiceRef{peerName=null, clusterName=examples_demo_cluster, serviceName=examples_demo_clusterHDFS}, roleRef=null, hostRef=null, parent=null}false=Command failed to run because service examples_demo_clusterHDFS has invalid configuration. Review and correct its configuration. First error: There is more than one NameNode and none are configured with a nameserviceFound dfs.block.local-path-access.user(dfs.block.local-path-access.user) with dfs_block_local_path_access_user

		if (!com.getSuccess())
			log.error(com.getResultMessage());
		log.info(com.toString());
	}

	private class UpdateConfigInstanceScanner extends InstanceScanner {

		UpdateConfigInstanceScanner(Cluster cluster) {
			super(cluster);
		}

		@Override
		public void process(Instance i) {
			String id = i.getService().getType().toString()
					+ "-" + i.getRole().getType().toString()
					+ "-" + i.getHost().getIp().replaceAll("\\.", "");
			i.setId(id);
		}
	}

	protected String getSaftyValveKey(Service s) {
		//core_site_safety_valve
		//hdfs_service_env_safety_valve
		//hbase_service_env_safety_valve
		//yarn_service_env_safety_valve
		//hive_service_env_safety_valve
		switch (s.getType()) {
			case HDFS: return "hdfs_service_config_safety_valve";
			case ZOOKEEPER: return "zookeeper_config_safety_valve";
			case HBASE: return "hbase_service_config_safety_valve";
			case HIVE: return "hive_service_config_safety_valve";
			case YARN: return "yarn_service_config_safety_valve";
			default: return null;
		}
	}

	protected String getSaftyValveKey(Role r) {
		switch (r.getType()) {
			case NAMENODE: return "namenode_config_safety_valve";
			case BALANCER: return "balancer_config_safety_valve";
			case JOURNALNODE: return "jn_config_safety_valve";
			case FAILOVERCONTROLLER: return "fc_config_safety_valve";
			case HBASETHRIFTSERVER: return "hbase_thriftserver_config_safety_valve";
			case MASTER: return "hbase_master_config_safety_valve";
			case REGIONSERVER: return "hbase_regionserver_config_safety_valve";
			case HBASERESTSERVER: return "hbase_restserver_config_safety_valve";

			case NODEMANAGER: return "nodemanager_config_safety_valve";
			case JOBHISTORY: return "jobhistory_config_safety_valve";
			case RESOURCEMANAGER: return "resourcemanager_config_safety_valve";

			case HIVEMETASTORE: return "hive_metastore_config_safety_valve";
			case HIVESERVER2: return "hive_hs2_config_safety_valve";
			default: return null;
		}
	}

	protected String settingsToXML(StringPairs sp) {
		if ((sp == null) || (sp.isEmpty()))
				return null;
		StringBuilder sb = new StringBuilder();
		for (StringPair pair : sp) {
			sb.append("<property><name>");
			sb.append(pair.getName());
			sb.append("</name><value>");
			sb.append(pair.getValue());
			sb.append("</value>");
			//sb.append("<final>false</final>");
			//sb.append("<description/>");
			sb.append("</property>");
		}
		return sb.toString();
	}

	protected StringPairs updateNodeLocalConfig(ConfigurableTreeNode node,
			ApiConfigList oldConfigList,
			ApiConfigList newConfigList) {

		StringPairs ret = new StringPairs();
		for (StringPair pair : node.getLocalSettings()) {
			ApiConfig found = null;
			String key = pair.getName();
			while (key != null) {
				for (ApiConfig c : oldConfigList) {
					if (c.getRelatedName().equals(key)) {
						found = c;
						c.setValue(pair.getValue());
						newConfigList.add(c);
						String msg = "Found " + key + "(" + pair.getName() + ") with " + c.getName();
						log.info(msg);
						break;
					}
				}

				if (found != null)
					break;
				///< Try new name
				key = node.getSettingManager().getSettingDictionary().getNewName(key);
			}
			if (found == null) {
				ret.add(pair);
				System.err.println(pair.saveToString() + " at " + node.getId() + " not found");
			}
		}

		if (ret.size() == 0)
			return null;
		else
			return ret;
	}

	protected class NameNodeHAInfo {
		Instance nn1;
		Instance nn2;
		Service hdfsService;
	}
	public void provisionCluster(Cluster cluster) throws Exception {
		final String clusterName = cluster.getName();
		
		///
		///< Prepare
		///
		createIdforCluster(cluster);

		///
		///< Step 1: Add Hosts
		///
		ApiHostList a = new ApiHostList();
		for (Host h : cluster.getHostList()) {
			ApiHost h1 = new ApiHost();
			h1.setHostId(h.getId());
			h1.setHostname(h.getFQDN());
			h1.setIpAddress(h.getIp());
			a.add(h1);
		}
		apiRoot.getHostsResource().createHosts(a);
		log.info("Create Host done");

		///
		///< Step 2: Create Cluster
		///
		ApiCluster apiCluster = new ApiCluster();
		apiCluster.setName(clusterName);
		apiCluster.setDisplayName(cluster.getName());
		apiCluster.setVersion(ApiClusterVersion.CDH5);
		apiCluster.setFullVersion("5.1.3");

		ApiClusterList cl = new ApiClusterList();
		cl.add(apiCluster);
		apiRoot.getClustersResource().createClusters(cl);
		log.info("Create Cluster...");

		///
		///< Step 3: Create Service
		///

		List<NameNodeHAInfo> nnhas = new ArrayList<NameNodeHAInfo>();
		ServicesResourceV7 sr = apiRoot.getClustersResource().getServicesResource(cluster.getName());
		
		ApiServiceList services = new ApiServiceList();
		for (ConfigurableTreeNode n : cluster.getChildren()) {
			Service s = (Service) n;
			///
			///< Step 3.1: Add Roles
			///
			List<ApiRole> apiRoles = new ArrayList<ApiRole>();
			for (ConfigurableTreeNode r : s.getChildren()) {
				///< NameNode HA?
				Role rnode = (Role) r;
				if (rnode.getType() == RoleType.NAMENODE) {
					int i = rnode.getInstanceNumber();
					if (i > 2)
						throw new Exception("More than 2 NameNode found");
					else if(i == 2) {
						NameNodeHAInfo ha = new NameNodeHAInfo();
						ha.nn1 = (Instance)rnode.getDefaultGroup().getChildren().get(0);
						ha.nn2 = (Instance)rnode.getDefaultGroup().getChildren().get(1);
						ha.hdfsService = s;
						nnhas.add(ha);

						ApiRole apiRole = new ApiRole();
				        apiRole.setName(ha.nn1.getId());
				        apiRole.setType(RoleType.NAMENODE.toString());
				        apiRole.setHostRef(new ApiHostRef(ha.nn1.getHost().getId()));
				        apiRoles.add(apiRole);
						
				        apiRole = new ApiRole();
				        apiRole.setName(ha.nn2.getId());
				        apiRole.setType(RoleType.SECONDARYNAMENODE.toString());
				        apiRole.setHostRef(new ApiHostRef(ha.nn1.getHost().getId()));
				        apiRoles.add(apiRole);

				        continue; ///< Will fix it in HA
					}
				}
				for(ConfigurableTreeNode g : r.getChildren()) {
					if ((g == null) || !g.hasChild()) continue;
					for (ConfigurableTreeNode i : g.getChildren()) {
						ApiRole apiRole = new ApiRole();
				        apiRole.setName(i.getId());
				        apiRole.setType(((Role)r).getType().toString());
				        apiRole.setHostRef(new ApiHostRef(((Instance)i).getHost().getId()));
				        apiRoles.add(apiRole);
					}
				}
			}

			ApiService apiService = new ApiService();
			apiService.setType(s.getType().toString());
			apiService.setName(s.getId());
			apiService.setDisplayName(s.getId());
			
			if (apiRoles.size() != 0) {
				apiService.setRoles(apiRoles);
			}

			services.add(apiService);
		}
		sr.createServices(services);
		log.info("Create Service done");

		///
		///< Step 4: NameNode HA
		///
//		for (NameNodeHAInfo ha : nnhas) {
//			///< Enable Ha
//			log.info("Enable NameNode HA for " + ha.hdfsService.getId());
//			enableHdfsNameNodeHA(cluster, ha);
//		}				

		///
		///< Step 5: Update Configurations
		///
		for (ConfigurableTreeNode s : cluster.getChildren()) {
			RolesResource rr = sr.getRolesResource(s.getId());
			RoleConfigGroupsResource gr = sr.getRoleConfigGroupsResource(s.getId());

			///< Step 5.1: Update Service Config
			ApiServiceConfig oldServiceConfig = sr.readServiceConfig(s.getId(), DataView.FULL);
			ApiServiceConfig newServiceConfig = new ApiServiceConfig();
			StringPairs unknown = updateNodeLocalConfig(s, oldServiceConfig, newServiceConfig);
			if (newServiceConfig.size() != 0) {
				sr.updateServiceConfig(s.getId(), "message", newServiceConfig);
				log.info("Created Service Config for " + s.getId());
			}
			if (unknown != null)
				log.info("safty-valve=" + settingsToXML(unknown));

			ApiRoleConfigGroupList l = gr.readRoleConfigGroups();
			
			for (ConfigurableTreeNode r : s.getChildren()) {
				///< Step 5.2: Update Role default Config
				Role rnode = (Role)r;
				rnode.getDefaultGroup();
				ApiRoleConfigGroup cg = null;
				ApiConfigList newGroupConfigList = new ApiConfigList();

				if (unknown == null)
					unknown = new StringPairs();
				else
					unknown.clear();

				for (ApiRoleConfigGroup x : l) {
					if (r.getTypeString().equals(x.getRoleType())) {
						ApiConfigList oldGroupConfigList = gr.readConfig(x.getName(), DataView.FULL);
						StringPairs sps = updateNodeLocalConfig(r, oldGroupConfigList, newGroupConfigList);
						if (sps != null)
							unknown.addAll(sps);
					
						sps = updateNodeLocalConfig(rnode.getDefaultGroup(), oldGroupConfigList, newGroupConfigList);
						if (sps != null)
							unknown.addAll(sps);
						cg = x;
						break;
					}
				}
				if ((cg != null) && (newGroupConfigList.size() != 0)) {
					gr.updateConfig(cg.getName(), "message", newGroupConfigList);
					log.info("Created Group Config for " + cg.getName());
				}
				if (!unknown.isEmpty())
					log.info("safty-valve=" + settingsToXML(unknown));

				for(ConfigurableTreeNode g : r.getChildren()) {
					if ((g == null) || !g.hasChild()) continue;

					Group gnode = (Group)g;
					if (!gnode.isDefaultGroup()) {
						///< TODO
					}
					for (ConfigurableTreeNode i : g.getChildren()) {
						//if (i.settingLength() == 0) continue;
						ApiConfigList oldRoleConfig = rr.readRoleConfig(i.getId(), DataView.FULL);
						ApiConfigList newRoleConfig = new ApiConfigList();
						StringPairs sps = updateNodeLocalConfig(i, oldRoleConfig, newRoleConfig);
						//updateNodeLocalConfig(g, oldRoleConfig, newRoleConfig);
						//updateNodeLocalConfig(r, oldRoleConfig, newRoleConfig);

						if (newRoleConfig.size() != 0) {
							rr.updateRoleConfig(i.getId(), "arg1", newRoleConfig);
							log.info("Created Role Config for " + i.getId());
						}
						if (sps != null)
							log.info("safty-valve=" + settingsToXML(sps));

					}
				}
			}
		}
//		hdfs_service_config_safety_valve

		///
		///< Step 5: Fix Configurations
		///
		final String DEFAULT_SERVICE_NAME_HDFS = cluster.getDefaultService(ServiceType.HDFS).getId();
		final String DEFAULT_SERVICE_NAME_HBASE = cluster.getDefaultService(ServiceType.HBASE).getId();
		final String DEFAULT_SERVICE_NAME_ZOOKEEPER = cluster.getDefaultService(ServiceType.ZOOKEEPER).getId();
		final String DEFAULT_SERVICE_NAME_HIVE = cluster.getDefaultService(ServiceType.HIVE).getId();
		final String DEFAULT_SERVICE_NAME_IMPALA = cluster.getDefaultService(ServiceType.IMPALA).getId();
		final String DEFAULT_SERVICE_NAME_YARN = cluster.getDefaultService(ServiceType.YARN).getId();
		///< Step 5.1:
		/// Name of the ZooKeeper service that this HDFS service instance depends on
		/// Default: NULL
		sr.updateServiceConfig(DEFAULT_SERVICE_NAME_HDFS,
				"set zookeeper", 
				packageApiServiceConfig("zookeeper_service", DEFAULT_SERVICE_NAME_ZOOKEEPER));
		
		///< Step 5.2:
		/// Name of the ZooKeeper/HDFS service that this HBase service instance depends on
		/// Default: NULL
		sr.updateServiceConfig(DEFAULT_SERVICE_NAME_HBASE,
				"set hdfs", 
				packageApiServiceConfig("hdfs_service", DEFAULT_SERVICE_NAME_HDFS));
		sr.updateServiceConfig(DEFAULT_SERVICE_NAME_HBASE,
				"set zookeeper", 
				packageApiServiceConfig("zookeeper_service", DEFAULT_SERVICE_NAME_ZOOKEEPER));
		
		///< Step 5.3: Impala
		/// Name of the ZooKeeper/HDFS service that this HBase service instance depends on
		/// Default: NULL
//		sr.updateServiceConfig(DEFAULT_SERVICE_NAME_IMPALA,
//				"set hdfs", 
//				packageApiServiceConfig("hdfs_service", DEFAULT_SERVICE_NAME_HDFS));
//		sr.updateServiceConfig(DEFAULT_SERVICE_NAME_IMPALA,
//				"set zookeeper", 
//				packageApiServiceConfig("hive_service", DEFAULT_SERVICE_NAME_HIVE));
		
		///< Step 5.4: YARN
		/// Name of the ZooKeeper/HDFS service that this HBase service instance depends on
		/// Default: NULL
		sr.updateServiceConfig(DEFAULT_SERVICE_NAME_YARN,
				"set hdfs", 
				packageApiServiceConfig("hdfs_service", DEFAULT_SERVICE_NAME_HDFS));


		waitForServiceStatus(sr, DEFAULT_SERVICE_NAME_ZOOKEEPER, ApiServiceState.STOPPED);
		
		log.info(sr.startCommand(DEFAULT_SERVICE_NAME_ZOOKEEPER).toString());

		try {
			waitForServiceStatus(sr, DEFAULT_SERVICE_NAME_ZOOKEEPER, ApiServiceState.STARTED);
		} catch (Exception e) {
			//< continue;
		}

		/* CM NameNode HA
		 * 1. We cannot enabled it simply by setting config
		 * 2. There must be NN+SNN before HA (V7)
		 * 3. Zookeeper must be started
		 */
		for (NameNodeHAInfo ha : nnhas) {
			///< Enable Ha
			log.info("Enable NameNode HA for " + ha.hdfsService.getId());
			enableHdfsNameNodeHA(cluster, ha);
		}				

	}

	public void waitForServiceStatus(
			ServicesResourceV7 sr,
			String serviceName,
			ApiServiceState target) throws Exception {

		final long CHECK_SERVICE_STATUS_INTERVAL = 10000;
		final int RETRY_TIME = 100;
		int i = 0;
		ApiServiceState as =  sr.readService(serviceName).getServiceState();
		while (as != target) {
			Thread.sleep(CHECK_SERVICE_STATUS_INTERVAL);
			i++;
			if (i > RETRY_TIME)
				throw new Exception(serviceName + " status expected as " + target + ", but " + as + " received.");
			log.info("Waiting for " + serviceName + " service status from " + ((as == null) ? "" : as.toString()) + " to " + target);
			as = sr.readService(serviceName).getServiceState();
		}
	}

	public ApiServiceConfig packageApiServiceConfig(String name, String value) {
		ApiServiceConfig sc = new ApiServiceConfig();
		ApiConfig item = new ApiConfig();
		item.setName(name);
		item.setValue(value);
		sc.add(item);
		return sc;
	}

	public void addConfigCode(PrintStream ps, ApiConfig c, String service, String role) {
		ps.println("addConfig("
//				String id, String name, String defaultValue, ServiceType service, RoleType role) {
				+ "\"" + c.getName() + "\","
				+ "\""+ ((c.getRelatedName() == null) ? null : c.getRelatedName()) + "\","
				//+ ((c.getDefaultValue() == null) ? "null" : ("\"" + c.getDefaultValue().replaceAll("\"", "\\\"") + "\"") + ""
				+ "null,"
				+ ((service == null) ? "null" : ("ServiceType." + service)) + ","
				+ ((role == null) ? "null" : ("RoleType." + role))
				+ ");"
				);
	}

	public void generateParameterDicrtionary() throws Exception {
		Cluster dummyCluster = new Cluster();
		final String DUMMY_CLUSTER_NAME = "dummyCluster" + System.currentTimeMillis();
		final String dummyHostName = "test1fqdn";
		final String dummyHostIp = "127.0.0.2";
		final Host h = dummyCluster.createSingleNodeFullCluster(DUMMY_CLUSTER_NAME, dummyHostName, dummyHostIp);
		provisionCluster(dummyCluster);
		String version = apiRoot.getClouderaManagerResource().getVersion().getVersion().replaceAll("\\.", "");
		Dumper dumper = new Dumper("src/main/java/com/egnore/cmhelper/CMConfigDictionary" + version + ".java");
		PrintStream ps = dumper.getPrintStream();

		ps.println("package com.egnore.cmhelper;");
		ps.println("");
		ps.println("import com.egnore.cluster.model.ServiceType;");
		ps.println("import com.egnore.cluster.model.RoleType;");
		ps.println("");
		ps.println("public class CMConfigDictionary" + version + " extends CMConfigDictionary {");
		ps.println("");
		ps.println("	@Override");
		ps.println("	public void init() {");
		ps.println("		super.init();");

		ClustersResourceV2 clustersResource = apiRoot.getClustersResource();
		ServicesResourceV2 servicesResource = clustersResource.getServicesResource(DUMMY_CLUSTER_NAME);
		for (ApiService service : servicesResource.readServices(DataView.FULL)) {
			ApiServiceConfig cfg = servicesResource.readServiceConfig(service.getName(), DataView.FULL);
			for (ApiConfig c : cfg) {
				addConfigCode(ps, c, service.getType(), null);
			}
			RolesResourceV2 rolesResource = servicesResource.getRolesResource(service.getName());
			for (ApiRole role : rolesResource.readRoles()) {
				ApiConfigList c2 = rolesResource.readRoleConfig(role.getName(), DataView.FULL);
				for (ApiConfig c : c2) {
					addConfigCode(ps, c, service.getType(), role.getType());
				}
			}
		}
		
		ps.println("	}");
		ps.println("}");

		clustersResource.deleteCluster(DUMMY_CLUSTER_NAME);
		apiRoot.getHostsResource().deleteHost(h.getId());
	}

	public void dumpConfig(PrintStream ps, ApiConfig c) {
		ps.print(c.getName()
				+ "\t" + c.getRequired().toString()
				+ "\t" + c.getRelatedName()
				+ "\t" + c.getDisplayName()
//				+ "\t" + c.getValue()
//				+ "\t" + c.getDefaultValue()
//				+ "\t" + c.getValidationState().toString()
				//+ "\t" + c.getDescription()
				);
	}

	public void dumpDefaultConfigurations() throws Exception {
		Cluster dummyCluster = new Cluster();
		final String DUMMY_CLUSTER_NAME = "dummyCluster" + System.currentTimeMillis();
		final String dummyHostName = "test1fqdn";
		final String dummyHostIp = "127.0.0.2";
		final Host h = dummyCluster.createSingleNodeFullCluster(DUMMY_CLUSTER_NAME, dummyHostName, dummyHostIp);
		provisionCluster(dummyCluster);
		System.out.println("version: " + apiRoot.getClouderaManagerResource().getVersion().getVersion());
		Dumper dumper = new Dumper("CM_Config_5_1_3.txt");
		PrintStream ps = dumper.getPrintStream();

		ClustersResourceV2 clustersResource = apiRoot.getClustersResource();
		ServicesResourceV2 servicesResource = clustersResource.getServicesResource(DUMMY_CLUSTER_NAME);
		for (ApiService service : servicesResource.readServices(DataView.FULL)) {
			ApiServiceConfig cfg = servicesResource.readServiceConfig(service.getName(), DataView.FULL);
			for (ApiConfig c : cfg) {
				ps.print("SERVICE\t" + service.getType() + "\t");
				dumpConfig(ps, c);
				ps.println();
			}
////				</strong><em> {u'catch_events': ,  u'dfs_block_access_token_enable': ,  u'dfs_block_size': ,  ... <strong><br />&gt;&gt;&gt; for k, v in sorted(service_conf.items()): ... print "\n------ ", v.displayName, "\n Key:", k, \ ... "\n Value:", v.value, "\n Default:", v.default, \ ... "\n AKA:", v.relatedName, "\n Desc:", v.description</strong> ... ------ Enable log event capture   Key: catch_events   Value: None   Default: true   AKA: None   Desc: When set, each role will identify important log events and forward them to Cloudera Manager. ------ Enable block access token   Key: dfs_block_access_token_enable   Value: None   Default: true   AKA: dfs.block.access.token.enable   Desc: If true, access tokens are used as capabilities for accessing DataNodes. If false, no access tokens are checked on accessing DataNodes. ------ Block Size   Key: dfs_block_size   Value: None   Default: 134217728   AKA: dfs.blocksize   Desc: The default block size for new HDFS files.   ...</em>
			RolesResourceV2 rolesResource = servicesResource.getRolesResource(service.getName());
			for (ApiRole role : rolesResource.readRoles()) {
				ApiConfigList c2 = rolesResource.readRoleConfig(role.getName(), DataView.FULL);
				for (ApiConfig c : c2) {
					ps.print("ROLE\t" + role.getType() + "\t");
					dumpConfig(ps, c);
					ps.println();
					//System.out.println("\t" + c.getRelatedName() + "=" + c.getName() + "[" + c.getDisplayName()
					//		+ "]" + c.getValue());
				}
//					System.out.println("\t\t" + role.getName());
//					System.out.println("\t\t" + role.getType());
//					System.out.println("\t\t" + role.getHostRef());
			}
		}
		clustersResource.deleteCluster(DUMMY_CLUSTER_NAME);
		apiRoot.getHostsResource().deleteHost(h.getId());
	}

	public void dumpClusters() {
		ClustersResourceV2 clustersResource = apiRoot.getClustersResource();
		for (ApiCluster cluster : clustersResource.readClusters(DataView.FULL)) {
			System.out.println(cluster.getName());
			ServicesResourceV2 servicesResource = clustersResource.getServicesResource(cluster.getName());
			for (ApiService service : servicesResource.readServices(DataView.FULL)) {
				System.out.println("\t" + service.getName());
//				ApiServiceConfig cfg = servicesResource.readServiceConfig(service.getName(), DataView.FULL);
//				for (ApiConfig c : cfg) {
//					System.out.println(c.getRelatedName() + "=" + c.getName() + "[" + c.getDisplayName()
//							+ "]" + c.getValue());
//				}
////				</strong><em> {u'catch_events': ,  u'dfs_block_access_token_enable': ,  u'dfs_block_size': ,  ... <strong><br />&gt;&gt;&gt; for k, v in sorted(service_conf.items()): ... print "\n------ ", v.displayName, "\n Key:", k, \ ... "\n Value:", v.value, "\n Default:", v.default, \ ... "\n AKA:", v.relatedName, "\n Desc:", v.description</strong> ... ------ Enable log event capture   Key: catch_events   Value: None   Default: true   AKA: None   Desc: When set, each role will identify important log events and forward them to Cloudera Manager. ------ Enable block access token   Key: dfs_block_access_token_enable   Value: None   Default: true   AKA: dfs.block.access.token.enable   Desc: If true, access tokens are used as capabilities for accessing DataNodes. If false, no access tokens are checked on accessing DataNodes. ------ Block Size   Key: dfs_block_size   Value: None   Default: 134217728   AKA: dfs.blocksize   Desc: The default block size for new HDFS files.   ...</em>
				RolesResourceV2 rolesResource = servicesResource.getRolesResource(service.getName());
				for (ApiRole role : rolesResource.readRoles()) {
//					ApiConfigList c2 = rolesResource.readRoleConfig(role.getName(), DataView.FULL);
//					for (ApiConfig c : c2) {
//						//System.out.println("\t" + c.getRelatedName() + "=" + c.getName() + "[" + c.getDisplayName()
//						//		+ "]" + c.getValue());
//					}
					System.out.println("\t\t" + role.getName());
					System.out.println("\t\t" + role.getType());
					System.out.println("\t\t" + role.getHostRef());
				}
			}
		}

	}
	
	public void startCluster(String clusterName) {
		// Start the first cluster
		ApiCommand cmd = apiRoot.getClustersResource().startCommand(clusterName);
		while (cmd.isActive()) {
		   try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   cmd = apiRoot.getCommandsResource().readCommand(cmd.getId());
		}
		System.out.printf("Cluster start {}", cmd.getSuccess() ? "succeeded" : "failed " + cmd.getResultMessage());
	}

	public void moveCluster() {
//		Export the configuration through the Cloudera Manager API to a JSON file:
//
//			curl -v -u admin:admin http://CURRENT_CM_SERVER:7180/api/v2/cm/deployment > path/to/file
//			3. Using the API, import the configuration using the JSON file.
//
//			curl --upload-file ./cm_current_config -u admin:admin http://NEW_CM_SERVER:7180/api/v2/cm/deployment?deleteCurrentDeployment=true
//			4. Remove Cluster A from the new Cloudera Manager instance.
//
//			5. For servers in Cluster B: (NN, DN) set the following parameters in /etc/cloudera-scm-agent/config.ini:
//
//			# Hostname of Cloudera SCM Server
//			server_host="NEW CM SERVER"
//
//			# Port that server is listening on
//			server_port=7182
	}

/*
  // CM Version Matrix, of form {CM_VERSION=CM_API_VERSION}
  // Entry for the latest CM minor version for each API upgrade, from baseline 4.5.0
  public static Map<String, Integer> VERSION_CM_API_MATRIX = ImmutableMap.of("4.5.0", 3, "4.5.3", 3, "4.6.3", 4,
      "4.8.0", 5, "5.0.0", 6);
  public static String VERSION_CM_API_MATRIX_CM_MIN = VERSION_CM_API_MATRIX.keySet().toArray(
      new String[VERSION_CM_API_MATRIX.size()])[0];
  public static String VERSION_CM_API_MATRIX_CM_MAX = VERSION_CM_API_MATRIX.keySet().toArray(
      new String[VERSION_CM_API_MATRIX.size()])[VERSION_CM_API_MATRIX.size() - 1];
  public static int VERSION_CM_API_MATRIX_CM_MAX_MAJOR = new DefaultArtifactVersion(VERSION_CM_API_MATRIX_CM_MAX)
      .getMajorVersion();
  public static int CM_VERSION_API_EARLIEST = VERSION_CM_API_MATRIX.values().toArray(
      new Integer[VERSION_CM_API_MATRIX.size()])[0];
  public static int VERSION_CM_API_MATRIX_API_MAX = VERSION_CM_API_MATRIX.values().toArray(
      new Integer[VERSION_CM_API_MATRIX.size()])[VERSION_CM_API_MATRIX.size() - 1];

  public static int VERSION_CM_MIN = 5;
  public static int VERSION_CDH_MIN = 4;
  public static int VERSION_CDH_MAX = 5;

  private static final String CDH_REPO_PREFIX = "CDH";

  private static final String CM_PARCEL_STAGE_DOWNLOADED = "DOWNLOADED";
  private static final String CM_PARCEL_STAGE_DISTRIBUTED = "DISTRIBUTED";
  private static final String CM_PARCEL_STAGE_ACTIVATED = "ACTIVATED";

  private static final String CM_CONFIG_UPDATE_MESSAGE = "Update base config group with defaults";

  private static int API_POLL_PERIOD_MS = 500;
  private static int API_POLL_PERIOD_BACKOFF_NUMBER = 3;
  private static int API_POLL_PERIOD_BACKOFF_INCRAMENT = 2;

  private OpLog logger;

  private String version;
  private int versionApi;
  private int versionCdh;
  private CmServerService host;

  final private RootResourceV3 apiResourceRootV3;
  final private RootResourceV4 apiResourceRootV4;
  @SuppressWarnings("unused")
  final private RootResourceV5 apiResourceRootV5;
  final private RootResourceV6 apiResourceRootV6;

  private boolean isFirstStartRequired = true;

  protected CmServerImpl(String version, String vesionApi, String versionCdh, String ip, String ipInternal, int port,
      String user, String password, CmServerLog logger) throws CmServerException {
    this.version = getVersion(version);
    this.versionApi = getVersionApi(this.version, vesionApi);
    this.versionCdh = getVersionCdh(versionCdh);
    this.host = new CmServerServiceBuilder().ip(ip).ipInternal(ipInternal).build();
    this.logger = logger;
    ApiRootResource apiResource = new ClouderaManagerClientBuilder().withHost(ip).withPort(port)
        .withUsernamePassword(user, password).build();
    this.apiResourceRootV3 = apiResource.getRootV3();
    this.apiResourceRootV4 = this.versionApi >= 4 ? apiResource.getRootV4() : null;
    this.apiResourceRootV5 = this.versionApi >= 5 ? apiResource.getRootV5() : null;
    this.apiResourceRootV6 = this.versionApi >= 6 ? apiResource.getRootV6() : null;
  }

  private static String getVersion(String version) throws CmServerException {
    String versionValidated = null;
    if (version != null && !version.equals("")) {
      String versionFullyQualified = version.contains(".") ? version : version + "." + Integer.MAX_VALUE + "."
          + Integer.MAX_VALUE;
      if (new DefaultArtifactVersion(versionFullyQualified).compareTo(new DefaultArtifactVersion(
          VERSION_CM_API_MATRIX_CM_MIN)) < 0
          || new DefaultArtifactVersion(versionFullyQualified).compareTo(new DefaultArtifactVersion(
              new DefaultArtifactVersion(VERSION_CM_API_MATRIX_CM_MAX).getMajorVersion() + "." + Integer.MAX_VALUE
                  + "." + Integer.MAX_VALUE)) > 0) {
        throw new CmServerException("Requested CM version [" + version
            + "] is invalid and cannot be reconciled with CM versions " + VERSION_CM_API_MATRIX.keySet());
      } else {
        versionValidated = version;
      }
    }
    versionValidated = versionValidated == null ? "" + VERSION_CM_API_MATRIX_CM_MAX_MAJOR : versionValidated;
    if (new DefaultArtifactVersion(versionValidated).getMajorVersion() < VERSION_CM_MIN) {
      throw new CmServerException("Requested CM version [" + version + "] is below required mininum [" + VERSION_CM_MIN
          + "]");
    }
    return versionValidated;
  }

  private static int getVersionApi(String version, String versionApi) throws CmServerException {
    Integer versionApiValidated = null;
    if (version == null || version.equals("")) {
      version = VERSION_CM_API_MATRIX_CM_MAX;
    }
    if (!version.contains(".")) {
      String versionLatest = null;
      for (String versionIterator : VERSION_CM_API_MATRIX.keySet()) {
        if (versionIterator.startsWith(version)) {
          versionLatest = versionIterator;
        }
      }
      version = versionLatest;
    }
    if (version != null) {
      ArtifactVersion versionArtifact = new DefaultArtifactVersion(version);
      for (String versionUpperBound : VERSION_CM_API_MATRIX.keySet()) {
        if (versionArtifact.compareTo(new DefaultArtifactVersion(versionUpperBound)) <= 0) {
          versionApiValidated = VERSION_CM_API_MATRIX.get(versionUpperBound);
          break;
        }
        if (versionApiValidated == null) {
          versionApiValidated = VERSION_CM_API_MATRIX_API_MAX;
        }
      }
    }
    if (version == null
        || versionApi != null
        && !versionApi.equals("")
        && (new DefaultArtifactVersion(versionApi)
            .compareTo(new DefaultArtifactVersion(versionApiValidated.toString())) > 0 || new DefaultArtifactVersion(
            versionApi).compareTo(new DefaultArtifactVersion("" + CM_VERSION_API_EARLIEST)) < 0)) {
      throw new CmServerException("Requested CM API version [" + versionApi + "] of CM version [" + version
          + "] could not be reconciled with CM API version matrix " + VERSION_CM_API_MATRIX);
    }
    if (versionApi != null && !StringUtils.isNumeric(versionApi)) {
      throw new CmServerException("CM API version requested is non-numeric [" + versionApi + "]");
    }
    return versionApi == null ? versionApiValidated : Integer.parseInt(versionApi);
  }

  private static int getVersionCdh(String versionCdh) throws CmServerException {
    int versionCdhValidated;
    if (versionCdh == null || versionCdh.equals("")) {
      versionCdhValidated = VERSION_CDH_MAX;
    } else {
      try {
        versionCdhValidated = new DefaultArtifactVersion(versionCdh).getMajorVersion();
        if (versionCdhValidated < 4 || versionCdhValidated > VERSION_CDH_MAX) {
          throw new CmServerException("CDH version requested [" + versionCdh
              + "] is not within the supported major version range [" + VERSION_CDH_MIN + "-" + VERSION_CDH_MAX + "]");
        }
      } catch (Exception e) {
        throw new CmServerException("CDH version requested [" + versionCdh + "] cannot be corelated with CDH versions "
            + Arrays.asList(ApiClusterVersion.values()));
      }
    }
    return versionCdhValidated;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public int getVersionApi() {
    return versionApi;
  }

  @Override
  public int getVersionCdh() {
    return versionCdh;
  }

  @Override
  @CmServerCommandMethod(name = "client")
  public boolean getServiceConfigs(final CmServerCluster cluster, final File directory) throws CmServerException {

    final AtomicBoolean executed = new AtomicBoolean(false);
    try {

      if (isProvisioned(cluster)) {
        logger.logOperation("GetConfig", new CmServerLogSyncCommand() {
          @Override
          public void execute() throws IOException {
            for (ApiService apiService : apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
                .readServices(DataView.SUMMARY)) {
              CmServerServiceType type = CmServerServiceType.valueOfId(apiService.getType());
              if (type.equals(CmServerServiceType.HDFS) || type.equals(CmServerServiceType.MAPREDUCE)
                  || type.equals(CmServerServiceType.YARN) || type.equals(CmServerServiceType.HBASE) || versionApi >= 4
                  && type.equals(CmServerServiceType.HIVE) || versionApi >= 5 && type.equals(CmServerServiceType.SOLR)) {
                ZipInputStream configInputZip = null;
                try {
                  InputStreamDataSource configInput = apiResourceRootV3.getClustersResource()
                      .getServicesResource(getName(cluster)).getClientConfig(apiService.getName());
                  if (configInput != null) {
                    configInputZip = new ZipInputStream(configInput.getInputStream());
                    ZipEntry configInputZipEntry = null;
                    while ((configInputZipEntry = configInputZip.getNextEntry()) != null) {
                      String configFile = configInputZipEntry.getName();
                      if (configFile.contains(File.separator)) {
                        configFile = configFile.substring(configFile.lastIndexOf(File.separator), configFile.length());
                      }
                      directory.mkdirs();
                      BufferedWriter configOutput = null;
                      try {
                        int read;
                        configOutput = new BufferedWriter(new FileWriter(new File(directory, configFile)));
                        while (configInputZip.available() > 0) {
                          if ((read = configInputZip.read()) != -1) {
                            configOutput.write(read);
                          }
                        }
                      } finally {
                        configOutput.close();
                      }
                    }
                  }
                } finally {
                  if (configInputZip != null) {
                    configInputZip.close();
                  }
                }
                executed.set(true);
              }
            }
          }
        });
      }

    } catch (Exception e) {
      throw new CmServerException("Failed to get cluster config", e);
    }

    return executed.get();

  }

  @Override
  public List<CmServerService> getServiceHosts() throws CmServerException {

    final List<CmServerService> services = new ArrayList<CmServerService>();
    try {

      logger.logOperation("GetHosts", new CmServerLogSyncCommand() {
        @Override
        public void execute() {
          for (ApiHost host : apiResourceRootV3.getHostsResource().readHosts(DataView.SUMMARY).getHosts()) {
            services.add(new CmServerServiceBuilder().host(host.getHostId()).ip(host.getIpAddress())
                .ipInternal(host.getIpAddress()).status(CmServerServiceStatus.STARTED).build());
          }
        }
      });

    } catch (Exception e) {
      throw new CmServerException("Failed to list cluster hosts", e);
    }

    return services;
  }

  @Override
  public CmServerService getServiceHost(CmServerService service) throws CmServerException {

    return getServiceHost(service, getServiceHosts());

  }

  @Override
  public CmServerService getServiceHost(CmServerService service, List<CmServerService> services)
      throws CmServerException {

    CmServerService serviceFound = null;
    try {
      for (CmServerService serviceTmp : services) {
        if (service.getHost() != null && service.getHost().equals(serviceTmp.getHost()) || service.getHost() != null
            && service.getHost().equals(serviceTmp.getIp()) || service.getHost() != null
            && service.getHost().equals(serviceTmp.getIpInternal()) || service.getIp() != null
            && service.getIp().equals(serviceTmp.getIp()) || service.getIp() != null
            && service.getIp().equals(serviceTmp.getIpInternal()) || service.getIpInternal() != null
            && service.getIpInternal().equals(serviceTmp.getIp()) || service.getIpInternal() != null
            && service.getIpInternal().equals(serviceTmp.getIpInternal())) {
          serviceFound = serviceTmp;
          break;
        }
      }

    } catch (Exception e) {
      throw new CmServerException("Failed to find service", e);
    }

    return serviceFound;
  }

  @Override
  @CmServerCommandMethod(name = "services")
  public CmServerCluster getServices(final CmServerCluster cluster) throws CmServerException {

    final CmServerCluster clusterView = new CmServerCluster();
    try {
      clusterView.setServer(cluster.getServer());
      List<CmServerService> services = getServiceHosts();
      for (CmServerService server : cluster.getAgents()) {
        if (getServiceHost(server, services) != null) {
          clusterView.addAgent(getServiceHost(server, services));
        }
      }
      if (!cluster.isEmpty() && isProvisioned(cluster)) {
        logger.logOperation("GetServices", new CmServerLogSyncCommand() {
          @Override
          public void execute() throws IOException, CmServerException {
            Map<String, String> ips = new HashMap<String, String>();
            for (ApiService apiService : apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
                .readServices(DataView.SUMMARY)) {
              for (ApiRole apiRole : apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
                  .getRolesResource(apiService.getName()).readRoles()) {
                if (!ips.containsKey(apiRole.getHostRef().getHostId())) {
                  ips.put(apiRole.getHostRef().getHostId(),
                      apiResourceRootV3.getHostsResource().readHost(apiRole.getHostRef().getHostId()).getIpAddress());
                }
                CmServerServiceStatus status = null;
                try {
                  status = CmServerServiceStatus.valueOf(apiRole.getRoleState().toString());
                } catch (IllegalArgumentException exception) {
                  status = CmServerServiceStatus.UNKNOWN;
                }
                try {
                  CmServerService service = new CmServerServiceBuilder().name(apiRole.getName())
                      .host(apiRole.getHostRef().getHostId()).ip(ips.get(apiRole.getHostRef().getHostId()))
                      .ipInternal(ips.get(apiRole.getHostRef().getHostId())).status(status).build();
                  if (service.getType().isConcrete()) {
                    clusterView.addService(service);
                  }
                } catch (IllegalArgumentException exception) {
                  // ignore
                }
              }
            }
          }
        });

      }

    } catch (Exception e) {
      throw new CmServerException("Failed to find services", e);
    }

    return clusterView;

  }

  @Override
  public CmServerService getService(final CmServerCluster cluster, final CmServerServiceType type)
      throws CmServerException {

    return getServices(cluster, type).getService(type, versionApi, versionCdh);

  }

  @Override
  public CmServerCluster getServices(final CmServerCluster cluster, final CmServerServiceType type)
      throws CmServerException {

    final CmServerCluster clusterView = new CmServerCluster();
    try {

      for (CmServerService service : getServices(cluster).getServices(CmServerServiceType.CLUSTER, versionApi,
          versionCdh)) {
        if (type.equals(CmServerServiceType.CLUSTER) || type.equals(service.getType().getParent())
            || type.equals(service.getType())) {
          clusterView.addService(service);
        }
      }

    } catch (Exception e) {
      throw new CmServerException("Failed to find services", e);
    }

    return clusterView;

  }

  @Override
  public boolean isProvisioned(final CmServerCluster cluster) throws CmServerException {

    boolean executed = false;
    try {

      for (ApiCluster apiCluster : apiResourceRootV3.getClustersResource().readClusters(DataView.SUMMARY)) {
        if (apiCluster.getName().equals(getName(cluster))) {
          executed = true;
          break;
        }
      }

    } catch (Exception e) {
      throw new CmServerException("Failed to detrermine if cluster is provisioned", e);
    }

    return executed;

  }

  @Override
  public boolean isConfigured(final CmServerCluster cluster) throws CmServerException {

    boolean executed = false;
    final Set<String> servicesNotConfigured = new HashSet<String>();
    try {

      if (isProvisioned(cluster)) {
        for (CmServerService service : cluster.getServices(CmServerServiceType.CLUSTER, versionApi, versionCdh)) {
          servicesNotConfigured.add(service.getName());
        }
        for (ApiService apiService : apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
            .readServices(DataView.SUMMARY)) {
          for (ApiRole apiRole : apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
              .getRolesResource(apiService.getName()).readRoles()) {
            servicesNotConfigured.remove(apiRole.getName());
          }
        }
        executed = true;
      }

    } catch (Exception e) {
      throw new CmServerException("Failed to detrermine if cluster is configured", e);
    }

    return executed && servicesNotConfigured.size() == 0;

  }

  @Override
  public boolean isStarted(final CmServerCluster cluster) throws CmServerException {

    boolean executed = true;
    final Set<String> servicesNotStarted = new HashSet<String>();
    try {

      if (isConfigured(cluster)) {
        for (CmServerService service : cluster.getServices(CmServerServiceType.CLUSTER, versionApi, versionCdh)) {
          servicesNotStarted.add(service.getName());
        }
        for (ApiService apiService : apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
            .readServices(DataView.SUMMARY)) {
          for (ApiRole apiRole : apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
              .getRolesResource(apiService.getName()).readRoles()) {
            if (apiRole.getRoleState().equals(ApiRoleState.STARTED)) {
              servicesNotStarted.remove(apiRole.getName());
            }
          }
        }
      } else {
        executed = false;
      }

    } catch (Exception e) {
      throw new CmServerException("Failed to detrermine if cluster is started", e);
    }

    return executed ? servicesNotStarted.size() == 0 : false;

  }

  @Override
  public boolean isStopped(final CmServerCluster cluster) throws CmServerException {

    final Set<String> servicesNotStopped = new HashSet<String>();
    try {

      if (isConfigured(cluster)) {
        for (CmServerService service : cluster.getServices(CmServerServiceType.CLUSTER, versionApi, versionCdh)) {
          servicesNotStopped.add(service.getName());
        }
        for (ApiService apiService : apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
            .readServices(DataView.SUMMARY)) {
          for (ApiRole apiRole : apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
              .getRolesResource(apiService.getName()).readRoles()) {
            if (apiRole.getRoleState().equals(ApiRoleState.STOPPED)) {
              servicesNotStopped.remove(apiRole.getName());
            }
          }
        }
      }

    } catch (Exception e) {
      throw new CmServerException("Failed to detrermine if cluster is stopped", e);
    }

    return servicesNotStopped.size() == 0;

  }

  @Override
  @CmServerCommandMethod(name = "initialise")
  public boolean initialise(final CmServerCluster cluster) throws CmServerException {

    boolean executed = false;
    try {

      logger.logOperationStartedSync("ClusterInitialise");

      Map<String, String> configuration = cluster.getServiceConfiguration(versionApi).get(
          CmServerServiceTypeCms.CM.getId());
      configuration.remove("cm_database_name");
      configuration.remove("cm_database_type");
      executed = CmServerServiceTypeCms.CM.getId() != null
          && provisionCmSettings(configuration).size() >= configuration.size();

      logger.logOperationFinishedSync("ClusterInitialise");

    } catch (Exception e) {
      logger.logOperationFailedSync("ClusterInitialise");
      throw new CmServerException("Failed to initialise cluster", e);
    }

    return executed;
  }

  @Override
  public boolean provision(CmServerCluster cluster) throws CmServerException {

    boolean executed = false;
    try {

      logger.logOperationStartedSync("ClusterProvision");

      provisionManagement(cluster);
      if (!cluster.isEmpty() && !isProvisioned(cluster)) {
        provsionCluster(cluster);
        if (cluster.getIsParcel()) {
          provisionParcels(cluster);
        }
        executed = true;
      }

      logger.logOperationFinishedSync("ClusterProvision");

    } catch (Exception e) {
      logger.logOperationFailedSync("ClusterProvision");
      throw new CmServerException("Failed to provision cluster", e);
    }

    return executed;
  }

  @Override
  @CmServerCommandMethod(name = "configure")
  public boolean configure(CmServerCluster cluster) throws CmServerException {

    boolean executed = false;
    try {

      logger.logOperationStartedSync("ClusterConfigure");

      if (!cluster.isEmpty()) {
        if (!isProvisioned(cluster)) {
          provision(cluster);
        }
        if (!isConfigured(cluster)) {
          configureServices(cluster);
          isFirstStartRequired = true;
          executed = true;
        }
      }

      logger.logOperationFinishedSync("ClusterConfigure");

    } catch (Exception e) {
      logger.logOperationFailedSync("ClusterConfigure");
      throw new CmServerException("Failed to configure cluster", e);
    }

    return executed;
  }

  @Override
  public boolean start(final CmServerCluster cluster) throws CmServerException {

    boolean executed = true;
    try {

      logger.logOperationStartedSync("ClusterStart");

      if (!cluster.isEmpty()) {
        if (!isConfigured(cluster)) {
          configure(cluster);
        }
        if (!isStarted(cluster)) {
          for (CmServerServiceType type : cluster.getServiceTypes(versionApi, versionCdh)) {
            if (isFirstStartRequired) {
              for (CmServerService service : cluster.getServices(type, versionApi, versionCdh)) {
                initPreStartServices(cluster, service);
              }
            }
            startService(cluster, type);
            if (isFirstStartRequired) {
              for (CmServerService service : cluster.getServices(type, versionApi, versionCdh)) {
                initPostStartServices(cluster, service);
              }
            }
          }
          isFirstStartRequired = false;
        } else {
          executed = false;
        }

        // push into provision phase once OPSAPS-13194/OPSAPS-12870 is addressed
        startManagement(cluster);

      }

      logger.logOperationFinishedSync("ClusterStart");

    } catch (Exception e) {
      logger.logOperationFailedSync("ClusterStart");
      throw new CmServerException("Failed to start cluster", e);
    }

    return executed;
  }

  @Override
  public boolean stop(final CmServerCluster cluster) throws CmServerException {

    boolean executed = true;
    try {

      logger.logOperationStartedSync("ClusterStop");

      if (!cluster.isEmpty()) {
        if (isConfigured(cluster) && !isStopped(cluster)) {
          final Set<CmServerServiceType> types = new TreeSet<CmServerServiceType>(Collections.reverseOrder());
          types.addAll(cluster.getServiceTypes(versionApi, versionCdh));
          for (CmServerServiceType type : types) {
            stopService(cluster, type);
          }
        } else {
          executed = false;
        }
      }

      logger.logOperationFinishedSync("ClusterStop");

    } catch (Exception e) {
      logger.logOperationFailedSync("ClusterStop");
      throw new CmServerException("Failed to stop cluster", e);
    }

    return executed;
  }

  @Override
  @CmServerCommandMethod(name = "unconfigure")
  public boolean unconfigure(final CmServerCluster cluster) throws CmServerException {

    boolean executed = false;
    try {

      logger.logOperationStartedSync("ClusterUnConfigure");

      if (!cluster.isEmpty()) {
        if (isConfigured(cluster)) {
          if (!isStopped(cluster)) {
            stop(cluster);
          }
          unconfigureServices(cluster);
          executed = true;
        }
      }

      logger.logOperationFinishedSync("ClusterUnConfigure");

    } catch (Exception e) {
      logger.logOperationFailedSync("ClusterUnConfigure");
      throw new CmServerException("Failed to unconfigure cluster", e);
    }

    return executed;
  }

  @Override
  @CmServerCommandMethod(name = "unprovision")
  public boolean unprovision(final CmServerCluster cluster) throws CmServerException {

    boolean executed = false;
    try {

      logger.logOperationStartedSync("ClusterUnProvision");

      if (!cluster.isEmpty()) {
        if (isProvisioned(cluster)) {
          logger.logOperation("UnProvisionCluster", new CmServerLogSyncCommand() {
            @Override
            public void execute() throws IOException {
              apiResourceRootV3.getClustersResource().deleteCluster(getName(cluster));
            }
          });
          executed = true;
        }
      }

      logger.logOperationFinishedSync("ClusterUnProvision");

    } catch (Exception e) {
      logger.logOperationFailedSync("ClusterUnProvision");
      throw new CmServerException("Failed to unprovision cluster", e);
    }

    return executed;

  }

  private String getName(CmServerCluster cluster) {
    try {
      return cluster.getServiceName(CmServerServiceType.CLUSTER);
    } catch (IOException e) {
      throw new RuntimeException("Could not resolve cluster name", e);
    }
  }

  private Map<String, String> provisionCmSettings(Map<String, String> config) throws InterruptedException {

    Map<String, String> configPostUpdate = new HashMap<String, String>();
    ApiConfigList apiConfigList = new ApiConfigList();
    if (config != null && !config.isEmpty()) {
      for (String key : config.keySet()) {
        apiConfigList.add(new ApiConfig(key, config.get(key)));
      }
      apiResourceRootV3.getClouderaManagerResource().updateConfig(apiConfigList);
    }
    apiConfigList = apiResourceRootV3.getClouderaManagerResource().getConfig(DataView.SUMMARY);
    for (ApiConfig apiConfig : apiConfigList) {
      configPostUpdate.put(apiConfig.getName(), apiConfig.getValue());
    }

    return configPostUpdate;

  }

  private void provisionManagement(final CmServerCluster cluster) throws Exception {

    boolean cmsProvisionRequired = false;
    try {
      try {
        cmsProvisionRequired = apiResourceRootV3.getClouderaManagerResource().getMgmtServiceResource()
            .readService(DataView.SUMMARY) == null;
      } catch (RuntimeException exception) {
        cmsProvisionRequired = true;
      }
    } catch (RuntimeException exception) {
      // ignore
    }

    if (cmsProvisionRequired) {

      final ApiHostRef cmServerHostRefApi = new ApiHostRef(getServiceHost(host).getHost());

      boolean licenseDeployed = false;
      try {
        licenseDeployed = apiResourceRootV3.getClouderaManagerResource().readLicense() != null;
      } catch (Exception e) {
        // ignore
      }
      if (versionApi >= 7 && !licenseDeployed) {
        apiResourceRootV6.getClouderaManagerResource().beginTrial();
        licenseDeployed = true;
      }
      final boolean enterpriseDeployed = licenseDeployed;

      if (versionApi >= 4 || licenseDeployed) {
        logger.logOperation("CreateManagementServices", new CmServerLogSyncCommand() {
          @Override
          public void execute() throws IOException, CmServerException, InterruptedException {
            ApiService cmsServiceApi = new ApiService();
            List<ApiRole> cmsRoleApis = new ArrayList<ApiRole>();
            cmsServiceApi.setName(CmServerServiceTypeCms.MANAGEMENT.getName());
            cmsServiceApi.setType(CmServerServiceTypeCms.MANAGEMENT.getId());
            for (CmServerServiceTypeCms type : CmServerServiceTypeCms.values()) {
              if (type.getParent() != null && (!type.getEnterprise() || enterpriseDeployed)) {
                ApiRole cmsRoleApi = new ApiRole();
                cmsRoleApi.setName(type.getName());
                cmsRoleApi.setType(type.getId());
                cmsRoleApi.setHostRef(cmServerHostRefApi);
                cmsRoleApis.add(cmsRoleApi);
              }
            }
            cmsServiceApi.setRoles(cmsRoleApis);

            apiResourceRootV3.getClouderaManagerResource().getMgmtServiceResource().setupCMS(cmsServiceApi);

            for (ApiRoleConfigGroup cmsRoleConfigGroupApi : apiResourceRootV3.getClouderaManagerResource()
                .getMgmtServiceResource().getRoleConfigGroupsResource().readRoleConfigGroups()) {
              try {

                CmServerServiceTypeCms type = CmServerServiceTypeCms.valueOf(cmsRoleConfigGroupApi.getRoleType());
                if (!type.getEnterprise() || enterpriseDeployed) {
                  ApiRoleConfigGroup cmsRoleConfigGroupApiNew = new ApiRoleConfigGroup();
                  ApiServiceConfig cmsServiceConfigApi = new ApiServiceConfig();
                  if (cluster.getServiceConfiguration(versionApi).get(type.getId()) != null) {
                    for (String setting : cluster.getServiceConfiguration(versionApi).get(type.getId()).keySet()) {
                      cmsServiceConfigApi.add(new ApiConfig(setting, cluster.getServiceConfiguration(versionApi)
                          .get(type.getId()).get(setting)));
                    }
                  }
                  cmsRoleConfigGroupApiNew.setConfig(cmsServiceConfigApi);

                  apiResourceRootV3
                      .getClouderaManagerResource()
                      .getMgmtServiceResource()
                      .getRoleConfigGroupsResource()
                      .updateRoleConfigGroup(cmsRoleConfigGroupApi.getName(), cmsRoleConfigGroupApiNew,
                          CM_CONFIG_UPDATE_MESSAGE);

                }
              } catch (IllegalArgumentException e) {
                // ignore
              }
            }
          }
        });
      }
    }

  }

  private void provsionCluster(final CmServerCluster cluster) throws Exception {

    execute(apiResourceRootV3.getClouderaManagerResource().inspectHostsCommand());

    final ApiClusterList clusterList = new ApiClusterList();
    ApiCluster apiCluster = new ApiCluster();
    apiCluster.setName(getName(cluster));
    apiCluster.setVersion(ApiClusterVersion.valueOf(CDH_REPO_PREFIX + versionCdh));
    clusterList.add(apiCluster);

    logger.logOperation("CreateCluster", new CmServerLogSyncCommand() {
      @Override
      public void execute() throws IOException {
        apiResourceRootV3.getClustersResource().createClusters(clusterList);
      }
    });

    List<ApiHostRef> apiHostRefs = Lists.newArrayList();
    for (CmServerService service : getServiceHosts()) {
      apiHostRefs.add(new ApiHostRef(service.getHost()));
    }
    apiResourceRootV3.getClustersResource().addHosts(getName(cluster), new ApiHostRefList(apiHostRefs));

  }

  private void provisionParcels(final CmServerCluster cluster) throws InterruptedException, IOException {

    apiResourceRootV3.getClouderaManagerResource().updateConfig(
        new ApiConfigList(Arrays.asList(new ApiConfig[] { new ApiConfig("PARCEL_UPDATE_FREQ", "1") })));

    final Set<String> repositoriesRequired = new HashSet<String>();
    for (CmServerServiceType type : cluster.getServiceTypes(versionApi, versionCdh)) {
      repositoriesRequired.add(type.getRepository().toString(CDH_REPO_PREFIX + versionCdh));
    }
    final List<String> repositoriesRequiredOrdered = new ArrayList<String>();
    for (String repository : repositoriesRequired) {
      if (repository.equals(CDH_REPO_PREFIX)) {
        repositoriesRequiredOrdered.add(0, repository);
      } else {
        repositoriesRequiredOrdered.add(repository);
      }
    }

    execute("WaitForParcelsAvailability", new Callback() {
      @Override
      public boolean poll() {
        for (ApiParcel parcel : apiResourceRootV3.getClustersResource().getParcelsResource(getName(cluster))
            .readParcels(DataView.FULL).getParcels()) {
          try {
            repositoriesRequired.remove(parcel.getProduct());
          } catch (IllegalArgumentException e) {
            // ignore
          }
        }
        return repositoriesRequired.isEmpty();
      }
    });

    apiResourceRootV3.getClouderaManagerResource().updateConfig(
        new ApiConfigList(Arrays.asList(new ApiConfig[] { new ApiConfig("PARCEL_UPDATE_FREQ", "60") })));

    for (String repository : repositoriesRequiredOrdered) {
      DefaultArtifactVersion parcelVersion = null;
      for (ApiParcel apiParcel : apiResourceRootV3.getClustersResource().getParcelsResource(getName(cluster))
          .readParcels(DataView.FULL).getParcels()) {
        DefaultArtifactVersion parcelVersionTmp = new DefaultArtifactVersion(apiParcel.getVersion());
        if (apiParcel.getProduct().equals(repository)) {
          if (!apiParcel.getProduct().equals(CDH_REPO_PREFIX) || versionCdh == parcelVersionTmp.getMajorVersion()) {
            if (parcelVersion == null || parcelVersion.compareTo(parcelVersionTmp) < 0) {
              parcelVersion = new DefaultArtifactVersion(apiParcel.getVersion());
            }
          }
        }
      }

      final ParcelResource apiParcelResource = apiResourceRootV3.getClustersResource()
          .getParcelsResource(getName(cluster)).getParcelResource(repository, parcelVersion.toString());
      execute(apiParcelResource.startDownloadCommand(), new Callback() {
        @Override
        public boolean poll() {
          return apiParcelResource.readParcel().getStage().equals(CM_PARCEL_STAGE_DOWNLOADED);
        }
      }, false);
      execute(apiParcelResource.startDistributionCommand(), new Callback() {
        @Override
        public boolean poll() {
          return apiParcelResource.readParcel().getStage().equals(CM_PARCEL_STAGE_DISTRIBUTED);
        }
      }, false);
      execute(apiParcelResource.activateCommand(), new Callback() {
        @Override
        public boolean poll() {
          return apiParcelResource.readParcel().getStage().equals(CM_PARCEL_STAGE_ACTIVATED);
        }
      }, false);
    }

  }

  private void configureServices(final CmServerCluster cluster) throws Exception {

    final List<CmServerService> services = getServiceHosts();

    logger.logOperation("CreateClusterServices", new CmServerLogSyncCommand() {
      @Override
      public void execute() throws IOException, InterruptedException, CmServerException {

        ApiServiceList serviceList = new ApiServiceList();
        for (CmServerServiceType type : cluster.getServiceTypes(versionApi, versionCdh)) {

          ApiService apiService = new ApiService();
          List<ApiRole> apiRoles = new ArrayList<ApiRole>();
          apiService.setType(type.getId());
          apiService.setName(cluster.getServiceName(type));

          ApiServiceConfig apiServiceConfig = new ApiServiceConfig();
          if (cluster.getServiceConfiguration(versionApi).get(type.getId()) != null) {
            for (String setting : cluster.getServiceConfiguration(versionApi).get(type.getId()).keySet()) {
              apiServiceConfig.add(new ApiConfig(setting, cluster.getServiceConfiguration(versionApi).get(type.getId())
                  .get(setting)));
            }
          }
          Set<CmServerServiceType> serviceTypes = cluster.getServiceTypes(versionApi, versionCdh);
          switch (type) {
          case YARN:
            apiServiceConfig.add(new ApiConfig("hdfs_service", cluster.getServiceName(CmServerServiceType.HDFS)));
            break;
          case MAPREDUCE:
            apiServiceConfig.add(new ApiConfig("hdfs_service", cluster.getServiceName(CmServerServiceType.HDFS)));
            break;
          case HBASE:
            apiServiceConfig.add(new ApiConfig("hdfs_service", cluster.getServiceName(CmServerServiceType.HDFS)));
            apiServiceConfig.add(new ApiConfig("zookeeper_service", cluster
                .getServiceName(CmServerServiceType.ZOOKEEPER)));
            break;
          case SOLR:
            apiServiceConfig.add(new ApiConfig("hdfs_service", cluster.getServiceName(CmServerServiceType.HDFS)));
            apiServiceConfig.add(new ApiConfig("zookeeper_service", cluster
                .getServiceName(CmServerServiceType.ZOOKEEPER)));
            break;
          case SOLR_INDEXER:
            apiServiceConfig.add(new ApiConfig("hbase_service", cluster.getServiceName(CmServerServiceType.HBASE)));
            apiServiceConfig.add(new ApiConfig("solr_service", cluster.getServiceName(CmServerServiceType.SOLR)));
            break;
          case HUE:
            apiServiceConfig.add(new ApiConfig("hue_webhdfs", cluster.getServiceName(CmServerServiceType.HDFS_HTTP_FS)));
            apiServiceConfig.add(new ApiConfig("oozie_service", cluster.getServiceName(CmServerServiceType.OOZIE)));
            apiServiceConfig.add(new ApiConfig("hive_service", cluster.getServiceName(CmServerServiceType.HIVE)));
            if (serviceTypes.contains(CmServerServiceType.HBASE)) {
              apiServiceConfig.add(new ApiConfig("hbase_service", cluster.getServiceName(CmServerServiceType.HBASE)));
            }
            if (serviceTypes.contains(CmServerServiceType.IMPALA)) {
              apiServiceConfig.add(new ApiConfig("impala_service", cluster.getServiceName(CmServerServiceType.IMPALA)));
            }
            if (serviceTypes.contains(CmServerServiceType.SOLR)) {
              apiServiceConfig.add(new ApiConfig("solr_service", cluster.getServiceName(CmServerServiceType.SOLR)));
            }
            if (serviceTypes.contains(CmServerServiceType.SQOOP)) {
              apiServiceConfig.add(new ApiConfig("sqoop_service", cluster.getServiceName(CmServerServiceType.SQOOP)));
            }
            if (cluster.getService(CmServerServiceType.HBASE_THRIFT_SERVER) != null) {
              apiServiceConfig.add(new ApiConfig("hue_hbase_thrift", cluster
                  .getServiceName(CmServerServiceType.HBASE_THRIFT_SERVER)));
            }
            break;
          case SQOOP:
            apiServiceConfig.add(new ApiConfig("mapreduce_yarn_service", serviceTypes
                .contains(CmServerServiceType.YARN) ? cluster.getServiceName(CmServerServiceType.YARN) : cluster
                .getServiceName(CmServerServiceType.MAPREDUCE)));
            break;
          case OOZIE:
            apiServiceConfig.add(new ApiConfig("mapreduce_yarn_service", serviceTypes
                .contains(CmServerServiceType.YARN) ? cluster.getServiceName(CmServerServiceType.YARN) : cluster
                .getServiceName(CmServerServiceType.MAPREDUCE)));
            break;
          case HIVE:
            apiServiceConfig.add(new ApiConfig("mapreduce_yarn_service", serviceTypes
                .contains(CmServerServiceType.YARN) ? cluster.getServiceName(CmServerServiceType.YARN) : cluster
                .getServiceName(CmServerServiceType.MAPREDUCE)));
            if (versionApi >= 4) {
              apiServiceConfig.add(new ApiConfig("zookeeper_service", cluster
                  .getServiceName(CmServerServiceType.ZOOKEEPER)));
            }
            break;
          case IMPALA:
            apiServiceConfig.add(new ApiConfig("hdfs_service", cluster.getServiceName(CmServerServiceType.HDFS)));
            apiServiceConfig.add(new ApiConfig("hbase_service", cluster.getServiceName(CmServerServiceType.HBASE)));
            apiServiceConfig.add(new ApiConfig("hive_service", cluster.getServiceName(CmServerServiceType.HIVE)));
            break;
          case FLUME:
            apiServiceConfig.add(new ApiConfig("hdfs_service", cluster.getServiceName(CmServerServiceType.HDFS)));
            apiServiceConfig.add(new ApiConfig("hbase_service", cluster.getServiceName(CmServerServiceType.HBASE)));
          default:
            break;
          }
          apiService.setConfig(apiServiceConfig);

          for (CmServerService subService : cluster.getServices(type, versionApi, versionCdh)) {
            if (subService.getType().isValid(versionApi, versionCdh)) {
              CmServerService subServiceHost = getServiceHost(subService, services);
              if (subServiceHost == null || subServiceHost.getHost() == null) {
                throw new CmServerException("Could not find CM agent host to match [" + subService + "]");
              }
              ApiRole apiRole = new ApiRole();
              apiRole.setName(subService.getName());
              apiRole.setType(subService.getType().getId());
              apiRole.setHostRef(new ApiHostRef(subServiceHost.getHost()));
              apiRoles.add(apiRole);
            }
          }

          apiService.setRoles(apiRoles);
          serviceList.add(apiService);

        }

        apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster)).createServices(serviceList);

        for (CmServerServiceType type : cluster.getServiceTypes(versionApi, versionCdh)) {
          for (ApiRoleConfigGroup roleConfigGroup : apiResourceRootV3.getClustersResource()
              .getServicesResource(getName(cluster)).getRoleConfigGroupsResource(cluster.getServiceName(type))
              .readRoleConfigGroups()) {

            ApiConfigList apiConfigList = new ApiConfigList();
            CmServerServiceType roleConfigGroupType = null;
            try {
              roleConfigGroupType = CmServerServiceType.valueOfId(roleConfigGroup.getRoleType());
            } catch (IllegalArgumentException e) {
              // ignore
            }
            if (roleConfigGroupType != null && roleConfigGroupType.equals(CmServerServiceType.GATEWAY)) {
              try {
                roleConfigGroupType = CmServerServiceType.valueOfId(new CmServerServiceBuilder()
                    .name(roleConfigGroup.getServiceRef().getServiceName()).build().getType()
                    + "_" + roleConfigGroup.getRoleType());
              } catch (IllegalArgumentException e) {
                // ignore
              }
            }
            if (roleConfigGroupType != null) {
              Map<String, String> config = cluster.getServiceConfiguration(versionApi).get(roleConfigGroupType.getId());
              if (config != null) {
                for (String setting : config.keySet()) {
                  apiConfigList.add(new ApiConfig(setting, config.get(setting)));
                }
              }
              ApiRoleConfigGroup apiRoleConfigGroup = new ApiRoleConfigGroup();
              apiRoleConfigGroup.setConfig(apiConfigList);
              apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
                  .getRoleConfigGroupsResource(cluster.getServiceName(type))
                  .updateRoleConfigGroup(roleConfigGroup.getName(), apiRoleConfigGroup, CM_CONFIG_UPDATE_MESSAGE);
            }

          }
        }
      }
    });

    // Necessary, since createServices a habit of kicking off async commands (eg ZkAutoInit )
    for (CmServerServiceType type : cluster.getServiceTypes(versionApi, versionCdh)) {
      for (ApiCommand command : apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
          .listActiveCommands(cluster.getServiceName(type), DataView.SUMMARY)) {
        CmServerImpl.this.execute(command, false);
      }
    }

    execute(apiResourceRootV3.getClustersResource().deployClientConfig(getName(cluster)));

  }

  private void unconfigureServices(final CmServerCluster cluster) throws Exception {

    final Set<CmServerServiceType> types = new TreeSet<CmServerServiceType>(Collections.reverseOrder());
    types.addAll(cluster.getServiceTypes(versionApi, versionCdh));
    logger.logOperation("DestroyClusterServices", new CmServerLogSyncCommand() {
      @Override
      public void execute() throws IOException {
        for (final CmServerServiceType type : types) {
          apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
              .deleteService(cluster.getServiceName(type));
        }
      }
    });

  }

  private void initPreStartServices(final CmServerCluster cluster, CmServerService service) throws IOException,
      InterruptedException {

    switch (service.getType().getParent()) {
    case HIVE:
      execute(apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
          .createHiveWarehouseCommand(cluster.getServiceName(CmServerServiceType.HIVE)));
      execute(apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
          .hiveCreateMetastoreDatabaseTablesCommand(cluster.getServiceName(CmServerServiceType.HIVE)), false);
      break;
    case OOZIE:
      execute(
          apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
              .installOozieShareLib(cluster.getServiceName(CmServerServiceType.OOZIE)), false);
      execute(
          apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
              .createOozieDb(cluster.getServiceName(CmServerServiceType.OOZIE)), false);
      break;
    case HBASE:
      execute(apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
          .createHBaseRootCommand(cluster.getServiceName(CmServerServiceType.HBASE)));
    case ZOOKEEPER:
      execute(
          apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
              .zooKeeperInitCommand(cluster.getServiceName(CmServerServiceType.ZOOKEEPER)), false);
      break;
    case SOLR:
      if (versionApi >= 4) {
        execute(
            apiResourceRootV4.getClustersResource().getServicesResource(getName(cluster))
                .initSolrCommand(cluster.getServiceName(CmServerServiceType.SOLR)), false);
        execute(apiResourceRootV4.getClustersResource().getServicesResource(getName(cluster))
            .createSolrHdfsHomeDirCommand(cluster.getServiceName(CmServerServiceType.SOLR)));
      }
      break;
    case SQOOP:
      if (versionApi >= 4) {
        execute(apiResourceRootV4.getClustersResource().getServicesResource(getName(cluster))
            .createSqoopUserDirCommand(cluster.getServiceName(CmServerServiceType.SQOOP)));
      }
      break;
    default:
      break;
    }

    switch (service.getType()) {
    case HDFS_NAMENODE:
      execute(
          apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
              .getRoleCommandsResource(cluster.getServiceName(CmServerServiceType.HDFS))
              .formatCommand(new ApiRoleNameList(ImmutableList.<String> builder().add(service.getName()).build())),
          false);
      break;
    case YARN_RESOURCE_MANAGER:
      if (versionApi >= 6) {
        execute(apiResourceRootV6.getClustersResource().getServicesResource(getName(cluster))
            .createYarnNodeManagerRemoteAppLogDirCommand(cluster.getServiceName(CmServerServiceType.YARN)));
      }
      break;
    case YARN_JOB_HISTORY:
      if (versionApi >= 6) {
        execute(apiResourceRootV6.getClustersResource().getServicesResource(getName(cluster))
            .createYarnJobHistoryDirCommand(cluster.getServiceName(CmServerServiceType.YARN)));
      }
      break;
    case HUE_SERVER:
      execute(
          apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
              .getRoleCommandsResource(cluster.getServiceName(CmServerServiceType.HUE))
              .syncHueDbCommand(new ApiRoleNameList(ImmutableList.<String> builder().add(service.getName()).build())),
          false);
      break;
    default:
      break;
    }

  }

  private void initPostStartServices(final CmServerCluster cluster, CmServerService service) throws IOException,
      InterruptedException {

    switch (service.getType().getParent()) {
    default:
      break;
    }

    switch (service.getType()) {
    case HDFS_NAMENODE:
      ApiRoleNameList formatList = new ApiRoleNameList();
      formatList.add(service.getName());
      execute(apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
          .hdfsCreateTmpDir(cluster.getServiceName(CmServerServiceType.HDFS)));
      break;
    default:
      break;
    }

  }

  private void startManagement(final CmServerCluster cluster) throws InterruptedException {

    try {
      if (apiResourceRootV3.getClouderaManagerResource().getMgmtServiceResource().readService(DataView.SUMMARY)
          .getServiceState().equals(ApiServiceState.STOPPED)) {
        CmServerImpl.this.execute("Start " + CmServerServiceTypeCms.MANAGEMENT.getId().toLowerCase(), apiResourceRootV3
            .getClouderaManagerResource().getMgmtServiceResource().startCommand());
      }
    } catch (RuntimeException exception) {
      // ignore
    }

  }

  private void startService(CmServerCluster cluster, CmServerServiceType type) throws InterruptedException, IOException {
    execute(
        "Start " + type.getId().toLowerCase(),
        apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
            .startCommand(cluster.getServiceName(type)));
  }

  private void stopService(CmServerCluster cluster, CmServerServiceType type) throws InterruptedException, IOException {
    execute(
        "Stop " + type.getId().toLowerCase(),
        apiResourceRootV3.getClustersResource().getServicesResource(getName(cluster))
            .stopCommand(cluster.getServiceName(type)), false);
  }

  private ApiCommand execute(final ApiBulkCommandList bulkCommand, boolean checkReturn) throws InterruptedException {
    ApiCommand lastCommand = null;
    for (ApiCommand command : bulkCommand) {
      lastCommand = execute(command, checkReturn);
    }
    return lastCommand;
  }

  private ApiCommand execute(final ApiCommand command) throws InterruptedException {
    return execute(command, true);
  }

  private ApiCommand execute(String label, final ApiCommand command) throws InterruptedException {
    return execute(label, command, true);
  }

  private ApiCommand execute(final ApiCommand command, boolean checkReturn) throws InterruptedException {
    return execute(command.getName(), command, checkReturn);
  }

  private ApiCommand execute(String label, final ApiCommand command, boolean checkReturn) throws InterruptedException {
    return execute(label, command, new Callback() {
      @Override
      public boolean poll() {
        return apiResourceRootV3.getCommandsResource().readCommand(command.getId()).getEndTime() != null;
      }
    }, checkReturn);
  }

  private ApiCommand execute(ApiCommand command, Callback callback, boolean checkReturn) throws InterruptedException {
    return execute(command.getName(), command, callback, checkReturn);
  }

  private ApiCommand execute(String label, Callback callback) throws InterruptedException {
    return execute(label, null, callback, false);
  }

  private ApiCommand execute(String label, ApiCommand command, Callback callback, boolean checkReturn)
      throws InterruptedException {
    label = WordUtils.capitalize(label.replace("-", " ").replace("_", " ")).replace(" ", "");
    logger.logOperationStartedAsync(label);
    ApiCommand commandReturn = null;
    int apiPollPeriods = 1;
    int apiPollPeriodLog = 1;
    int apiPollPeriodBackoffNumber = API_POLL_PERIOD_BACKOFF_NUMBER;
    while (true) {
      if (apiPollPeriods++ % apiPollPeriodLog == 0) {
        logger.logOperationInProgressAsync(label);
        if (apiPollPeriodBackoffNumber-- == 0) {
          apiPollPeriodLog += API_POLL_PERIOD_BACKOFF_INCRAMENT;
          apiPollPeriodBackoffNumber = API_POLL_PERIOD_BACKOFF_NUMBER;
        }
      }
      if (callback.poll()) {
        if (checkReturn && command != null
            && !(commandReturn = apiResourceRootV3.getCommandsResource().readCommand(command.getId())).getSuccess()) {
          logger.logOperationFailedAsync(label);
          throw new RuntimeException("Command [" + command + "] failed [" + commandReturn + "]");
        }
        logger.logOperationFinishedAsync(label);
        return commandReturn;
      }
      Thread.sleep(API_POLL_PERIOD_MS);
    }
  }

  private static abstract class Callback {
    public abstract boolean poll();
  }
*/
}