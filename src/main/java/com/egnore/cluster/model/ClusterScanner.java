package com.egnore.cluster.model;

import com.egnore.common.model.conf.ConfigurableTreeNode;

public class ClusterScanner {
	protected Cluster root;

	protected ClusterScanner(Cluster cluster) {
		this.root = cluster;
	}

	public void setCluster(Cluster cluster) {
		this.root = cluster;
	}

	public void execute() {
		for (ConfigurableTreeNode s : root.getChildren()) {
			if (s == null) continue;
			visitService((Service)s);
			for (ConfigurableTreeNode r : s.getChildren()) {
				if (r == null) continue;
				visitRole((Role)r);
				for (ConfigurableTreeNode g : r.getChildren()) {
					if (g == null) continue;
					visitGroup((Group)g);
					if (!g.hasChild()) continue;
					for (ConfigurableTreeNode i : g.getChildren()) {
						if (i == null) continue;
						visitInstance((Instance)i);
					}
				}
			}
		}
	}

	public void visitService(Service s) { }
	public void visitRole(Role r) { }
	public void visitGroup(Group g) { }
	public void visitInstance(Instance i) { }
}
