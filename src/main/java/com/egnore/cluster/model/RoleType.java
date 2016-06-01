package com.egnore.cluster.model;


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
}