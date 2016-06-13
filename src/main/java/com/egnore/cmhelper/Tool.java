package com.egnore.cmhelper;

import java.io.FileNotFoundException;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBException;

import com.egnore.cluster.model.Cluster;
import com.egnore.cluster.model.HostManager;
import com.egnore.cluster.model.conf.HadoopConfigManager;
import com.egnore.hadoop.conf.jaxb.Configurations;
import com.sun.jersey.core.spi.factory.ResponseImpl;

public class Tool {

	public static void main(String[] args) throws Exception {

		String hostname = "52.78.44.52";
		//hostname = "192.168.27.178";

		CMExecutor executor  = new CMExecutor(hostname);

		HadoopConfigManager sm = HadoopConfigManager.getInstance();
		HostManager hm = HostManager.getInstance();
		Cluster a = Cluster.loadFromFile("examples/demo_cluster.txt", sm, hm);
		sm.FlowDownConfig(a);
		sm.validateConfiguration(a);
		a.save(null);

		try {
			executor.provisionCluster(a);
		} catch (WebApplicationException e) {
			System.err.println(e.getMessage());
		//e.getResponse().getEntity()
			System.err.println(e.getResponse().toString());
			System.err.println(e.getResponse().getEntity().toString());
			
			
			System.err.println(e.getResponse().getStatus());
			System.err.println(e.getResponse().getMetadata().toString());
			System.err.println(e.getResponse().toString());
			
			throw e;
		}
		if (true) return;
		//		try {
			Validator v = new Validator();
			v.init();
			//v.getParameterDictionary().dumptoXML(null);
			v.verifyXML("file:///Users/biaochen/Downloads/hdfs-site.xml");
			v.verifyXML("file:///Users/biaochen/Downloads/core-site.xml");
			v.verifyXML("file:///Users/biaochen/Downloads/yarn-site.xml");
			v.verifyXML("file:///Users/biaochen/Downloads/mapred-site.xml");
			
			Cluster c = new Cluster(sm, hm);
			c.createTestCluster();
			c.save(null);
			c.save("a.txt");
			Cluster d = new Cluster(sm, hm);
			d.load("a.txt");
			d.save(null);
			
			if (true) return;

//			executor.dumpClusters();
//			executor.dumpDefaultConfigurations();
			executor.generateParameterDicrtionary();
			//executor.provisionCluster(c);
//		} catch (Exception e) {
//			System.out.print(e.getMessage());
//			System.out.print(e.getCause());
//			throw e;
//		}
	}
}
