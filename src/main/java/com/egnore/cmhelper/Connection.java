package com.egnore.cmhelper;

import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.v12.RootResourceV12;

public class Connection {
	RootResourceV12 apiRoot;
	protected String hostName;
	protected String user = "admin";
	protected String passwd = "admin";
	public Connection(String hostname) {
		this.hostName = hostname;

		apiRoot = new ClouderaManagerClientBuilder().withHost(hostname)
				.withUsernamePassword("admin", "admin").build().getRootV12();
	}

	public RootResourceV12 getApi() {
		return apiRoot;
	}
}
