package com.egnore.cmhelper;

import com.egnore.cluster.model.Cluster;

public class Tool {
	public static void main(String[] args) throws Exception {
		try {
			Cluster c = new Cluster();
			c.createTestCluster();
			CMExecutor executor  = new CMExecutor("52.78.20.67");
//			executor.dumpClusters();
			executor.dumpDefaultConfigurations();
		//	executor.provisionCluster(c);
		} catch (Exception e) {
			System.out.print(e.getMessage());
			System.out.print(e.getCause());
			throw e;
		}
	}
}
