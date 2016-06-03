package com.egnore.cmhelper;

import com.egnore.cluster.model.Cluster;

public class Tool {
	public static void main(String[] args) throws Exception {
		try {
			Validator v = new Validator();
			v.init();
			v.verifyXML("file:///Users/biaochen/Downloads/hdfs-site.xml");
			v.verifyXML("file:///Users/biaochen/Downloads/core-site.xml");
			v.verifyXML("file:///Users/biaochen/Downloads/yarn-site.xml");
			v.verifyXML("file:///Users/biaochen/Downloads/mapred-site.xml");
			
			if (true) return;
			Cluster c = new Cluster();
			c.createTestCluster();
			String hostname = "52.78.10.82";
			//String hostname = "52.78.28.131";

			CMExecutor executor  = new CMExecutor(hostname);
//			executor.dumpClusters();
//			executor.dumpDefaultConfigurations();
			executor.generateParameterDicrtionary();
			//executor.provisionCluster(c);
		} catch (Exception e) {
			System.out.print(e.getMessage());
			System.out.print(e.getCause());
			throw e;
		}
	}
}
