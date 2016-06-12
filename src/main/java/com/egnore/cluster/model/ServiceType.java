package com.egnore.cluster.model;

import javax.xml.bind.annotation.XmlEnumValue;

import com.egnore.hadoop.conf.jaxb.ScopeType;

///< CDH5
///< TODO: 为什么俺不直接使用ScopeType，为了保证ServiceType和RoleType的独立性。ScopeType只是作为编码存储用。
public enum ServiceType {
	///< Sorted by component dependency
	ZOOKEEPER,
	HDFS,
//	MAPREDUCE,
	HBASE,
	YARN,
	HIVE,
	IMPALA;
//	FLUME,
//	HUE,
//	OOZIE,
//	SOLR,
//	SQOOP,
//	KS_INDEXER,
//	SQOOP_CLIENT,
//	SENTRY,
//	ACCUMULO16,
//	KMS,
//	SPARK_ON_YARNZOOKEEPER

	static public ServiceType fromScopeType(ScopeType scope) {
		switch (scope) {
			case ZOOKEEPER: return ServiceType.ZOOKEEPER;
			case HDFS: 		return ServiceType.HDFS;
			case HBASE:		return ServiceType.HBASE;
			case YARN: 		return ServiceType.YARN;
			case HIVE: 		return ServiceType.HIVE;
			case IMPALA: 	return ServiceType.IMPALA;
			default:
				return null;
		}
	}

	public ScopeType toScopeType() {
		switch (this) {
			case ZOOKEEPER: return ScopeType.ZOOKEEPER;
			case HDFS: 		return ScopeType.HDFS;
			case HBASE: 	return ScopeType.HBASE;
			case YARN: 		return ScopeType.YARN;
			case HIVE: 		return ScopeType.HIVE;
			case IMPALA: 	return ScopeType.IMPALA;
			default:
				return null;
		}
	}

	static public ServiceType getXMLFileServiceType(String path) {
		ServiceType st = null;
		if ("hdfs-site.xml".equals(path)) {
			st = ServiceType.HDFS;
		} else if ("hbase-site.xml".equals(path)) {
			st = ServiceType.HBASE;
		} else if ("yarn-site.xml".equals(path)) {
			st = ServiceType.YARN;
		} else if ("mapred-site.xml".equals(path)) {
			st = ServiceType.YARN;
		}
		return st;
	}

}
