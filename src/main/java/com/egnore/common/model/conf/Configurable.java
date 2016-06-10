package com.egnore.common.model.conf;

import java.io.PrintStream;
import java.io.Serializable;

import com.egnore.common.StringPair;
import com.egnore.common.StringPairs;

public class Configurable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6294541935079119926L;
	protected String id = "";
	protected boolean enabled;
	protected StringPairs conf = new StringPairs();

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	protected void addSetting(StringPair s) {
		conf.add(s);
	}
	
	public boolean hasSetting(String name) {
		 return conf.containsKey(name);
	}

	public StringPair getSetting(String name) {
		return conf.get(name);
	}

	public StringPairs getSettings() {
		return conf;
	}

	final static String DUMP_LEADING_STRING = "\t";

	public void dump(PrintStream ps) {
		dump(ps, "");
	}

	public void dump(PrintStream ps, String prefix) {
		dumpSelf(ps, prefix);
		dumpSettings(ps, prefix);
	}

	protected void dumpSelf(PrintStream ps, String prefix) {
		ps.println(prefix + dumpSelfToString());
	}

	protected void dumpSettings(PrintStream ps, String prefix) {
		for (StringPair s : conf) {
			ps.println(prefix + s.saveToString());
		}
	}

	/**
	 * Type for outerclass
	 * @param s
	 */
	protected void loadType(String s) {
		
	}
	
	public String getTypeString() {
		return this.getClass().getName();
	}

	///< Single Lined.
	protected String dumpToString() {
		return dumpSelfToString() + dumpSettingsToString();
	}

	protected String dumpSelfToString() {
		return (id == null) ? "" : id;
	}

	protected String dumpSettingsToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (StringPair s : conf) {
			sb.append(s.saveToString());
			sb.append(",");
		}
		sb.append("}");
		return sb.toString();
	}
}
