package com.egnore.cluster.model.conf;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.apache.hadoop.fs.Path;

import com.egnore.cluster.model.Cluster;
import com.egnore.cluster.model.ClusterScanner;
import com.egnore.cluster.model.Group;
import com.egnore.cluster.model.Instance;
import com.egnore.cluster.model.Role;
import com.egnore.cluster.model.RoleType;
import com.egnore.cluster.model.Service;
import com.egnore.cluster.model.ServiceType;
import com.egnore.common.Dictionary;
import com.egnore.common.StringPair;
import com.egnore.common.StringPairs;
import com.egnore.common.model.conf.ConfigurableTreeNode;
import com.egnore.common.model.conf.SettingManager;
import com.egnore.common.model.conf.TreeScanner;
import com.egnore.hadoop.conf.jaxb.Configuration;
import com.egnore.hadoop.conf.jaxb.Configurations;


public class HadoopConfigManager extends SettingManager<HadoopConfigDescription> {

	protected List<Pattern> ignorePatterns = new ArrayList<Pattern>();

	public static HadoopConfigManager getInstance() {
		HadoopConfigManager sm = new HadoopConfigManager();
		try {
			sm.init();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
		return sm;
	}

	public void init() throws FileNotFoundException, JAXBException {
		Configurations configList = Configurations.load();
		List<HadoopConfigDescription> list = new ArrayList<HadoopConfigDescription>();
		for (Configuration c : configList.getList()) {
			RoleType rt = RoleType.fromScopeType(c.getScope());
			ServiceType st = (rt == null) ? ServiceType.fromScopeType(c.getScope()): rt.getServiceType();
			HadoopConfigDescription cd = new HadoopConfigDescription(c.getName(), c.getDefaultValue(), st, rt);
			list.add(cd);
		}
		dict = new Dictionary<HadoopConfigDescription>(list);
		addDeprecatedKeys();
		addIgnorePatterns();
	}

	private void addIgnorePatterns() {
		///< Skip HA settings
		addIgnorePatthern("yarn.resourcemanager.*");
		addIgnorePatthern("dfs.ha.namenodes.*");
		addIgnorePatthern("dfs.namenode.*");	
		addIgnorePatthern("dfs.ha.automatic-failover.enabled.*");
	}
	public void addIgnorePatthern(String regex) {
		ignorePatterns.add(Pattern.compile(regex));
	}

	public boolean isIgnored(String s) {
		for (Pattern p : ignorePatterns) {
			if (p.matcher(s).matches())
				return true;
		}
		return false;
	}

	public void loadXMLConfig(String file, ConfigurableTreeNode context) {
		org.apache.hadoop.conf.Configuration c = new org.apache.hadoop.conf.Configuration(false);
		c.addResource(new Path(file));
		for (Entry<String, String> pair : c) {
			if (!isIgnored(pair.getKey()))
				context.addSetting(pair.getKey(), pair.getValue());
		}
	}

	public class OverrideInfo {
		String key;
		String oldValue;
		String newValue;
		ConfigurableTreeNode node;
	}

	public boolean validateOverrideInfo(Cluster root) {
		final List<OverrideInfo> ret = new ArrayList<OverrideInfo>();
		new TreeScanner(root) {
			public void process(ConfigurableTreeNode i) {
				for (StringPair s : i.getLocalSettings()) {
					StringPair a = i.getAncestorSetting(s.getName());
					if ((a != null) && (!s.getValue().equals(a.getValue()))) {
						OverrideInfo o = new OverrideInfo();
						o.key = s.getName();
						o.oldValue = a.getValue();
						o.newValue = s.getValue();
						o.node = i;
						ret.add(o);
					}
				}
			}

		}.execute();
		if (!ret.isEmpty()) {
			for (OverrideInfo o : ret) {
				System.err.println(o.key + "is override by " + o.node.getId() + "(" + o.oldValue + "->" + o.newValue + ")");
			}
			return false;
		}
		return true;
	}

	private class validateCrossScopeInfoScanner extends ClusterScanner {
		protected validateCrossScopeInfoScanner(Cluster cluster) {
			super(cluster);
			m = (HadoopConfigManager) root.getSettingManager();
		}
		HadoopConfigManager m;
		public boolean result = true;

		@Override
		public void visitService(Service s) {
			for (StringPair sp : s.getLocalSettings()) {
				if ((m.get(sp.getName()).service != s.getType())
						|| (m.get(sp.getName()).role != null)) {
					System.err.println(sp + " is wrong defined.");
					result = false;
				}
			}
		}

		@Override
		public void visitRole(Role r) {
			for (StringPair sp : r.getLocalSettings()) {
				if ((m.get(sp.getName()).service != r.getType().getServiceType())
						|| (m.get(sp.getName()).role != r.getType())) {
					System.err.println(sp + " is wrong defined.");
					result = false;
				}
			}
		}
		
		@Override
		public void visitGroup(Group g) {
			visitRole(g.getRole());
		}
		@Override
		public void visitInstance(Instance i) {
			visitRole(i.getRole());
		}
	};

	public boolean validateCrossScopeInfo(Cluster root) {
		validateCrossScopeInfoScanner scanner = new validateCrossScopeInfoScanner(root);
		scanner.execute();
		return scanner.result;
	}

	public boolean validateConfiguration(Cluster root) {
		return (validateOverrideInfo(root)
				&& validateCrossScopeInfo(root)
				);
	}

	public void FlowDownConfig(Cluster root) {
		for (ConfigurableTreeNode n : root.getServices()) {
			Service s = (Service) n;
			StringPairs can = new StringPairs();
			for (StringPair sp : s.getLocalSettings()) {
				can.add(sp);
			}

			for (StringPair sp : can) {
				HadoopConfigDescription h = this.get(sp.getName());
				if (h.role != null) {
					s.getRole(h.role).addSetting(sp);
					s.removeSetting(sp);
				}
			}
		}
	}

	@Override
	public HadoopConfigDescription newItem(String key, ConfigurableTreeNode context) {
		return (HadoopConfigDescription) context.createSettingDescription(key);
	}

	public void addDeprecatedKey(String oldName, String newName) {
		dict.addDeprecatedKey(oldName, newName);
	}

	/**
	 * TODO: remove after configuration 
	 */
	private void addDeprecatedKeys() {
		///< Import from:
		///<	http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/DeprecatedProperties.html
		///< Apache Hadoop 2.7.2
		addDeprecatedKey("create.empty.dir.if.nonexist","mapreduce.jobcontrol.createdir.ifnotexist");
		addDeprecatedKey("dfs.access.time.precision","dfs.namenode.accesstime.precision");
		addDeprecatedKey("dfs.backup.address","dfs.namenode.backup.address");
		addDeprecatedKey("dfs.backup.http.address","dfs.namenode.backup.http-address");
		addDeprecatedKey("dfs.balance.bandwidthPerSec","dfs.datanode.balance.bandwidthPerSec");
		addDeprecatedKey("dfs.block.size","dfs.blocksize");
		addDeprecatedKey("dfs.data.dir","dfs.datanode.data.dir");
		addDeprecatedKey("dfs.datanode.max.xcievers","dfs.datanode.max.transfer.threads");
		addDeprecatedKey("dfs.df.interval","fs.df.interval");
		addDeprecatedKey("dfs.federation.nameservice.id","dfs.nameservice.id");
		addDeprecatedKey("dfs.federation.nameservices","dfs.nameservices");
		addDeprecatedKey("dfs.http.address","dfs.namenode.http-address");
		addDeprecatedKey("dfs.https.address","dfs.namenode.https-address");
		addDeprecatedKey("dfs.https.client.keystore.resource","dfs.client.https.keystore.resource");
		addDeprecatedKey("dfs.https.need.client.auth","dfs.client.https.need-auth");
		addDeprecatedKey("dfs.max.objects","dfs.namenode.max.objects");
		addDeprecatedKey("dfs.max-repl-streams","dfs.namenode.replication.max-streams");
		addDeprecatedKey("dfs.name.dir","dfs.namenode.name.dir");
		addDeprecatedKey("dfs.name.dir.restore","dfs.namenode.name.dir.restore");
		addDeprecatedKey("dfs.name.edits.dir","dfs.namenode.edits.dir");
		addDeprecatedKey("dfs.permissions","dfs.permissions.enabled");
		addDeprecatedKey("dfs.permissions.supergroup","dfs.permissions.superusergroup");
		addDeprecatedKey("dfs.read.prefetch.size","dfs.client.read.prefetch.size");
		addDeprecatedKey("dfs.replication.considerLoad","dfs.namenode.replication.considerLoad");
		addDeprecatedKey("dfs.replication.interval","dfs.namenode.replication.interval");
		addDeprecatedKey("dfs.replication.min","dfs.namenode.replication.min");
		addDeprecatedKey("dfs.replication.pending.timeout.sec","dfs.namenode.replication.pending.timeout-sec");
		addDeprecatedKey("dfs.safemode.extension","dfs.namenode.safemode.extension");
		addDeprecatedKey("dfs.safemode.threshold.pct","dfs.namenode.safemode.threshold-pct");
		addDeprecatedKey("dfs.secondary.http.address","dfs.namenode.secondary.http-address");
		addDeprecatedKey("dfs.socket.timeout","dfs.client.socket-timeout");
		addDeprecatedKey("dfs.umaskmode","fs.permissions.umask-mode");
		addDeprecatedKey("dfs.write.packet.size","dfs.client-write-packet-size");
		addDeprecatedKey("fs.checkpoint.dir","dfs.namenode.checkpoint.dir");
		addDeprecatedKey("fs.checkpoint.edits.dir","dfs.namenode.checkpoint.edits.dir");
		addDeprecatedKey("fs.checkpoint.period","dfs.namenode.checkpoint.period");
		addDeprecatedKey("fs.default.name","fs.defaultFS");
		addDeprecatedKey("hadoop.configured.node.mapping","net.topology.configured.node.mapping");
		addDeprecatedKey("hadoop.job.history.location","mapreduce.jobtracker.jobhistory.location");
		addDeprecatedKey("hadoop.native.lib","io.native.lib.available");
		addDeprecatedKey("hadoop.net.static.resolutions","mapreduce.tasktracker.net.static.resolutions");
		addDeprecatedKey("hadoop.pipes.command-file.keep","mapreduce.pipes.commandfile.preserve");
		addDeprecatedKey("hadoop.pipes.executable.interpretor","mapreduce.pipes.executable.interpretor");
		addDeprecatedKey("hadoop.pipes.executable","mapreduce.pipes.executable");
		addDeprecatedKey("hadoop.pipes.java.mapper","mapreduce.pipes.isjavamapper");
		addDeprecatedKey("hadoop.pipes.java.recordreader","mapreduce.pipes.isjavarecordreader");
		addDeprecatedKey("hadoop.pipes.java.recordwriter","mapreduce.pipes.isjavarecordwriter");
		addDeprecatedKey("hadoop.pipes.java.reducer","mapreduce.pipes.isjavareducer");
		addDeprecatedKey("hadoop.pipes.partitioner","mapreduce.pipes.partitioner");
		addDeprecatedKey("heartbeat.recheck.interval","dfs.namenode.heartbeat.recheck-interval");
		addDeprecatedKey("io.bytes.per.checksum","dfs.bytes-per-checksum");
		addDeprecatedKey("io.sort.factor","mapreduce.task.io.sort.factor");
		addDeprecatedKey("io.sort.mb","mapreduce.task.io.sort.mb");
		addDeprecatedKey("io.sort.spill.percent","mapreduce.map.sort.spill.percent");
		addDeprecatedKey("jobclient.completion.poll.interval","mapreduce.client.completion.pollinterval");
		addDeprecatedKey("jobclient.output.filter","mapreduce.client.output.filter");
		addDeprecatedKey("jobclient.progress.monitor.poll.interval","mapreduce.client.progressmonitor.pollinterval");
		addDeprecatedKey("job.end.notification.url","mapreduce.job.end-notification.url");
		addDeprecatedKey("job.end.retry.attempts","mapreduce.job.end-notification.retry.attempts");
		addDeprecatedKey("job.end.retry.interval","mapreduce.job.end-notification.retry.interval");
		addDeprecatedKey("job.local.dir","mapreduce.job.local.dir");
		addDeprecatedKey("keep.failed.task.files","mapreduce.task.files.preserve.failedtasks");
		addDeprecatedKey("keep.task.files.pattern","mapreduce.task.files.preserve.filepattern");
		addDeprecatedKey("key.value.separator.in.input.line","mapreduce.input.keyvaluelinerecordreader.key.value.separator");
		addDeprecatedKey("local.cache.size","mapreduce.tasktracker.cache.local.size");
		addDeprecatedKey("map.input.file","mapreduce.map.input.file");
		addDeprecatedKey("map.input.length","mapreduce.map.input.length");
		addDeprecatedKey("map.input.start","mapreduce.map.input.start");
		addDeprecatedKey("map.output.key.field.separator","mapreduce.map.output.key.field.separator");
		addDeprecatedKey("map.output.key.value.fields.spec","mapreduce.fieldsel.map.output.key.value.fields.spec");
		addDeprecatedKey("mapred.acls.enabled","mapreduce.cluster.acls.enabled");
		addDeprecatedKey("mapred.binary.partitioner.left.offset","mapreduce.partition.binarypartitioner.left.offset");
		addDeprecatedKey("mapred.binary.partitioner.right.offset","mapreduce.partition.binarypartitioner.right.offset");
		addDeprecatedKey("mapred.cache.archives","mapreduce.job.cache.archives");
		addDeprecatedKey("mapred.cache.archives.timestamps","mapreduce.job.cache.archives.timestamps");
		addDeprecatedKey("mapred.cache.files","mapreduce.job.cache.files");
		addDeprecatedKey("mapred.cache.files.timestamps","mapreduce.job.cache.files.timestamps");
		addDeprecatedKey("mapred.cache.localArchives","mapreduce.job.cache.local.archives");
		addDeprecatedKey("mapred.cache.localFiles","mapreduce.job.cache.local.files");
		addDeprecatedKey("mapred.child.tmp","mapreduce.task.tmp.dir");
		addDeprecatedKey("mapred.cluster.average.blacklist.threshold","mapreduce.jobtracker.blacklist.average.threshold");
		addDeprecatedKey("mapred.cluster.map.memory.mb","mapreduce.cluster.mapmemory.mb");
		addDeprecatedKey("mapred.cluster.max.map.memory.mb","mapreduce.jobtracker.maxmapmemory.mb");
		addDeprecatedKey("mapred.cluster.max.reduce.memory.mb","mapreduce.jobtracker.maxreducememory.mb");
		addDeprecatedKey("mapred.cluster.reduce.memory.mb","mapreduce.cluster.reducememory.mb");
		addDeprecatedKey("mapred.committer.job.setup.cleanup.needed","mapreduce.job.committer.setup.cleanup.needed");
		addDeprecatedKey("mapred.compress.map.output","mapreduce.map.output.compress");
		addDeprecatedKey("mapred.data.field.separator","mapreduce.fieldsel.data.field.separator");
		addDeprecatedKey("mapred.debug.out.lines","mapreduce.task.debugout.lines");
		addDeprecatedKey("mapred.healthChecker.interval","mapreduce.tasktracker.healthchecker.interval");
		addDeprecatedKey("mapred.healthChecker.script.args","mapreduce.tasktracker.healthchecker.script.args");
		addDeprecatedKey("mapred.healthChecker.script.path","mapreduce.tasktracker.healthchecker.script.path");
		addDeprecatedKey("mapred.healthChecker.script.timeout","mapreduce.tasktracker.healthchecker.script.timeout");
		addDeprecatedKey("mapred.heartbeats.in.second","mapreduce.jobtracker.heartbeats.in.second");
		addDeprecatedKey("mapred.hosts.exclude","mapreduce.jobtracker.hosts.exclude.filename");
		addDeprecatedKey("mapred.hosts","mapreduce.jobtracker.hosts.filename");
		addDeprecatedKey("mapred.inmem.merge.threshold","mapreduce.reduce.merge.inmem.threshold");
		addDeprecatedKey("mapred.input.dir.formats","mapreduce.input.multipleinputs.dir.formats");
		addDeprecatedKey("mapred.input.dir.mappers","mapreduce.input.multipleinputs.dir.mappers");
		addDeprecatedKey("mapred.input.dir","mapreduce.input.fileinputformat.inputdir");
		addDeprecatedKey("mapred.input.pathFilter.class","mapreduce.input.pathFilter.class");
		addDeprecatedKey("mapred.jar","mapreduce.job.jar");
		addDeprecatedKey("mapred.job.classpath.archives","mapreduce.job.classpath.archives");
		addDeprecatedKey("mapred.job.classpath.files","mapreduce.job.classpath.files");
		addDeprecatedKey("mapred.job.id","mapreduce.job.id");
		addDeprecatedKey("mapred.jobinit.threads","mapreduce.jobtracker.jobinit.threads");
		addDeprecatedKey("mapred.job.map.memory.mb","mapreduce.map.memory.mb");
		addDeprecatedKey("mapred.job.name","mapreduce.job.name");
		addDeprecatedKey("mapred.job.priority","mapreduce.job.priority");
		addDeprecatedKey("mapred.job.queue.name","mapreduce.job.queuename");
		addDeprecatedKey("mapred.job.reduce.input.buffer.percent","mapreduce.reduce.input.buffer.percent");
		addDeprecatedKey("mapred.job.reduce.markreset.buffer.percent","mapreduce.reduce.markreset.buffer.percent");
		addDeprecatedKey("mapred.job.reduce.memory.mb","mapreduce.reduce.memory.mb");
		addDeprecatedKey("mapred.job.reduce.total.mem.bytes","mapreduce.reduce.memory.totalbytes");
		addDeprecatedKey("mapred.job.reuse.jvm.num.tasks","mapreduce.job.jvm.numtasks");
		addDeprecatedKey("mapred.job.shuffle.input.buffer.percent","mapreduce.reduce.shuffle.input.buffer.percent");
		addDeprecatedKey("mapred.job.shuffle.merge.percent","mapreduce.reduce.shuffle.merge.percent");
		addDeprecatedKey("mapred.job.tracker.handler.count","mapreduce.jobtracker.handler.count");
		addDeprecatedKey("mapred.job.tracker.history.completed.location","mapreduce.jobtracker.jobhistory.completed.location");
		addDeprecatedKey("mapred.job.tracker.http.address","mapreduce.jobtracker.http.address");
		addDeprecatedKey("mapred.jobtracker.instrumentation","mapreduce.jobtracker.instrumentation");
		addDeprecatedKey("mapred.jobtracker.job.history.block.size","mapreduce.jobtracker.jobhistory.block.size");
		addDeprecatedKey("mapred.job.tracker.jobhistory.lru.cache.size","mapreduce.jobtracker.jobhistory.lru.cache.size");
		addDeprecatedKey("mapred.job.tracker","mapreduce.jobtracker.address");
		addDeprecatedKey("mapred.jobtracker.maxtasks.per.job","mapreduce.jobtracker.maxtasks.perjob");
		addDeprecatedKey("mapred.job.tracker.persist.jobstatus.active","mapreduce.jobtracker.persist.jobstatus.active");
		addDeprecatedKey("mapred.job.tracker.persist.jobstatus.dir","mapreduce.jobtracker.persist.jobstatus.dir");
		addDeprecatedKey("mapred.job.tracker.persist.jobstatus.hours","mapreduce.jobtracker.persist.jobstatus.hours");
		addDeprecatedKey("mapred.jobtracker.restart.recover","mapreduce.jobtracker.restart.recover");
		addDeprecatedKey("mapred.job.tracker.retiredjobs.cache.size","mapreduce.jobtracker.retiredjobs.cache.size");
		addDeprecatedKey("mapred.job.tracker.retire.jobs","mapreduce.jobtracker.retirejobs");
		addDeprecatedKey("mapred.jobtracker.taskalloc.capacitypad","mapreduce.jobtracker.taskscheduler.taskalloc.capacitypad");
		addDeprecatedKey("mapred.jobtracker.taskScheduler","mapreduce.jobtracker.taskscheduler");
		addDeprecatedKey("mapred.jobtracker.taskScheduler.maxRunningTasksPerJob","mapreduce.jobtracker.taskscheduler.maxrunningtasks.perjob");
		addDeprecatedKey("mapred.join.expr","mapreduce.join.expr");
		addDeprecatedKey("mapred.join.keycomparator","mapreduce.join.keycomparator");
		addDeprecatedKey("mapred.lazy.output.format","mapreduce.output.lazyoutputformat.outputformat");
		addDeprecatedKey("mapred.line.input.format.linespermap","mapreduce.input.lineinputformat.linespermap");
		addDeprecatedKey("mapred.linerecordreader.maxlength","mapreduce.input.linerecordreader.line.maxlength");
		addDeprecatedKey("mapred.local.dir","mapreduce.cluster.local.dir");
		addDeprecatedKey("mapred.local.dir.minspacekill","mapreduce.tasktracker.local.dir.minspacekill");
		addDeprecatedKey("mapred.local.dir.minspacestart","mapreduce.tasktracker.local.dir.minspacestart");
		addDeprecatedKey("mapred.map.child.env","mapreduce.map.env");
		addDeprecatedKey("mapred.map.child.java.opts","mapreduce.map.java.opts");
		addDeprecatedKey("mapred.map.child.log.level","mapreduce.map.log.level");
		addDeprecatedKey("mapred.map.max.attempts","mapreduce.map.maxattempts");
		addDeprecatedKey("mapred.map.output.compression.codec","mapreduce.map.output.compress.codec");
		addDeprecatedKey("mapred.mapoutput.key.class","mapreduce.map.output.key.class");
		addDeprecatedKey("mapred.mapoutput.value.class","mapreduce.map.output.value.class");
		addDeprecatedKey("mapred.mapper.regex.group","mapreduce.mapper.regexmapper..group");
		addDeprecatedKey("mapred.mapper.regex","mapreduce.mapper.regex");
		addDeprecatedKey("mapred.map.task.debug.script","mapreduce.map.debug.script");
		addDeprecatedKey("mapred.map.tasks","mapreduce.job.maps");
		addDeprecatedKey("mapred.map.tasks.speculative.execution","mapreduce.map.speculative");
		addDeprecatedKey("mapred.max.map.failures.percent","mapreduce.map.failures.maxpercent");
		addDeprecatedKey("mapred.max.reduce.failures.percent","mapreduce.reduce.failures.maxpercent");
		addDeprecatedKey("mapred.max.split.size","mapreduce.input.fileinputformat.split.maxsize");
		addDeprecatedKey("mapred.max.tracker.blacklists","mapreduce.jobtracker.tasktracker.maxblacklists");
		addDeprecatedKey("mapred.max.tracker.failures","mapreduce.job.maxtaskfailures.per.tracker");
		addDeprecatedKey("mapred.merge.recordsBeforeProgress","mapreduce.task.merge.progress.records");
		addDeprecatedKey("mapred.min.split.size","mapreduce.input.fileinputformat.split.minsize");
		addDeprecatedKey("mapred.min.split.size.per.node","mapreduce.input.fileinputformat.split.minsize.per.node");
		addDeprecatedKey("mapred.min.split.size.per.rack","mapreduce.input.fileinputformat.split.minsize.per.rack");
		addDeprecatedKey("mapred.output.compression.codec","mapreduce.output.fileoutputformat.compress.codec");
		addDeprecatedKey("mapred.output.compression.type","mapreduce.output.fileoutputformat.compress.type");
		addDeprecatedKey("mapred.output.compress","mapreduce.output.fileoutputformat.compress");
		addDeprecatedKey("mapred.output.dir","mapreduce.output.fileoutputformat.outputdir");
		addDeprecatedKey("mapred.output.key.class","mapreduce.job.output.key.class");
		addDeprecatedKey("mapred.output.key.comparator.class","mapreduce.job.output.key.comparator.class");
		addDeprecatedKey("mapred.output.value.class","mapreduce.job.output.value.class");
		addDeprecatedKey("mapred.output.value.groupfn.class","mapreduce.job.output.group.comparator.class");
		addDeprecatedKey("mapred.permissions.supergroup","mapreduce.cluster.permissions.supergroup");
		addDeprecatedKey("mapred.pipes.user.inputformat","mapreduce.pipes.inputformat");
		addDeprecatedKey("mapred.reduce.child.env","mapreduce.reduce.env");
		addDeprecatedKey("mapred.reduce.child.java.opts","mapreduce.reduce.java.opts");
		addDeprecatedKey("mapred.reduce.child.log.level","mapreduce.reduce.log.level");
		addDeprecatedKey("mapred.reduce.max.attempts","mapreduce.reduce.maxattempts");
		addDeprecatedKey("mapred.reduce.parallel.copies","mapreduce.reduce.shuffle.parallelcopies");
		addDeprecatedKey("mapred.reduce.slowstart.completed.maps","mapreduce.job.reduce.slowstart.completedmaps");
		addDeprecatedKey("mapred.reduce.task.debug.script","mapreduce.reduce.debug.script");
		addDeprecatedKey("mapred.reduce.tasks","mapreduce.job.reduces");
		addDeprecatedKey("mapred.reduce.tasks.speculative.execution","mapreduce.reduce.speculative");
		addDeprecatedKey("mapred.seqbinary.output.key.class","mapreduce.output.seqbinaryoutputformat.key.class");
		addDeprecatedKey("mapred.seqbinary.output.value.class","mapreduce.output.seqbinaryoutputformat.value.class");
		addDeprecatedKey("mapred.shuffle.connect.timeout","mapreduce.reduce.shuffle.connect.timeout");
		addDeprecatedKey("mapred.shuffle.read.timeout","mapreduce.reduce.shuffle.read.timeout");
		addDeprecatedKey("mapred.skip.attempts.to.start.skipping","mapreduce.task.skip.start.attempts");
		addDeprecatedKey("mapred.skip.map.auto.incr.proc.count","mapreduce.map.skip.proc-count.auto-incr");
		addDeprecatedKey("mapred.skip.map.max.skip.records","mapreduce.map.skip.maxrecords");
		addDeprecatedKey("mapred.skip.on","mapreduce.job.skiprecords");
		addDeprecatedKey("mapred.skip.out.dir","mapreduce.job.skip.outdir");
		addDeprecatedKey("mapred.skip.reduce.auto.incr.proc.count","mapreduce.reduce.skip.proc-count.auto-incr");
		addDeprecatedKey("mapred.skip.reduce.max.skip.groups","mapreduce.reduce.skip.maxgroups");
		addDeprecatedKey("mapred.speculative.execution.slowNodeThreshold","mapreduce.job.speculative.slownodethreshold");
		addDeprecatedKey("mapred.speculative.execution.slowTaskThreshold","mapreduce.job.speculative.slowtaskthreshold");
		addDeprecatedKey("mapred.speculative.execution.speculativeCap","mapreduce.job.speculative.speculativecap");
		addDeprecatedKey("mapred.submit.replication","mapreduce.client.submit.file.replication");
		addDeprecatedKey("mapred.system.dir","mapreduce.jobtracker.system.dir");
		addDeprecatedKey("mapred.task.cache.levels","mapreduce.jobtracker.taskcache.levels");
		addDeprecatedKey("mapred.task.id","mapreduce.task.attempt.id");
		addDeprecatedKey("mapred.task.is.map","mapreduce.task.ismap");
		addDeprecatedKey("mapred.task.partition","mapreduce.task.partition");
		addDeprecatedKey("mapred.task.profile","mapreduce.task.profile");
		addDeprecatedKey("mapred.task.profile.maps","mapreduce.task.profile.maps");
		addDeprecatedKey("mapred.task.profile.params","mapreduce.task.profile.params");
		addDeprecatedKey("mapred.task.profile.reduces","mapreduce.task.profile.reduces");
		addDeprecatedKey("mapred.task.timeout","mapreduce.task.timeout");
		addDeprecatedKey("mapred.tasktracker.dns.interface","mapreduce.tasktracker.dns.interface");
		addDeprecatedKey("mapred.tasktracker.dns.nameserver","mapreduce.tasktracker.dns.nameserver");
		addDeprecatedKey("mapred.tasktracker.events.batchsize","mapreduce.tasktracker.events.batchsize");
		addDeprecatedKey("mapred.tasktracker.expiry.interval","mapreduce.jobtracker.expire.trackers.interval");
		addDeprecatedKey("mapred.task.tracker.http.address","mapreduce.tasktracker.http.address");
		addDeprecatedKey("mapred.tasktracker.indexcache.mb","mapreduce.tasktracker.indexcache.mb");
		addDeprecatedKey("mapred.tasktracker.instrumentation","mapreduce.tasktracker.instrumentation");
		addDeprecatedKey("mapred.tasktracker.map.tasks.maximum","mapreduce.tasktracker.map.tasks.maximum");
		addDeprecatedKey("mapred.tasktracker.memory_calculator_plugin","mapreduce.tasktracker.resourcecalculatorplugin");
		addDeprecatedKey("mapred.tasktracker.memorycalculatorplugin","mapreduce.tasktracker.resourcecalculatorplugin");
		addDeprecatedKey("mapred.tasktracker.reduce.tasks.maximum","mapreduce.tasktracker.reduce.tasks.maximum");
		addDeprecatedKey("mapred.task.tracker.report.address","mapreduce.tasktracker.report.address");
		addDeprecatedKey("mapred.task.tracker.task-controller","mapreduce.tasktracker.taskcontroller");
		addDeprecatedKey("mapred.tasktracker.taskmemorymanager.monitoring-interval","mapreduce.tasktracker.taskmemorymanager.monitoringinterval");
		addDeprecatedKey("mapred.tasktracker.tasks.sleeptime-before-sigkill","mapreduce.tasktracker.tasks.sleeptimebeforesigkill");
		addDeprecatedKey("mapred.temp.dir","mapreduce.cluster.temp.dir");
		addDeprecatedKey("mapred.text.key.comparator.options","mapreduce.partition.keycomparator.options");
		addDeprecatedKey("mapred.text.key.partitioner.options","mapreduce.partition.keypartitioner.options");
		addDeprecatedKey("mapred.textoutputformat.separator","mapreduce.output.textoutputformat.separator");
		addDeprecatedKey("mapred.tip.id","mapreduce.task.id");
		addDeprecatedKey("mapreduce.combine.class","mapreduce.job.combine.class");
		addDeprecatedKey("mapreduce.inputformat.class","mapreduce.job.inputformat.class");
		addDeprecatedKey("mapreduce.job.counters.limit","mapreduce.job.counters.max");
		addDeprecatedKey("mapreduce.jobtracker.permissions.supergroup","mapreduce.cluster.permissions.supergroup");
		addDeprecatedKey("mapreduce.map.class","mapreduce.job.map.class");
		addDeprecatedKey("mapreduce.outputformat.class","mapreduce.job.outputformat.class");
		addDeprecatedKey("mapreduce.partitioner.class","mapreduce.job.partitioner.class");
		addDeprecatedKey("mapreduce.reduce.class","mapreduce.job.reduce.class");
		addDeprecatedKey("mapred.used.genericoptionsparser","mapreduce.client.genericoptionsparser.used");
		addDeprecatedKey("mapred.userlog.limit.kb","mapreduce.task.userlog.limit.kb");
		addDeprecatedKey("mapred.userlog.retain.hours","mapreduce.job.userlog.retain.hours");
		addDeprecatedKey("mapred.working.dir","mapreduce.job.working.dir");
		addDeprecatedKey("mapred.work.output.dir","mapreduce.task.output.dir");
		addDeprecatedKey("min.num.spills.for.combine","mapreduce.map.combine.minspills");
		addDeprecatedKey("reduce.output.key.value.fields.spec","mapreduce.fieldsel.reduce.output.key.value.fields.spec");
		addDeprecatedKey("security.job.submission.protocol.acl","security.job.client.protocol.acl");
		addDeprecatedKey("security.task.umbilical.protocol.acl","security.job.task.protocol.acl");
		addDeprecatedKey("sequencefile.filter.class","mapreduce.input.sequencefileinputfilter.class");
		addDeprecatedKey("sequencefile.filter.frequency","mapreduce.input.sequencefileinputfilter.frequency");
		addDeprecatedKey("sequencefile.filter.regex","mapreduce.input.sequencefileinputfilter.regex");
		addDeprecatedKey("session.id","dfs.metrics.session-id");
		addDeprecatedKey("slave.host.name","dfs.datanode.hostname");
		addDeprecatedKey("slave.host.name","mapreduce.tasktracker.host.name");
		addDeprecatedKey("tasktracker.contention.tracking","mapreduce.tasktracker.contention.tracking");
		addDeprecatedKey("tasktracker.http.threads","mapreduce.tasktracker.http.threads");
		addDeprecatedKey("topology.node.switch.mapping.impl","net.topology.node.switch.mapping.impl");
		addDeprecatedKey("topology.script.file.name","net.topology.script.file.name");
		addDeprecatedKey("topology.script.number.args","net.topology.script.number.args");
		addDeprecatedKey("user.name","mapreduce.job.user.name");
		addDeprecatedKey("webinterface.private.actions","mapreduce.jobtracker.webinterface.trusted");
		addDeprecatedKey("yarn.app.mapreduce.yarn.app.mapreduce.client-am.ipc.max-retries-on-timeouts","yarn.app.mapreduce.client-am.ipc.max-retries-on-timeouts");
	}
}
