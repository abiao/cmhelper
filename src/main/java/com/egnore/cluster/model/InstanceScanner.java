package com.egnore.cluster.model;

public abstract class InstanceScanner {
	protected Cluster root;

	protected InstanceScanner(Cluster cluster) {
		this.root = cluster;
	}

	public void setCluster(Cluster cluster) {
		this.root = cluster;
	}

	public void execute() {
		for (Service s : root.getServiceList()) {
			if (s == null) continue;
			for (Role r : s.getRoles()) {
				if (r == null) continue;
				for (Group g : r.getGroups()) {
					if (g == null) continue;
					for (Instance i : g.getInstances()) {
						if (i == null) continue;
						process(i);
					}
				}
			}
		}
	}

	abstract public void process(Instance i);
}
