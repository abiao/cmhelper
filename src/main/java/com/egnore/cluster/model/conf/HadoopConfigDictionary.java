package com.egnore.cluster.model.conf;

import java.util.ArrayList;
import java.util.List;

import com.egnore.cluster.model.RoleType;
import com.egnore.cluster.model.ServiceType;
import com.egnore.common.model.conf.SettingDescription;
import com.egnore.common.Dictionary;
import com.egnore.common.util.StringUtil;

public class HadoopConfigDictionary extends Dictionary<HadoopConfigDescription> {

	protected List<List<HadoopConfigDescription> > services;
	protected List<List<HadoopConfigDescription> > roles;

	@Override
	public void init() {
		int len;
		
		len = ServiceType.values().length;
		services = new ArrayList<List<HadoopConfigDescription>>(len);
		for (int i = 0; i < len; i++) {
			services.add(new ArrayList<HadoopConfigDescription>());
		}

		len = RoleType.values().length;
		roles = new ArrayList<List<HadoopConfigDescription>>(len);
		for (int i = 0; i < len; i++) {
			roles.add(new ArrayList<HadoopConfigDescription>());
		}
	}

	@Override
	protected void buildIndex(HadoopConfigDescription sd) {
		super.buildIndex(sd);

		if (sd.service != null) services.get(sd.service.ordinal()).add(sd);
		if (sd.role != null) roles.get(sd.role.ordinal()).add(sd);
		
	}

//	public void dumptoXML(String path) {
//		for (HadoopConfigDescription s : this.) {
//			if(StringUtil.isValidString(s.getName())) {
//				ParameterDescription p = (ParameterDescription) s;
//				System.out.println("  <configuration>");
//				System.out.println("    <name>"+p.getName()+"</name>");
//				String scope = (p.role == null) ? p.service.toString() : (p.service.toString()+"."+p.role.toString());
//				System.out.println("    <scope>"+scope+"</scope>");
//				System.out.println("    <file>" + p.service.toString().toLowerCase() + "-site.xml</file>");
//				System.out.println("  </configuration>");
//			}
//		}
//	}
}
