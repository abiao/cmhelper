package com.egnore.cmhelper;

import java.io.Console;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.DataView;
//import com.cloudera.api.model.ApiAudit;
//import com.cloudera.api.model.ApiAuditList;
import com.cloudera.api.model.ApiCluster;
import com.cloudera.api.model.ApiConfig;
import com.cloudera.api.model.ApiConfigList;
import com.cloudera.api.model.ApiHost;
import com.cloudera.api.model.ApiHostList;
import com.cloudera.api.model.ApiRole;
import com.cloudera.api.model.ApiRoleConfigGroup;
import com.cloudera.api.model.ApiService;
import com.cloudera.api.model.ApiServiceConfig;
import com.cloudera.api.model.ApiServiceList;
import com.cloudera.api.v1.ClustersResource;
import com.cloudera.api.v1.HostsResource;
import com.cloudera.api.v1.RolesResource;
import com.cloudera.api.v1.ServicesResource;
import com.cloudera.api.v3.RoleConfigGroupsResource;
import com.cloudera.api.v3.ServicesResourceV3;
//import com.cloudera.api.v4.AuditsResource;
import com.cloudera.api.v4.RootResourceV4;
import com.cloudera.api.v7.RootResourceV7;

public class CMConfigReport {
	
	private static final String HOST_REPORT_FILENAME_PROP = "hostReportFilename";
	private static final String HOST_REPORT_FILENAME_DEFAULT = "host-report.html";
	private static final String SERVICE_REPORT_FILENAME_PROP = "serviceReportFilename";
	private static final String SERVICE_REPORT_FILENAME_DEFAULT = "service-report.html";
	private static final String SHOW_DEFAULTS_PROP = "showDefaults";
	private static final String SHOW_DEFAULTS_DEFAULT = "true";
	private static final String SERVICES_PROP = "services";
	private static final String SERVICES_DEFAULT = "hdfs1,mapreduce1,hbase1,oozie1,zookeeper1";
	private static final String REPORTS_PROP = "reports";
	private static final String REPORTS_DEFAULT = "service,host";
	private static final String LOG_LEVEL_PROP = "logLevel";
	private static final String LOG_LEVEL_DEFAULT = "info";

	private static final String CM_HOST_PROP = "host";
	private static final String CM_HOST_DEFAULT = "localhost";
	private static final String CM_USER_PROP = "user";
	private static final String CM_USER_DEFAULT = "admin";
	private static final String CM_PASSWORD_PROP = "password";
	
	private static List<String> VALID_SERVICES = new ArrayList<String>();
	private static List<String> VALID_REPORTS = new ArrayList<String>();
	
	private static StringBuffer SERVICE_REPORT_BUF = new StringBuffer();
	private static StringBuffer HOST_REPORT_BUF = new StringBuffer();

	private static String HOST_REPORT_FILENAME = null;
	private static String SERVICE_REPORT_FILENAME = null;
	private static boolean SHOW_DEFAULTS = Boolean.valueOf(SHOW_DEFAULTS_DEFAULT);
	private static String SERVICES_TO_SHOW = null;
	private static String REPORTS_TO_SHOW = null;
	private static String LOG_LEVEL = null;

	private static String CM_HOST = null;
	private static String CM_USER = null;
	private static String CM_PASSWORD = null;
	
	private static final Logger log = Logger.getLogger(CMConfigReport.class);
	
	static {
		HOST_REPORT_FILENAME = System.getProperty(HOST_REPORT_FILENAME_PROP, HOST_REPORT_FILENAME_DEFAULT);
		SERVICE_REPORT_FILENAME = System.getProperty(SERVICE_REPORT_FILENAME_PROP, SERVICE_REPORT_FILENAME_DEFAULT);
		SHOW_DEFAULTS = Boolean.valueOf(System.getProperty(SHOW_DEFAULTS_PROP, SHOW_DEFAULTS_DEFAULT));
		SERVICES_TO_SHOW = System.getProperty(SERVICES_PROP, SERVICES_DEFAULT);
		REPORTS_TO_SHOW = System.getProperty(REPORTS_PROP, REPORTS_DEFAULT);
		LOG_LEVEL = System.getProperty(LOG_LEVEL_PROP, LOG_LEVEL_DEFAULT);

		CM_HOST = System.getProperty(CM_HOST_PROP, CM_HOST_DEFAULT);
		CM_USER = System.getProperty(CM_USER_PROP, CM_USER_DEFAULT);
		CM_PASSWORD = System.getProperty(CM_PASSWORD_PROP);
		
		if (CM_PASSWORD == null) {
			CM_PASSWORD = readPassword();
		}
		
		VALID_SERVICES = Arrays.asList(SERVICES_TO_SHOW.split(","));
		VALID_REPORTS = Arrays.asList(REPORTS_TO_SHOW.split(","));
		log.setLevel(Level.toLevel(LOG_LEVEL));
	}
	
