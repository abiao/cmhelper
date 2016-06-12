package com.egnore.hadoop.conf.jaxb;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="configurations")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configurations {

	@XmlElement(name="configuration")
	List<Configuration> configs;

	static protected final String XML_FILE_PATH="resources/hadoop_configuration.xml";

	static public Configurations load() throws FileNotFoundException, JAXBException {
		return loadFromFile(XML_FILE_PATH);
	}

	static public Configurations loadFromFile(String path) throws FileNotFoundException, JAXBException {
		return loadFromReader(new FileReader(path));
	}

	static public Configurations loadFromString(String s) throws JAXBException {
		return loadFromReader(new StringReader(s));
	}

	static public Configurations loadFromReader(Reader r) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Configurations.class);  
		Unmarshaller unmarshaller = context.createUnmarshaller();  
		return (Configurations) unmarshaller.unmarshal(r);
	}

	public List<Configuration> getList() {
		return configs;
	}

	public void dump() {
		for (Configuration c : configs) {
			System.out.println(c.saveToString());
		}
	}

	public static void main(String[] args) throws JAXBException, FileNotFoundException {  
		JAXBContext context = JAXBContext.newInstance(Configurations.class);  
		 
		Marshaller marshaller = context.createMarshaller();  

		Configurations dummy = new Configurations();
		dummy.configs = new ArrayList<Configuration>();
		Configuration c = new Configuration();
		c.name="dfs.datanode.dir";
		c.file="hdfs-site.xml";
		c.scope = ScopeType.HDFS_DATANODE;
		dummy.configs.add(c);

		Configuration d = new Configuration();
		d.name="dfs.datanode.dir";
		d.file="hdfs-site.xml";
		d.scope = ScopeType.HDFS_DATANODE;
		dummy.configs.add(d);
		
		marshaller.marshal(dummy, System.out);  
		System.out.println(c);  
		 
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><configurations><configuration><naxme>dfs.datanode.dir</naxme><file>hdfs-site.xml</file><scope>HDFS.DATANODE</scope><deprecated>false</deprecated></configuration><configuration><name>dfs.datanode.dir</name><file>hdfs-site.xml</file><scope>HDFS.DATANODE</scope><deprecated>false</deprecated></configuration></configurations>";
		Configurations c2 = Configurations.loadFromString(xml);  
		c2.dump();  
		c2 = Configurations.loadFromFile(XML_FILE_PATH);
		c2.dump();  
	}  
}