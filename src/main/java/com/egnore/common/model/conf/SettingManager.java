package com.egnore.common.model.conf;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import com.egnore.common.Dictionary;
import com.egnore.common.StringKeyedValue;
import com.egnore.common.io.LinedFileReader;

///< TODO: Maybe it's better to use interface/abstract class instead of template.
public class SettingManager<I extends StringKeyedValue> {

	protected Dictionary<I> dict;

	public SettingManager() {
		this.dict = null;
	}

	public SettingManager(Dictionary<I> dict) {
		this.dict = dict;
	}

	public void setSettingDictionary(Dictionary<I> dict) {
		this.dict = dict;
	}

	public Dictionary<I> getSettingDictionary() {
		return this.dict;
	}

	public I get(String key) {
		return this.dict.get(key);
	}

	public I getOrNew(String key, ConfigurableTreeNode context) {
		I sd = get(key);
		if (sd == null) {
			sd = newItem(key, context);
			dict.addUserDefined(sd);
		}
		return sd;
	}

	public I newItem(String key, ConfigurableTreeNode context) {
		return null;
	}

	public boolean containsKey(String key) {
		return this.dict.containsKey(key);
	}
/*	
	public SettingDescription newSettingDescription(String name) {
		return newSettingDescription(name, null);
	}

	public SettingDescription newSettingDescription(String name, String defaultValue) {
		return new SettingDescription(name, name, defaultValue);
	}

	public Setting newSetting(String name, String value) {
		return newSetting(name, value, false);
	}

	public Setting newSetting(String name, String value, boolean noNew) {
		SettingDescription pd;
		if (dict != null) {
			pd = dict.findByName(name);
			if (pd == null) {
				if (noNew)
					return null;
				else {
					pd = newSettingDescription(name, value);
				}
			}
		} else {
			pd = new SettingDescription(name, name, value);
		}
		return new Setting(pd, value);
	}
*/
	public String storeItemToString(I item) {
		return "";
	}
	
	public I loadItemFromString(String s) {
		return null;
	}
	
	public void dumpUserDefinedSettings(PrintStream ps) {
		List<I> ud = dict.getUserDefinedList();
		ps.println(ud.size());
		for (I i : ud) {
			ps.println(storeItemToString(i));
		}
	}
	
	public void loadUserDefinedSettings(LinedFileReader reader) throws NumberFormatException, IOException {
		int n = Integer.parseInt(reader.next());
		for (int i = 0; i < n; i++) {
			dict.addUserDefined(loadItemFromString(reader.next()));
		}
	}

}