	private void showOptions() {
		log.info("Usage: java -jar -D[option]=[value] cm-config-report-jar-with-dependencies.jar");
		
		log.info("Options:");
		log.info("  " + HOST_REPORT_FILENAME_PROP + "     (default '" + HOST_REPORT_FILENAME_DEFAULT + "')    Name of file to save host report in.");
		log.info("  " + SERVICE_REPORT_FILENAME_PROP + "  (default '" + SERVICE_REPORT_FILENAME_DEFAULT + "')    Name of file to save service report in.");
		log.info("  " + SHOW_DEFAULTS_PROP + "           (default '" + SHOW_DEFAULTS_DEFAULT + "')    Whether or not to show config default values.");
		log.info("  " + SERVICES_PROP + "               (default '" + SERVICES_DEFAULT + "')    Which CM services to include in the services report.");
		log.info("  " + REPORTS_PROP + "                (default '" + REPORTS_DEFAULT + "')    Comma-separated list of reports to run: service and host are the valid reports.");
		log.info("  " + LOG_LEVEL_PROP + "               (default '" + LOG_LEVEL_DEFAULT + "')    The log level to use: debug, info, warn, error.");
		log.info("  " + CM_HOST_PROP + "                   (default '" + CM_HOST_DEFAULT + "')    The host on which CM is running.");
		log.info("  " + CM_USER_PROP + "                   (default '" + CM_USER_DEFAULT + "')    The user with which to connect to CM.");
		log.info("  " + CM_PASSWORD_PROP + "               (no default)    The password with which to connect to CM.");
		
		log.info("Configuration used for this run:");
		log.info("  " + HOST_REPORT_FILENAME_PROP + "=" + HOST_REPORT_FILENAME);
		log.info("  " + SERVICE_REPORT_FILENAME_PROP + "=" + SERVICE_REPORT_FILENAME);
		log.info("  " + SHOW_DEFAULTS_PROP + "=" + SHOW_DEFAULTS);
		log.info("  " + SERVICES_PROP + "=" + SERVICES_TO_SHOW);
		log.info("  " + REPORTS_PROP + "=" + REPORTS_TO_SHOW);
		log.info("  " + LOG_LEVEL_PROP + "=" + LOG_LEVEL);
		log.info("  " + CM_HOST_PROP + "=" + CM_HOST);
		log.info("  " + CM_USER_PROP + "=" + CM_USER);
		log.info("  " + CM_PASSWORD_PROP + "=********");
	}

