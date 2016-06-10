package com.egnore.hadoop.conf.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum Scope {
	// Service
	@XmlEnumValue("HDFS") HDFS_ALL,
	@XmlEnumValue("YARN") YARN_ALL,
//	@XmlEnumValue("ZOOKEEPER") ZOOKEEPER_ALL,
	@XmlEnumValue("HBASE") HBASE_ALL,
	@XmlEnumValue("HIVE") HIVE_ALL,
	@XmlEnumValue("IMPALA") IMPALA_ALL,

	
	// HDFS
	@XmlEnumValue("HDFS.NAMENODE")	HDFS_NAMENODE,
	@XmlEnumValue("HDFS.BALANCER")	HDFS_BALANCER,
	@XmlEnumValue("HDFS.SECONDARYNAMENODE")	HDFS_SECONDARYNAMENODE,
	@XmlEnumValue("HDFS.FAILOVERCONTROLLER")	HDFS_FAILOVERCONTROLLER,
	@XmlEnumValue("HDFS.JOURNALNODE")	HDFS_JOURNALNODE,
	@XmlEnumValue("HDFS.HTTPFS")	HDFS_HTTPFS,
	@XmlEnumValue("HDFS.DATANODE")	HDFS_DATANODE,
	@XmlEnumValue("HDFS.NFSGATEWAY")	HDFS_NFSGATEWAY,
	@XmlEnumValue("HDFS.CLIENT")	HDFS_CLIENT,

	// YARN
	@XmlEnumValue("YARN.JOBHISTORY") YARN_JOBHISTORY,
	@XmlEnumValue("YARN.RESOURCEMANAGER") YARN_RESOURCEMANAGER,
	@XmlEnumValue("YARN.NODEMANAGER") YARN_NODEMANAGER,
	@XmlEnumValue("YARN.CLIENT")	YARN_CLIENT,

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
	@XmlEnumValue("ZOOKEEPER.SERVER") ZOOKEEPER_SERVER,
	@XmlEnumValue("ZOOKEEPER.CLIENT") ZOOKEEPER_CLIENT,

	// HBase
	@XmlEnumValue("HBASE.REGIONSERVER") HBASE_REGIONSERVER,
	@XmlEnumValue("HBASE.MASTER") HBASE_MASTER,
	@XmlEnumValue("HBASE.THRIFTSERVER") HBASE_THRIFTSERVER,
	@XmlEnumValue("HBASE.RESTSERVER") HBASE_RESTSERVER,
	@XmlEnumValue("HBASE.CLIENT")	HBASE_CLIENT,

	// Hive
	@XmlEnumValue("HIVE.METASTORE")	HIVE_METASTORE,
	@XmlEnumValue("HIVE.SERVER2")	HIVE_SERVER2,
	@XmlEnumValue("HIVE.WEBHCAT")	HIVE_WEBHCAT,
	@XmlEnumValue("HIVE.CLIENT")	HIVE_CLIENT,

	// IMPALA
	@XmlEnumValue("IMPALA.STATESTORE") IMPALA_STATESTORE,
	@XmlEnumValue("IMPALA.IMPALAD") IMPALA_IMPALAD,
	@XmlEnumValue("IMPALA.CATALOGSERVER") IMPALA_CATALOGSERVER,
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
	@XmlEnumValue("CLIENT") CLIENT;

	boolean isServiceLevel() {
		return this.ordinal() < HDFS_NAMENODE.ordinal();
	}
}
