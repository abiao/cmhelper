package com.egnore.hadoop.conf.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.egnore.common.StringKeyedValue;
import com.egnore.common.StringSerDeObject;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration implements StringKeyedValue,StringSerDeObject {
	String name;
	String file;

	@XmlElement(name="scope")
	ScopeType scope;

	@XmlElement(name="default-value")
	String defaultValue;
	boolean deprecated = false;
	String newName;
	
	ValueType type;
//	String minValue;
//	String maxValue;
//	String unit;
//	String incrementStep;
	
	@XmlElement(name="display-name")
	String displayName;
	String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public ScopeType getScope() {
		return scope;
	}

	public void setScope(ScopeType scope) {
		this.scope = scope;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public ValueType getType() {
		return type;
	}

	public void setType(ValueType type) {
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public String saveToString() {
		return name
				+ "[" + scope + "]"
				+ ",file=" + file
				//+ ",defaultValue=" + defaultValue
				;
	}

	public void loadFromString(String s) {
		// TODO Auto-generated method stub
	}

	public String getKey() {
		return name;
	}
}
