package com.egnore.cluster.model;

import com.egnore.common.model.conf.ConfigurableTreeNode;

public abstract class InstanceScanner {
	protected Cluster root;

	protected InstanceScanner(Cluster cluster) {
		this.root = cluster;
	}

	public void setCluster(Cluster cluster) {
		this.root = cluster;
	}

	public void execute() {
		for (ConfigurableTreeNode s : root.getChildren()) {
			if (s == null) continue;
			for (ConfigurableTreeNode r : s.getChildren()) {
				if (r == null) continue;
				for (ConfigurableTreeNode g : r.getChildren()) {
					if ((g == null) || !g.hasChild())continue;
					for (ConfigurableTreeNode i : g.getChildren()) {
						if (i == null) continue;
						process((Instance)i);
					}
				}
			}
		}
	}

	abstract public void process(Instance i);
}
