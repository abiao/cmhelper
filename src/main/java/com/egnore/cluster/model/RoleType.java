package com.egnore.cluster.model;

import javax.xml.bind.annotation.XmlEnumValue;

import com.egnore.hadoop.conf.jaxb.ScopeType;


public enum RoleType {
	// Cluster
	// CLUSTER(null, "CLUSTER", CmServerServiceTypeRepo.CDH, 3,
	// CmServerService.VERSION_UNBOUNDED, 4,
	// CmServerService.VERSION_UNBOUNDED),
	//
	// // Gateway
	// GATEWAY(null, "GATEWAY", CmServerServiceTypeRepo.CDH, 3,
	// CmServerService.VERSION_UNBOUNDED, 4,
	// CmServerService.VERSION_UNBOUNDED),

	// HDFS,
	NAMENODE(ServiceType.HDFS),
	SECONDARYNAMENODE(ServiceType.HDFS),
	BALANCER(ServiceType.HDFS),
	FAILOVERCONTROLLER(ServiceType.HDFS),
	JOURNALNODE(ServiceType.HDFS),
	HTTPFS(ServiceType.HDFS),
	DATANODE(ServiceType.HDFS),

	// YARN,
	//YARN_GATEWAY
	JOBHISTORY(ServiceType.YARN),
	RESOURCEMANAGER(ServiceType.YARN),
	NODEMANAGER(ServiceType.YARN),

	// MapReduce
	//MAPREDUCE",
	//"MAPREDUCE_GATEWAY", CmServerServiceTypeRepo.CDH, 3,
//			CmServerService.VERSION_UNBOUNDED, 4, 4), MAPREDUCE_JOB_TRACKER(
//			MAPREDUCE, "JOBTRACKER", CmServerServiceTypeRepo.CDH, 3,
//			CmServerService.VERSION_UNBOUNDED, 4, 4), MAPREDUCE_TASK_TRACKER(
//			MAPREDUCE, "TASKTRACKER", CmServerServiceTypeRepo.CDH, 3,
//			CmServerService.VERSION_UNBOUNDED, 4, 4),
//
//	SPARK(CLUSTER, "SPARK", CmServerServiceTypeRepo.SPARK, 6,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED), SPARK_MASTER(SPARK,
//			"SPARK_MASTER", CmServerServiceTypeRepo.SPARK, 6,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED), SPARK_WORKER(SPARK,
//			"SPARK_WORKER", CmServerServiceTypeRepo.SPARK, 6,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED),

	// Zookeeper
	//ZOOKEEPER,
	SERVER(ServiceType.ZOOKEEPER),

	// HBase
	//HBASE,
	//HBASE_GATEWAY
	MASTER(ServiceType.HBASE),
	HBASETHRIFTSERVER(ServiceType.HBASE),
	HBASERESTSERVER(ServiceType.HBASE),
	REGIONSERVER(ServiceType.HBASE),

	// Hive
	//HIVE,
	//HIVE_GATEWAY,
	HIVEMETASTORE(ServiceType.HIVE),
	HIVESERVER2(ServiceType.HIVE),
	WEBHCAT(ServiceType.HIVE),

	// IMPALA
	STATESTORE(ServiceType.IMPALA),
	IMPALAD(ServiceType.IMPALA),
	CATALOGSERVER(ServiceType.IMPALA),

//	// Solr
//	SOLR(CLUSTER, "SOLR", CmServerServiceTypeRepo.SOLR, 4,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED), SOLR_SERVER(SOLR,
//			"SOLR_SERVER", CmServerServiceTypeRepo.SOLR, 4,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED),
//
//	// Solr Indexers
//	SOLR_INDEXER(CLUSTER, "KS_INDEXER", CmServerServiceTypeRepo.SOLR, 5,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED), SOLR_INDEXER_HBASE(
//			SOLR_INDEXER, "HBASE_INDEXER", CmServerServiceTypeRepo.SOLR, 5,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED),
//
//	// Sqoop
//	SQOOP(CLUSTER, "SQOOP", CmServerServiceTypeRepo.CDH, 4,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED), SQOOP_SERVER(SQOOP,
//			"SQOOP_SERVER", CmServerServiceTypeRepo.CDH, 4,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED),
//
//	// Oozie
//	OOZIE(CLUSTER, "OOZIE", CmServerServiceTypeRepo.CDH, 3,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED), OOZIE_SERVER(OOZIE,
//			"OOZIE_SERVER", CmServerServiceTypeRepo.CDH, 3,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED),
//
//
//	// Flume
//	FLUME(CLUSTER, "FLUME", CmServerServiceTypeRepo.CDH, 3,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED), FLUME_AGENT(FLUME, "AGENT",
//			CmServerServiceTypeRepo.CDH, 3, CmServerService.VERSION_UNBOUNDED,
//			4, CmServerService.VERSION_UNBOUNDED),
//
//	// Hue
//	HUE(CLUSTER, "HUE", CmServerServiceTypeRepo.CDH, 3,
//			CmServerService.VERSION_UNBOUNDED, 4,
//			CmServerService.VERSION_UNBOUNDED), HUE_SERVER(HUE, "HUE_SERVER",
//			CmServerServiceTypeRepo.CDH, 3, CmServerService.VERSION_UNBOUNDED,
//			4, CmServerService.VERSION_UNBOUNDED), HUE_BEESWAX_SERVER(HUE,
//			"BEESWAX_SERVER", CmServerServiceTypeRepo.CDH, 3,
//			CmServerService.VERSION_UNBOUNDED, 4, 4),

