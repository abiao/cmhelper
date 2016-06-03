package com.egnore.common.model.conf;

import java.util.ArrayList;
import java.util.List;

public class ConfigurableTreeNode extends Configurable {

	protected ConfigurableTreeNode parent = null;
	protected List<ConfigurableTreeNode> children = null; ///< lazy: new ArrayList<ConfigurableNode>(); 

	public boolean hasLocalSetting(String name) {
		return conf.containsKey(name);
	}

	public boolean hasSetting(String name) {
		 return (conf.containsKey(name)) ? true : ((parent != null) ? parent.hasSetting(name) : false);
	}

	public Setting getLocalSetting(String name) {
		return conf.get(name);
	}

	public Setting getSetting(String name) {
		Setting s = conf.get(name);
		return (s != null) ? s : ((parent != null) ? parent.getSetting(name) : null);
	}

	public Settings getSettings() {
		return conf;
	}

	public boolean isLeafNode() {
		return children == null;
	}

	public List<ConfigurableTreeNode> getChildren() {
		return children;
	}
	
	public void addChild(ConfigurableTreeNode node) {
		if (node == null) return;
		
		if (node.parent != null) {
			node.parent.removeChild(node);
		}

		if (children == null) {
			children = new ArrayList<ConfigurableTreeNode>();  ///< Create lazily
		}
		children.add(node);
		node.parent = this;
	}

	public void removeChild(ConfigurableTreeNode node) {
		if (node == null) return;
		node.parent = null;
		if(children == null) return;
		children.remove(node);
	}
}
