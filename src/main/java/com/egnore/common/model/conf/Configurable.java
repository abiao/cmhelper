package com.egnore.common.model.conf;

import java.io.PrintStream;
import java.io.Serializable;

public class Configurable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6294541935079119926L;
	protected String id = "";
	protected boolean enabled;
	protected Settings conf = new Settings();

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	protected void addSetting(Setting s) {
		conf.add(s);
	}
	
	public boolean hasSetting(String name) {
		 return conf.containsKey(name);
	}

	public Setting getSetting(String name) {
		return conf.get(name);
	}

	public Settings getSettings() {
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
		for (Setting s : conf) {
			ps.println(prefix + s.dumpToString());
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
		for (Setting s : conf) {
			sb.append(s.dumpToString());
			sb.append(",");
		}
		sb.append("}");
		return sb.toString();
	}
}
