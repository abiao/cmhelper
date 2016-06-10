package com.egnore.cmhelper;

import java.io.FileNotFoundException;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBException;

import com.egnore.cluster.model.Cluster;
import com.egnore.cluster.model.conf.ParameterDictionary;
import com.egnore.common.model.conf.SettingFactory;
import com.egnore.hadoop.conf.jaxb.Configurations;
import com.sun.jersey.core.spi.factory.ResponseImpl;

public class Tool {

	public static void init() throws FileNotFoundException, JAXBException {
		Configurations.load();

		SettingFactory.getInstance().setSettingDictionary(new ParameterDictionary());
	}

	public static void main(String[] args) throws Exception {
		init();

		String hostname = "52.78.10.82";
		//String hostname = "52.78.28.131";

		CMExecutor executor  = new CMExecutor(hostname);

		Cluster a = Cluster.loadFromFile("examples/demo_cluster.txt");
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
			v.getParameterDictionary().dumptoXML(null);
			v.verifyXML("file:///Users/biaochen/Downloads/hdfs-site.xml");
			v.verifyXML("file:///Users/biaochen/Downloads/core-site.xml");
			v.verifyXML("file:///Users/biaochen/Downloads/yarn-site.xml");
			v.verifyXML("file:///Users/biaochen/Downloads/mapred-site.xml");
			
			Cluster c = new Cluster();
			c.createTestCluster();
			c.save(null);
			c.save("a.txt");
			Cluster d = new Cluster();
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
