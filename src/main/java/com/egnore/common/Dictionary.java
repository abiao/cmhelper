package com.egnore.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egnore.common.util.StringUtil;

public class Dictionary<I extends StringKeyedValue> /*extends java.util.Dictionary<String, I>*/ {

	protected List<I> predefines;
	protected Map<String, I> idLookup = new HashMap<String, I>();

	protected List<I> ud  = new ArrayList<I>();
	protected Map<String, String> deprecatedKeys = new HashMap<String, String>();
	public Dictionary() {
		this(null);
	}

	public Dictionary(List<I> predefines) {
		if (predefines != null) {
			this.predefines = predefines;
			build();
		} else 
			this.predefines = new ArrayList<I>();
		init();
	}

	public void init() {
		
	}

	protected void build() {
		for (I p : predefines) {
			buildIndex(p);
		}
		for (I p : ud) {
			buildIndex(p);
		}
	}

	protected final List<I> getPredefinedList() {
		return predefines;
	}

	public final List<I> getUserDefinedList() {
		return ud;
	}

	protected void buildIndex(I p) {
		String id = p.getKey();
		if (StringUtil.isValidString(id))	idLookup.put(id, p);
	}

	public void addPredefined(I p) {
		predefines.add(p);
		buildIndex(p);
	}

	public void addDeprecatedKey(String oldKey, String newKey) {
		deprecatedKeys.put(oldKey, newKey);
//		I oldp = get(oldKey);
//		I newp = get(newKey);
//		if (newp != null) {
//			
//		}
	}

	public void addUserDefined(I p) {
		ud.add(p);
		buildIndex(p);
	}
	
	public I get(String id) {
		if (!StringUtil.isValidString(id)) return null;
		I p = idLookup.get(id);
		if (p != null) {
			return p;
		}
		String nid = deprecatedKeys.get(id);
		return get(nid);
	}

//	public String getKey(I item) {
//		return item.getKey();
//		for(Entry<String, I> pair : idLookup.entrySet()) {
//			if (item.equals(pair.getValue()))
//				return pair.getKey();
//		}
//		return null;
//	}
	
	public boolean containsKey(String key) {
		return get(key) == null;
	}
/*
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Enumeration<String> keys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<I> elements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public I get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public I put(String key, I value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public I remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}
*/
//	public String storeItemToString(I item) {
//		return "";
//	}
//
//	public I loadItemFromString(String s) {
//		return null;
//	}
//	
//	public void dumpUserDefinedSettings(PrintStream ps) {
//		ps.println(ud.size());
//		for (I i : ud) {
//			ps.println(getKey(i) + ":" + storeItemToString(i));
//		}
//	}
//
//	public void loadUserDefinedSettings(LinedFileReader reader) throws NumberFormatException, IOException {
//		int n = Integer.parseInt(reader.next());
//		for (int i = 0; i < n; i++) {
//			String s = reader.next();
//			int x = s.indexOf(":");
//			String id = s.substring(0, x);
//			I p = loadItemFromString(s.substring(x + 1));
//			addUserDefined(id, p);
//		}
//	}
}
