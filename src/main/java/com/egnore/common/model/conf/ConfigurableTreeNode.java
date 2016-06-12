package com.egnore.common.model.conf;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.egnore.common.StringPair;
import com.egnore.common.StringPairs;
import com.egnore.common.io.LinedFileReader;

public class ConfigurableTreeNode extends Configurable {

	protected ConfigurableTreeNode parent = null;
	protected List<ConfigurableTreeNode> children = null; ///< lazy new 

	public List<ConfigurableTreeNode> getChildren() {
		return children;
	}
	
	protected void testAndNewChildren() {
		if (children == null)
			children = new ArrayList<ConfigurableTreeNode>();
	}

	public List<ConfigurableTreeNode> getOrNewChildren() {
		testAndNewChildren();
		return children;
	}

	public boolean hasLocalSetting(String name) {
		return conf.containsKey(name);
	}

	public boolean hasSetting(String name) {
		 return (conf.containsKey(name)) ? true : ((parent != null) ? parent.hasSetting(name) : false);
	}

	public StringPair getLocalSetting(String name) {
		return conf.get(name);
	}

	public StringPair getAncestorSetting(String name) {
		return ((parent != null) ? parent.getSetting(name) : null);
	}

	public StringPair getSetting(String name) {
		StringPair s = getLocalSetting(name);
		return (s != null) ? s : getAncestorSetting(name);
	}

	public StringPairs getLocalSettings() {
		return conf;
	}

	public int settingLength() {
		return conf.size();
	}


	/**
	 * Tree
	 * 
	 */


	public boolean hasChild() {
		return (children != null) && (!children.isEmpty());
	}

	public int getChildNumber() {
		return (children == null) ? 0 : children.size();
	}

	public boolean isLeafNode() {
		return children == null;
	}

	public void addChild(ConfigurableTreeNode node) {
		if (node == null) return;
		
		if (node.parent != null) {
			node.parent.removeChild(node);
		}

		testAndNewChildren();  ///< Create lazily
		children.add(node);
		node.parent = this;
	}

	public void removeChild(ConfigurableTreeNode node) {
		if (node == null) return;
		node.parent = null;
		if(children == null) return;
		children.remove(node);
	}

	///< TODO: 
	public SettingManager getSettingManager() {
		return (parent == null) ? null : parent.getSettingManager();
	}
	
	public SettingDescription createSettingDescription(String key) {
		if (parent != null)
			return parent.createSettingDescription(key);
		else
			return new SettingDescription(key);
	}

	public void addSetting(StringPair s) {
		getSettingManager().getOrNew(s.getName(), this);
		super.addSetting(s);
	}

	@Override
	public void dump(PrintStream ps, String prefix) {
		super.dump(ps, prefix);
		if (children == null) return;
		for (ConfigurableTreeNode n : children) {
			n.dump(ps, prefix + DUMP_LEADING_STRING);
		}
	}

	///< Single Lined.
	/**
	 * Dummp format
	 * <tabs>....Type=id[SettingNumber,Childen Number]:OtherFields
	 * 		tabs: optional
	 * 		Type: optional, parsed by outer class.
	 * 		id: optional
	 * 		OtherFields: optional, parsed by outer class.
	 */
	@Override
	protected String dumpSelfToString() {
		String s = (id == null) ? "" : id;
		int cn = (children == null) ? 0 : children.size();
		return getTypeString() + "=" + s + "[" + conf.size() +"," + cn + "]";// + dumpSettingsToString();
	}

	protected void readSelfFromString(String s) {
		//id = s;
	}

	/*
	 * Only for loading.
	 */
	protected ConfigurableTreeNode newEmptyChild() {
		return null;
	}

	public void loadFromLinedStrings(LinedFileReader reader) throws IOException {
		//NAMENODE=[0][1]{} 
		String s = reader.next();
		System.out.print(s);
		readSelfFromString(s);
		int i1 = s.indexOf("=");
		int i2 = s.indexOf("[");
		int i3 = s.indexOf(",");
		int i4 = s.indexOf("]");
		
		String[] ss = s.split("=|[|]");
		System.out.print(ss);

		this.loadType(s.substring(0, i1));
		this.id = s.substring(i1 + 1, i2);
		int settingNumber  = Integer.parseInt(s.substring(i2 + 1, i3));
		int childrenNumber = Integer.parseInt(s.substring(i3 + 1, i4));

		for (int i = 0; i < settingNumber; i++) {
			StringPair set = new StringPair();
			set.loadFromString(reader.next());
			this.addSetting(set);
		}

		for (int i = 0; i < childrenNumber; i++) {
			ConfigurableTreeNode node = this.newEmptyChild();
			node.loadFromLinedStrings(reader);
			this.addChild(node);
		}
	}
}