	// Client
	GATEWAY(null);

	private ServiceType parent;

	RoleType(ServiceType service) {
		this.parent = service;
	}

	public ServiceType getServiceType() {
		return parent;
	}

	static public RoleType fromScopeType(ScopeType scope) {
		switch (scope) {
			case HDFS_NAMENODE: return RoleType.NAMENODE;
			case HDFS_BALANCER: return RoleType.BALANCER;
			case HDFS_SECONDARYNAMENODE: return RoleType.SECONDARYNAMENODE;
			case HDFS_FAILOVERCONTROLLER: return RoleType.FAILOVERCONTROLLER;
			case HDFS_JOURNALNODE: return RoleType.JOURNALNODE;
			case HDFS_HTTPFS: return RoleType.HTTPFS;
			case HDFS_DATANODE: return RoleType.DATANODE;
			//case HDFS_NFSGATEWAY: return RoleType.;
			//case HDFS_CLIENT: return RoleType;
	
			
			// YARN
			case YARN_JOBHISTORY: return RoleType.JOBHISTORY;
			case YARN_RESOURCEMANAGER: return RoleType.RESOURCEMANAGER;
			case YARN_NODEMANAGER: return RoleType.NODEMANAGER;
			//case YARN_CLIENT: return RoleType.;
	
			// Zookeeper
			case ZOOKEEPER_SERVER: return RoleType.SERVER;
			//case ZOOKEEPER_CLIENT: return RoleType;
	
			// HBase
			case HBASE_REGIONSERVER: return RoleType.REGIONSERVER;
			case HBASE_MASTER: return RoleType.MASTER;
	//		case HBASE_THRIFTSERVER: return RoleType;
	//		case HBASE_RESTSERVER: return RoleType;
	//		case HBASE_CLIENT: return RoleType;
	
			// Hive
			case HIVE_METASTORE: return RoleType.HIVEMETASTORE;
			case HIVE_SERVER2: return RoleType.HIVESERVER2;
	//		case HIVE_WEBHCAT: return RoleType;
	//		case HIVE_CLIENT: return RoleType;
	
			// IMPALA
			case IMPALA_STATESTORE: return RoleType.STATESTORE;
			case IMPALA_IMPALAD: return RoleType.IMPALAD;
			case IMPALA_CATALOGSERVER: return RoleType.CATALOGSERVER;
			default:
				//return RoleType.GATEWAY;
				return null;
		}
	}

	public ScopeType toScopeType() {
		switch (this) {
			case NAMENODE: return ScopeType.HDFS_NAMENODE;
			case BALANCER: return ScopeType.HDFS_BALANCER;
			case SECONDARYNAMENODE: return ScopeType.HDFS_SECONDARYNAMENODE;
			case FAILOVERCONTROLLER: return ScopeType.HDFS_FAILOVERCONTROLLER;
			case JOURNALNODE: return ScopeType.HDFS_JOURNALNODE;
			case HTTPFS: return ScopeType.HDFS_HTTPFS;
			case DATANODE: return ScopeType.HDFS_DATANODE;
			//case HDFS_NFSGATEWAY: return RoleType.;
			//case HDFS_CLIENT: return RoleType;
		
			
			// YARN
			case JOBHISTORY: return ScopeType.YARN_JOBHISTORY;
			case RESOURCEMANAGER: return ScopeType.YARN_RESOURCEMANAGER;
			case NODEMANAGER: return ScopeType.YARN_NODEMANAGER;
			//case YARN_CLIENT: return RoleType.;
		
			// Zookeeper
			case SERVER: return ScopeType.ZOOKEEPER_SERVER;
			//case ZOOKEEPER_CLIENT: return RoleType;
		
			// HBase
			case REGIONSERVER: return ScopeType.HBASE_REGIONSERVER;
			case MASTER: return ScopeType.HBASE_MASTER;
		//		case HBASE_THRIFTSERVER: return RoleType;
		//		case HBASE_RESTSERVER: return RoleType;
		//		case HBASE_CLIENT: return RoleType;
		
			// Hive
			case HIVEMETASTORE: return ScopeType.HIVE_METASTORE;
			case HIVESERVER2: return ScopeType.HIVE_SERVER2;
		//		case HIVE_WEBHCAT: return RoleType;
		//		case HIVE_CLIENT: return RoleType;
		
			// IMPALA
			case STATESTORE: return ScopeType.IMPALA_STATESTORE;
			case IMPALAD: return ScopeType.IMPALA_IMPALAD;
			case CATALOGSERVER: return ScopeType.IMPALA_CATALOGSERVER;
			default:
				return ScopeType.CLIENT;
		}
	}
}