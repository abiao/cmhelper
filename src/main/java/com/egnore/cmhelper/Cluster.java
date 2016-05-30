package com.egnore.cmhelper;

import java.util.List;
import java.util.ArrayList;

import com.cloudera.api.DataView;
import com.cloudera.api.model.ApiCluster;
import com.cloudera.api.model.ApiClusterList;
import com.cloudera.api.model.ApiClusterVersion;
import com.cloudera.api.model.ApiService;
import com.cloudera.api.model.ApiServiceList;
import com.cloudera.api.v1.ServicesResource;
import com.cloudera.api.v7.ClustersResourceV7;

public class Cluster {

	protected Connection connection;
	protected ClustersResourceV7 api;
	
	public Cluster(Connection conn) {
		connection = conn;
		api = connection.getApi().getClustersResource();
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
		ServicesResource sr = api.getServicesResource(DEFAULT_CLUSTER_NAME);
		
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
}
