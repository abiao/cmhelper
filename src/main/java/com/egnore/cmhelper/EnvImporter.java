package com.egnore.cmhelper;

import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.DataView;
import com.cloudera.api.model.ApiCluster;
import com.cloudera.api.model.ApiClusterList;
import com.cloudera.api.model.ApiCommand;
import com.cloudera.api.model.ApiConfig;
import com.cloudera.api.model.ApiConfigList;
import com.cloudera.api.model.ApiHost;
import com.cloudera.api.model.ApiHostRef;
import com.cloudera.api.model.ApiRole;
import com.cloudera.api.model.ApiRoleRef;
import com.cloudera.api.model.ApiService;
import com.cloudera.api.model.ApiServiceConfig;
import com.cloudera.api.v12.ClustersResourceV12;
import com.cloudera.api.v12.RootResourceV12;
import com.cloudera.api.v2.RolesResourceV2;
import com.cloudera.api.v2.ServicesResourceV2;

public class EnvImporter {

	public static void main(String[] args) throws Exception {

		
		
		//https://github.com/cloudera/whirr-cm
		// http://your.cm.com:7180/api/version

//		curl -X POST -H "Content-Type:application/json" -u admin:admin  \
//		 -d '{ "items": [ { "name": "Cluster-1", "version": "CDH5" } ] }'  \
//		 'http://localhost:7180/api/v7/clusters'
//
//		 
//		
//		curl -X POST -H "Content-Type:application/json" -u admin:admin \
//		  -d '{ "items": [ { "name": "ZooKeeper", "type": "ZOOKEEPER" } ] }' \
//		  'http://localhost:7180/api/v7/clusters/Cluster-1/services'
//		  
//		    
//		  curl -X POST -H "Content-Type:application/json" -u admin:admin \
//		  -d '{ "items": [ { "name": "HDFS", "type": "HDFS" } ] }' \
//		  'http://localhost:7180/api/v7/clusters/Cluster-1/services'
//		  
//		 
//		  curl -X POST -H "Content-Type:application/json" -u admin:admin \
//		  -d '{ "items": [ { "name": "YARN", "type": "YARN" } ] }' \
//		  'http://localhost:7180/api/v7/clusters/Cluster-1/services'
//		  
//		 
//		  curl -X POST -H "Content-Type:application/json" -u admin:admin \
//		  -d '{ "items": [ { "name": "Hive", "type": "HIVE" } ] }' \
//		  'http://localhost:7180/api/v7/clusters/Cluster-1/services'
//		  
//		  curl -X POST -H "Content-Type:application/json" -u admin:admin \
//		  -d '{ "items": [ { "name": "Oozie", "type": "OOZIE" } ] }' \
//		  'http://localhost:7180/api/v7/clusters/Cluster-1/services'
//		  
//		 
//		  curl -X POST -H "Content-Type:application/json" -u admin:admin \
//		  -d '{ "items": [ { "name": "Hue", "type": "HUE" } ] }' \
//		  'http://localhost:7180/api/v7/clusters/Cluster-1/services'
//		  
//		   
//		  curl -X POST -H "Content-Type:application/json" -u admin:admin \
//		  -d '{ "items": [ { "name": "Impala", "type": "IMPALA" } ] }' \
//		  'http://localhost:7180/api/v7/clusters/Cluster-1/services'

		  
		RootResourceV12 apiRoot = new ClouderaManagerClientBuilder().withHost("52.78.19.217")
				.withUsernamePassword("admin", "admin").build().getRootV12();
		
		
//		for (ApiHost host : apiRoot.getHostsResource().readHosts(DataView.SUMMARY)) {
//			System.out.println(host.getHostname() + " " + host.getHostId() + " " + host.getHostUrl());
//
////			for (ApiConfig config : host.getConfig()) {
////				System.out.println(config.toString());
////			}
//			for (ApiRoleRef role : host.getRoleRefs()) {
//				System.out.println(role.getRoleName());
//			}
//		}

		ClustersResourceV12 clustersResource = apiRoot.getClustersResource();
		for (ApiCluster cluster : clustersResource.readClusters(DataView.FULL)) {
			System.out.println(cluster.getName());
			ServicesResourceV2 servicesResource = clustersResource.getServicesResource(cluster.getName());
			for (ApiService service : servicesResource.readServices(DataView.FULL)) {
				System.out.println("\t" + service.getName());
				ApiServiceConfig cfg = servicesResource.readServiceConfig(service.getName(), DataView.FULL);
//				for (ApiConfig c : cfg) {
//					System.out.println(c.getRelatedName() + "=" + c.getName() + "[" + c.getDisplayName()
//							+ "]" + c.getValue());
//				}
////				</strong><em> {u'catch_events': ,  u'dfs_block_access_token_enable': ,  u'dfs_block_size': ,  ... <strong><br />&gt;&gt;&gt; for k, v in sorted(service_conf.items()): ... print "\n------ ", v.displayName, "\n Key:", k, \ ... "\n Value:", v.value, "\n Default:", v.default, \ ... "\n AKA:", v.relatedName, "\n Desc:", v.description</strong> ... ------ Enable log event capture   Key: catch_events   Value: None   Default: true   AKA: None   Desc: When set, each role will identify important log events and forward them to Cloudera Manager. ------ Enable block access token   Key: dfs_block_access_token_enable   Value: None   Default: true   AKA: dfs.block.access.token.enable   Desc: If true, access tokens are used as capabilities for accessing DataNodes. If false, no access tokens are checked on accessing DataNodes. ------ Block Size   Key: dfs_block_size   Value: None   Default: 134217728   AKA: dfs.blocksize   Desc: The default block size for new HDFS files.   ...</em>
				RolesResourceV2 rolesResource = servicesResource.getRolesResource(service.getName());
				for (ApiRole role : rolesResource.readRoles()) {
					ApiConfigList c2 = rolesResource.readRoleConfig(role.getName(), DataView.FULL);
					for (ApiConfig c : c2) {
						System.out.println("\t" + c.getRelatedName() + "=" + c.getName() + "[" + c.getDisplayName()
								+ "]" + c.getValue());
					}
					//rolesResource.updateRoleConfig(arg0, arg1, arg2)
					System.out.println("\t\t" + role.getName());
					System.out.println("\t\t" + role.getType());
					System.out.println("\t\t" + role.getHostRef().getHostId());
					System.out.println("\t\t" + role.getHostRef().toString());
				}
			}
		}
		
	
		// List of clusters
//		ApiClusterList clusters = apiRoot.getClustersResource().readClusters(DataView.SUMMARY);
//		for (ApiCluster cluster : clusters) {
//			System.out.println("{}: {}" + cluster.getName() + cluster.getClass().getName() + " " +  cluster.getVersion());
//			System.out.println(cluster.getServices());
//			for (ApiService service : apiRoot.getClustersResource().getServicesResource(cluster.getName()).readServices(DataView.SUMMARY)) {
//				for (ApiRole role : service.getRoles()) {
//					ApiHostRef host = role.getHostRef();
//					System.out.print(role.getName() + " " + host.getHostId());
//				}
//			}
//		}

//		<strong><em>&gt;&gt;&gt; service_conf, roletype_conf = hdfs.get_config(<span style="color: #cc0033;">view="full"</span>) &gt;&gt;&gt; print service_conf</em></strong><em> {u'catch_events': ,  u'dfs_block_access_token_enable': ,  u'dfs_block_size': ,  ... <strong><br />&gt;&gt;&gt; for k, v in sorted(service_conf.items()): ... print "\n------ ", v.displayName, "\n Key:", k, \ ... "\n Value:", v.value, "\n Default:", v.default, \ ... "\n AKA:", v.relatedName, "\n Desc:", v.description</strong> ... ------ Enable log event capture   Key: catch_events   Value: None   Default: true   AKA: None   Desc: When set, each role will identify important log events and forward them to Cloudera Manager. ------ Enable block access token   Key: dfs_block_access_token_enable   Value: None   Default: true   AKA: dfs.block.access.token.enable   Desc: If true, access tokens are used as capabilities for accessing DataNodes. If false, no access tokens are checked on accessing DataNodes. ------ Block Size   Key: dfs_block_size   Value: None   Default: 134217728   AKA: dfs.blocksize   Desc: The default block size for new HDFS files.   ...</em>

		// Start the first cluster
//		ApiCommand cmd = apiRoot.getClustersResource().startCommand(clusters.get(0).getName());
//		while (cmd.isActive()) {
//		   Thread.sleep(100);
//		   cmd = apiRoot.getCommandsResource().readCommand(cmd.getId());
//		}
//		LOG.info("Cluster start {}", cmd.getSuccess() ? "succeeded" : "failed " + cmd.getResultMessage());
	}
}
