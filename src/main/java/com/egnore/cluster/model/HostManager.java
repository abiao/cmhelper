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

//	public List<Host> getHostList() {
//		return nodes;
//	}

	public Host getHost(String h, String name) {
		Host host = findHostById(h);
		if (host != null) {
			if (!name.equals(host.getFQDN())) {
				///< TODO error
			}
		} else {
			host = new Host();
			host.ip = h;
			host.fqdn = name;
		}
		return host;
	}

	public boolean contains(Host host) {
		for (Host h : nodes) {
			if (host == h)
				return true;
		}
		return false;
	}

	public boolean contains(String h) {
		return findHost(h) != null;
	}

	public Host findHost(String h) {
		return findHostById(h);
	}

	public Host addHost(String ip, String hostname) {
		Host h = new Host();
		h.ip = ip;
		h.fqdn = hostname;
		inst.nodes.add(h);
		return h;
	}

	protected void addHost(Host h) {
		inst.nodes.add(h);
	}

	public Host findHostById(String id) {
		for (Host h : nodes) {
			if (id.equals(h.id))
				return h;
		}
		return null;
	}

	public Host findHostByIp(String ip) {
		for (Host h : nodes) {
			if (ip.equals(h.ip))
				return h;
		}
		return null;
	}

	public Host findSHostByName(String name) {
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
