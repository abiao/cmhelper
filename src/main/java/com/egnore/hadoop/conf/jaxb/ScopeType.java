package com.egnore.hadoop.conf.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum ScopeType {
	// Service
	@XmlEnumValue("ALL") ALL(null),
	@XmlEnumValue("HDFS") HDFS(ALL),
	@XmlEnumValue("YARN") YARN(ALL),
	@XmlEnumValue("ZOOKEEPER") ZOOKEEPER(ALL),
	@XmlEnumValue("HBASE") HBASE(ALL),
	@XmlEnumValue("HIVE") HIVE(ALL),
	@XmlEnumValue("IMPALA") IMPALA(ALL),

	
	// HDFS
	@XmlEnumValue("HDFS.NAMENODE")	HDFS_NAMENODE(HDFS),
	@XmlEnumValue("HDFS.BALANCER")	HDFS_BALANCER(HDFS),
	@XmlEnumValue("HDFS.SECONDARYNAMENODE")	HDFS_SECONDARYNAMENODE(HDFS),
	@XmlEnumValue("HDFS.FAILOVERCONTROLLER")	HDFS_FAILOVERCONTROLLER(HDFS),
	@XmlEnumValue("HDFS.JOURNALNODE")	HDFS_JOURNALNODE(HDFS),
	@XmlEnumValue("HDFS.HTTPFS")	HDFS_HTTPFS(HDFS),
	@XmlEnumValue("HDFS.DATANODE")	HDFS_DATANODE(HDFS),
	@XmlEnumValue("HDFS.NFSGATEWAY")	HDFS_NFSGATEWAY(HDFS),
	@XmlEnumValue("HDFS.CLIENT")	HDFS_CLIENT(HDFS),

	// YARN
	@XmlEnumValue("YARN.JOBHISTORY") YARN_JOBHISTORY(YARN),
	@XmlEnumValue("YARN.RESOURCEMANAGER") YARN_RESOURCEMANAGER(YARN),
	@XmlEnumValue("YARN.NODEMANAGER") YARN_NODEMANAGER(YARN),
	@XmlEnumValue("YARN.CLIENT")	YARN_CLIENT(YARN),

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
	@XmlEnumValue("ZOOKEEPER.SERVER") ZOOKEEPER_SERVER(ZOOKEEPER),
	@XmlEnumValue("ZOOKEEPER.CLIENT") ZOOKEEPER_CLIENT(ZOOKEEPER),

	// HBase
	@XmlEnumValue("HBASE.REGIONSERVER") HBASE_REGIONSERVER(HBASE),
	@XmlEnumValue("HBASE.MASTER") HBASE_MASTER(HBASE),
	@XmlEnumValue("HBASE.THRIFTSERVER") HBASE_THRIFTSERVER(HBASE),
	@XmlEnumValue("HBASE.RESTSERVER") HBASE_RESTSERVER(HBASE),
	@XmlEnumValue("HBASE.CLIENT")	HBASE_CLIENT(HBASE),

	// Hive
	@XmlEnumValue("HIVE.METASTORE")	HIVE_METASTORE(HIVE),
	@XmlEnumValue("HIVE.SERVER2")	HIVE_SERVER2(HIVE),
	@XmlEnumValue("HIVE.WEBHCAT")	HIVE_WEBHCAT(HIVE),
	@XmlEnumValue("HIVE.CLIENT")	HIVE_CLIENT(HIVE),

	// IMPALA
	@XmlEnumValue("IMPALA.STATESTORE") IMPALA_STATESTORE(IMPALA),
	@XmlEnumValue("IMPALA.IMPALAD") IMPALA_IMPALAD(IMPALA),
	@XmlEnumValue("IMPALA.CATALOGSERVER") IMPALA_CATALOGSERVER(IMPALA),
	//@XmlEnumValue("IMPALA.CLIENT")	IMPALA_CLIENT,

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
	@XmlEnumValue("CLIENT") CLIENT(ALL); ///< TODO

	ScopeType parent;

	ScopeType(ScopeType parent) {
		this.parent = parent;
	}

	public ScopeType getParentScope() {
		return parent;
	}

	public boolean isServiceLevel() {
		return ((this != CLIENT) && (this.getParentScope() == ALL));
	}

}
