package com.egnore.cluster.model;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.egnore.common.TextSerDeObject;
import com.egnore.common.io.LinedFileReader;

public class HostManager implements TextSerDeObject {

	protected List<Host> nodes = new ArrayList<Host>();

	static HostManager inst = new HostManager();
	
	static public HostManager getInstance() {
		return inst;
	}

	public List<Host> getHostList() {
		return nodes;
	}

	static public void addHost(Host h) {
		inst.nodes.add(h);
	}

	public Host getHostbyId(String id) {
		for (Host h : nodes) {
			if (id.equals(h.id))
				return h;
		}
		return null;
	}

	public Host getHostbyIp(String ip) {
		for (Host h : nodes) {
			if (ip.equals(h.ip))
				return h;
		}
		return null;
	}

	public Host getHostbyName(String name) {
		for (Host h : nodes) {
			if (name.equals(h.fqdn))
				return h;
		}
		return null;
	}

	public void dump(PrintStream ps) {
		ps.println(nodes.size());
		for (Host h : nodes) {
			ps.println(h.saveToString());
		}
	}

	public void loadFromLinedStrings(LinedFileReader reader) throws IOException {	
		int n = Integer.parseInt(reader.next());
		for (int i = 0; i < n; i++) {
			Host h = new Host();
			h.loadFromString(reader.next());
			nodes.add(h);
		}
	}

}