	public void generateReport() {
		showOptions();
		
		RootResourceV7 apiRoot = new ClouderaManagerClientBuilder()
				.withHost(CM_HOST)
				.withUsernamePassword(CM_USER, CM_PASSWORD).build().getRootV7();

		try {
			// Get a list of defined clusters
			ClustersResource clustersResource = apiRoot.getClustersResource();
	
			//run the service report if necessary
			if (VALID_REPORTS.contains("service")) {
				outputServicesReport(clustersResource);
			}
			
			//run the host report
			if (VALID_REPORTS.contains("host")) {
				outputHostReport(clustersResource, apiRoot.getHostsResource());
			}
			
//			//TODO: testing audits to try to get config change history; experimental at this point
//			AuditsResource auditResource = apiRoot.getAuditsResource();
////			ApiAuditList auditList = auditResource.readAudits(1000, 1, String.valueOf(System.currentTimeMillis() - 1000000), String.valueOf(System.currentTimeMillis()), "service==hdfs1");
//			ApiAuditList auditList = auditResource.readAudits(1000, 0, null, null, null);
//			System.out.println("Audits");
//			for (ApiAudit audit : auditList) {
//				System.out.println("  audit: " + audit);
//			}
			
		} catch (Exception ex) {
			log.error("Caught exception generating report: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Makes the API calls and builds the report for the hadoop services.
	 * @param clustersResource
	 * @throws Exception
	 */
	private static void outputServicesReport(ClustersResource clustersResource) throws Exception {
		//just use the first cluster; if there are multiple clusters under management, 
		//we'll have to accept an argument for which cluster to report on (or make a
		//separate report for each cluster).
		ApiCluster apiCluster = clustersResource.readClusters(DataView.SUMMARY).getClusters().get(0);
		
		log.info("Services Report for Cluster '" + apiCluster.getName() + "' to be written to " + SERVICE_REPORT_FILENAME);
		
		outputHTMLHead(SERVICE_REPORT_BUF);

		SERVICE_REPORT_BUF.append("<h1>Services Report for Cluster '" + apiCluster.getName() + "'</h1>");

		ServicesResource servicesResource = clustersResource.getServicesResource(apiCluster.getName());
		ApiServiceList apiServiceList = servicesResource.readServices(DataView.EXPORT);
		
		//go through all services and output configs for the service, role config groups, and roles
		int svcCount = -1;
		for(ApiService apiService : apiServiceList) {
			svcCount++;
			String serviceContainerId = "ServiceContainer" + svcCount;
			
			//skip the services we don't care about
			if (!VALID_SERVICES.contains(apiService.getName())) {
				continue;
			}

			//get the roles and role config groups for this service
			RolesResource rolesResource = servicesResource.getRolesResource(apiService.getName());
			RoleConfigGroupsResource roleConfigGroupsResource = ((ServicesResourceV3)servicesResource).getRoleConfigGroupsResource(apiService.getName());
			
			//////////////////////////////
			//Output the service configs
			log.debug("Service " + apiService.getName());
			SERVICE_REPORT_BUF.append("<hr><h2>Service " + apiService.getName() + getToggler(serviceContainerId) + "</h2>");
			outputToggleJS(SERVICE_REPORT_BUF, serviceContainerId);
			outputContainerDiv(SERVICE_REPORT_BUF, serviceContainerId);
			
			log.debug(" Service Configs:");
			SERVICE_REPORT_BUF.append("<h3>Service Configs: " + getToggler(serviceContainerId + "Config") + "</h3>");
			outputToggleJS(SERVICE_REPORT_BUF, serviceContainerId + "Config");
			outputContainerDiv(SERVICE_REPORT_BUF, serviceContainerId + "Config");
			
			if (apiService.getConfig() != null && apiService.getConfig().size() > 0) {
				String tableId = "ServiceConfig" + svcCount;
				SERVICE_REPORT_BUF.append(getTableOpen(tableId));
				
				ApiServiceConfig apiServiceConfig = SHOW_DEFAULTS ?
						servicesResource.readServiceConfig(apiService.getName(), DataView.FULL) :
							apiService.getConfig();
				
				for(ApiConfig apiConfig : apiServiceConfig) {
					log.debug(getConfigOutput(apiConfig, true));
					SERVICE_REPORT_BUF.append(getConfigHTMLOutput(apiConfig, true));
				}
				SERVICE_REPORT_BUF.append(getTableClose());
				outputTableJS(SERVICE_REPORT_BUF, tableId);
			} else {
				SERVICE_REPORT_BUF.append("No overrides");
			}
			SERVICE_REPORT_BUF.append("</div><br />");
			
			//////////////////////////////
			//Role Configs
			log.debug(" Role Config Overrides:");
			SERVICE_REPORT_BUF.append("<h3>Role Configs:" + getToggler(serviceContainerId + "RoleConfig") + "</h3>");
			outputToggleJS(SERVICE_REPORT_BUF, serviceContainerId + "RoleConfig");
			outputContainerDiv(SERVICE_REPORT_BUF, serviceContainerId + "RoleConfig");
			
			if (apiService.getRoles() != null) {
				int rcCount = 0;
				
				//handle each role
				for(ApiRole apiRole : apiService.getRoles()) {
					String rcContainerId = "RCContainer" + svcCount + "_" + rcCount;

					log.debug("  Role " + apiRole.getName());
					SERVICE_REPORT_BUF.append("<h4>Role " + apiRole.getName() + ":" + getToggler(rcContainerId) + "</h4>");
					outputToggleJS(SERVICE_REPORT_BUF, rcContainerId);
					outputContainerDiv(SERVICE_REPORT_BUF, rcContainerId);
					
					//force it to not show defaults so it can pick up the role config overrides without duplicating the info from the RCG
					ApiConfigList apiConfigList = (false && SHOW_DEFAULTS) ? 
							rolesResource.readRoleConfig(apiRole.getName(), DataView.FULL) :
								apiRole.getConfig();
					
					rcCount++;
					if (apiConfigList.size() > 0) {
						String tableId = "RoleConfig" + svcCount + "_" + rcCount;
						SERVICE_REPORT_BUF.append(getTableOpen(tableId));
						
						//output each config in this role
						for (ApiConfig apiConfig : apiConfigList) {
							log.debug(getConfigOutput(apiConfig, true));
							SERVICE_REPORT_BUF.append(getConfigHTMLOutput(apiConfig, true));
						}
						SERVICE_REPORT_BUF.append(getTableClose());
						outputTableJS(SERVICE_REPORT_BUF, tableId);
					} else {
						SERVICE_REPORT_BUF.append("No overrides");
						SERVICE_REPORT_BUF.append("<br />");
					}
					SERVICE_REPORT_BUF.append("</div>");
				}
			}
			SERVICE_REPORT_BUF.append("</div><br />");

			//////////////////////////////			
			//RoleConfigGroup Configs
			log.debug("Role Config Group (RCG) Config Overrides:");
			SERVICE_REPORT_BUF.append("<h3>Role Config Group (RCG) Configs:" + getToggler(serviceContainerId + "RCGConfig") + "</h3>");
			outputToggleJS(SERVICE_REPORT_BUF, serviceContainerId + "RCGConfig");
			outputContainerDiv(SERVICE_REPORT_BUF, serviceContainerId + "RCGConfig");
			
			if (apiService.getRoleConfigGroups() != null) {
				int rcgCount = 0;
				
				//output each role config group
				for(ApiRoleConfigGroup apiRoleConfigGroup : apiService.getRoleConfigGroups()) {
					String rcgContainerId = "RCGContainer" + svcCount + "_" + rcgCount;
					
					log.debug("  RCG " + apiRoleConfigGroup.getName());
					SERVICE_REPORT_BUF.append("<h4>RCG " + apiRoleConfigGroup.getName() + ":" + getToggler(rcgContainerId) + "</h4>");
					outputToggleJS(SERVICE_REPORT_BUF, rcgContainerId);
					outputContainerDiv(SERVICE_REPORT_BUF, rcgContainerId);
					
					rcgCount++;
					if (apiRoleConfigGroup.getConfig().size() > 0) {
						String tableId = "RoleConfigGroup" + svcCount + "_" + rcgCount;
						SERVICE_REPORT_BUF.append(getTableOpen(tableId));
						
						ApiConfigList apiConfigList = SHOW_DEFAULTS ? 
								roleConfigGroupsResource.readConfig(apiRoleConfigGroup.getName(), DataView.FULL) :
									apiRoleConfigGroup.getConfig();

						//output each config
						for(ApiConfig apiConfig : apiConfigList) {
							log.debug(getConfigOutput(apiConfig, true));
							SERVICE_REPORT_BUF.append(getConfigHTMLOutput(apiConfig, true));
						}
						SERVICE_REPORT_BUF.append(getTableClose());
						outputTableJS(SERVICE_REPORT_BUF, tableId);
					} else {
						SERVICE_REPORT_BUF.append("No overrides");
						SERVICE_REPORT_BUF.append("<br />");
					}
					SERVICE_REPORT_BUF.append("</div>");
				}
			}
			SERVICE_REPORT_BUF.append("</div>");
			
			SERVICE_REPORT_BUF.append("</div><br />");
		}

		outputHTMLClose(SERVICE_REPORT_BUF);

		printReport(SERVICE_REPORT_BUF, SERVICE_REPORT_FILENAME);
	}

	/**
	 * Makes the API calls and builds the report for the CM-managed hosts.
	 * @param clustersResource
	 * @param hostResource
	 * @throws Exception
	 */
	private static void outputHostReport(ClustersResource clustersResource, HostsResource hostResource) throws Exception {
		//just use the first cluster; if there are multiple clusters under management, 
		//we'll have to accept an argument for which cluster to report on (or make a
		//separate report for each cluster).
		ApiCluster apiCluster = clustersResource.readClusters(DataView.SUMMARY).getClusters().get(0);
		
		log.info("Host Report for Cluster '" + apiCluster.getName() + "' to be written to " + HOST_REPORT_FILENAME);
		
		outputHTMLHead(HOST_REPORT_BUF);
		
		HOST_REPORT_BUF.append("<h1>Host Report for Cluster '" + apiCluster.getName() + "'</h1>");
		HOST_REPORT_BUF.append("<br />");
		
		ApiHostList apiHostList = hostResource.readHosts(DataView.EXPORT_REDACTED);

		String hostDefaultContainerId = "HostDefaultContainer";		

		//Host Config Defaults
		log.debug("Host Config Defaults");
		HOST_REPORT_BUF.append("<hr><h2>Host Config Defaults" + getToggler(hostDefaultContainerId) + "</h2>");
		outputToggleJS(HOST_REPORT_BUF, hostDefaultContainerId);
		outputContainerDiv(HOST_REPORT_BUF, hostDefaultContainerId);
		
		//use the first host in the list to get the defaults
		ApiConfigList apiConfigList = hostResource.readHostConfig(apiHostList.get(0).getHostId(), DataView.FULL);
		if (apiConfigList != null && apiConfigList.size() > 0) {
			String tableId = "HostDefault";
			HOST_REPORT_BUF.append(getTableOpen(tableId));
			
			for(ApiConfig apiConfig : apiConfigList) {
				//only show defaults in the table
				log.debug(getConfigOutput(apiConfig, false));
				HOST_REPORT_BUF.append(getConfigHTMLOutput(apiConfig, false));
			}
			HOST_REPORT_BUF.append(getTableClose());
			outputTableJS(HOST_REPORT_BUF, tableId);
		} else {
			HOST_REPORT_BUF.append("No overrides");
		}

		HOST_REPORT_BUF.append("</div><br />");

		//////////////////////////////
		//Individual Host Config Overrides
		int hostCount = 0;
		for (ApiHost apiHost : apiHostList) {
			hostCount++;
			
			String hostContainerId = "host" + hostCount;
			
			log.debug("Host " + apiHost.getHostname());
			HOST_REPORT_BUF.append("<hr><h2>Host " + apiHost.getHostname() + getToggler(hostContainerId) + "</h2>");
			outputToggleJS(HOST_REPORT_BUF, hostContainerId);
			outputContainerDiv(HOST_REPORT_BUF, hostContainerId);
			
			apiConfigList = hostResource.readHostConfig(apiHost.getHostId(), DataView.FULL);
			
			if (apiConfigList != null && apiConfigList.size() > 0) {
				String tableId = "Host" + hostCount;
				HOST_REPORT_BUF.append(getTableOpen(tableId));
				
				for(ApiConfig apiConfig : apiConfigList) {
					//only show overrides because we already showed the defaults above
					if (apiConfig.getValue() != null) {
						log.debug(getConfigOutput(apiConfig, true));
						HOST_REPORT_BUF.append(getConfigHTMLOutput(apiConfig, true));
					}
				}
				HOST_REPORT_BUF.append(getTableClose());
				outputTableJS(HOST_REPORT_BUF, tableId);
			} else {
				HOST_REPORT_BUF.append("No overrides");
			}
			HOST_REPORT_BUF.append("</div><br />");	
		}

		outputHTMLClose(SERVICE_REPORT_BUF);

		printReport(HOST_REPORT_BUF, HOST_REPORT_FILENAME);
	}
	
	
	/*********************************************************
	 * Helper methods
	 *********************************************************/

	private static void outputHTMLHead(StringBuffer buf) {		
		buf.append("<html><head>");
		
		buf.append("<script src=\"./jquery-2.0.3.min.js\"></script>");
		buf.append("<script src=\"./jquery.dataTables.min.js\"></script>");
		buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"./cm-config-report.css\" media=\"screen\" />");
		
		buf.append("</head><body>");
	}
	
	private static void outputTableJS(StringBuffer buf, String tableId) {
		buf.append("<script>");
		buf.append("$(document).ready(function() {");
		buf.append("	$('#" + tableId + "').dataTable({\"bInfo\": false, \"bPaginate\": false, \"aaSorting\": [[ 2, \"desc\" ], [0,\"asc\"]],});");
		buf.append("} );");
		buf.append("</script>");
	}

	private static String getConfigHTMLOutput(ApiConfig apiConfig, boolean showOverrides) {
		StringBuffer buf = new StringBuffer();
		String defaultValue = apiConfig.getDefaultValue();
		if (defaultValue != null) {
			if (defaultValue.trim().startsWith("<")) {
				defaultValue = "<textarea rows=\"20\" cols=\"60\" style=\"border:none;\">" + defaultValue + "</textarea>";
			}
//		} else {
//			defaultValue = "";
		}
		String configValue = apiConfig.getValue();
		if (showOverrides && configValue != null) {
			if (configValue.trim().startsWith("<")) {
				configValue = "<textarea rows=\"20\" cols=\"60\" style=\"border:none;\">" + configValue + "</textarea>";
			}
		} else {
			configValue = "";
		}
		buf.append("<tr>")
			.append("<td><strong>").append(apiConfig.getName()).append("</strong></td>")
			.append("<td>").append(defaultValue).append("</td>")
			.append("<td>").append(configValue).append("</td>")
			.append("</tr>");
		
		return buf.toString();
	}

	private static String getConfigOutput(ApiConfig apiConfig, boolean showOverrides) {
		StringBuffer buf = new StringBuffer();
		buf.append("   ")
			.append("prop=").append(apiConfig.getName())
			.append(", default=").append(apiConfig.getDefaultValue())
			.append((!showOverrides || apiConfig.getValue() == null ? "" : ", override=" + apiConfig.getValue()));
		
		return buf.toString();
	}
	
	private static String getTableOpen(String tableId) {
		StringBuffer buf = new StringBuffer();
		buf.append("<table border=\"1px solid black;\" id=\"" + tableId + "\" class=\"display\">"
				+ "<thead><tr><th>Property</th><th>Default</th><th>Override</th></tr></thead>"
				+ "<tbody>");
		return buf.toString();
	}
	
	private static String getTableClose() {
		return "</tbody></table><br />";
	}

	private static String getToggler(String serviceContainerId) {
		return "<button id=\"" + serviceContainerId + "Button\">Hide</button>";
	}

	private static void outputToggleJS(StringBuffer buf, String containerId) {
		buf.append("<script>");
		buf.append("$(document).ready(function() {");
		buf.append("	$('#" + containerId + "Button').click(function(){");
		buf.append("		$(\"#" + containerId + "\").toggle();");
		buf.append("		$(\"#" + containerId + "Button\").html($(\"#" + containerId + "Button\").html() == 'Hide' ? 'Show' : 'Hide');");
		buf.append("	});");
		buf.append("});");
		buf.append("</script>");
	}

	private static void printReport(StringBuffer buf, String fileName) throws Exception {
		FileWriter fw = new FileWriter(fileName);
		StringWriter sw = new StringWriter();
		sw.write(buf.toString());
		fw.write(sw.toString());
		fw.close();
	}

	private static void outputContainerDiv(StringBuffer buf, String containerId) {
		buf.append("<div style=\"margin-left:20px;\" id=\"" + containerId + "\">");
	}

	private static void outputHTMLClose(StringBuffer buf) {
		buf.append("</body></html>");		
	}

	private static String readPassword() {
		Console console = System.console();
	    if (console == null) {
	        log.error("Couldn't get Console instance");
	        System.exit(0);
	    }

	    char passwordArray[] = console.readPassword("Enter your CM password: ");
	    return new String(passwordArray);
	}

}