package com.egnore.cmhelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import com.egnore.cluster.model.conf.ParameterDescription;
import com.egnore.cluster.model.conf.ParameterDictionary;
import com.egnore.hadoop.conf.ConfigDictionary;

public class Validator {

	protected ParameterDictionary paramDict;
	public void init() {
		paramDict = new ParameterDicrtionary513();
		paramDict.init();
	}

//	ps.print(c.getName()
//			+ "\t" + c.getRequired().toString()
//			+ "\t" + c.getRelatedName()
//			+ "\t" + c.getDisplayName()
	
	//hdfs_service_config_safety_valve
	//core_site_safety_valve
	//balancer_config_safety_valve
	//hbase_conf_safety_valve
	//hive_site_safety_valve
	//config.yarn.service.mapred_safety_valve

	//yarn_service_config_safety_valve
	public void verifyXML(String file) {
		Configuration conf = new Configuration(false);
		conf.clear();
		conf.addResource(new Path(file));
//conf.dumpDeprecatedKeys();

//private static void addDeprecatedKeys() {
//    Configuration.addDeprecations(new DeprecationDelta[] {
//        new DeprecationDelta("yarn.client.max-nodemanagers-proxies",
//            NM_CLIENT_MAX_NM_PROXIES)
//    });
//  }

		for (Entry<String, String> pair : conf) {
			String key = pair.getKey();
			System.out.println(key + "=" + pair.getValue());
			ParameterDescription pd = paramDict.findByName(key);
			boolean found = false;
			if (pd == null) {
				String nn = ConfigDictionary.getLatestName(key);
				if (nn !=null) {
					 pd = paramDict.findByName(nn);
					 if (pd != null) {
						 found = true;
						 System.out.println("[found with \"" + nn + "\"]" + pd.toString());
					 }
				}
			} else {
				found = true;
				System.out.println("[found]" + pd.toString());
			}
			if (!found) 
				System.err.println(key + ": is not predefined");
		}
		/*
		List<Parameter> cfgs = new ArrayList<Parameter>();
		try {
			String encoding="utf8";
			File file=new File("resources/CM_Config_" + version + ".tsv");
			if(file.isFile() && file.exists()){ //判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file),encoding);//考虑到编码格式
                   BufferedReader bufferedReader = new BufferedReader(read);
                   String lineTxt = null;
                   while((lineTxt = bufferedReader.readLine()) != null){
                	   //
                	   Parameter p = new Parameter();
                	   p.id = fields[2]
                	   String[] fields = lineTxt.split("\t");
//					   SERVICE	ZOOKEEPER	process_groupname	FALSE		System Group
//					   ROLE	SERVER	maxSessionTimeout	FALSE	maxSessionTimeout	Maximum Session Timeout                	   Parameter p = new Parameter();
                	   if (fields[0] == "SERVICE") {
                		   
                	   }
                	   
                       System.out.println(lineTxt);
                   }
                   read.close();
       }else{
           System.out.println("找不到指定的文件");
       }
       } catch (Exception e) {
           System.out.println("读取文件内容出错");
           e.printStackTrace();
       }
*/
	}
}
