package com.egnore.common.model.conf;

public class TreeScanner {

	protected ConfigurableTreeNode root;

	protected TreeScanner(ConfigurableTreeNode cluster) {
		this.root = cluster;
	}

	public void setRoot(ConfigurableTreeNode cluster) {
		this.root = cluster;
	}

	public void execute() {
		execute(root);
	}

	public void execute(ConfigurableTreeNode node) {
		process(node);
		if (node.getChildren() == null)
			return;
		for (ConfigurableTreeNode n : node.getChildren()) {
			execute(n);
		}
	}

	public void process(ConfigurableTreeNode i) {
		
	}
}
