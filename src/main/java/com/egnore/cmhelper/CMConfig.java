package com.egnore.cmhelper;

import com.cloudera.api.model.ApiConfig.ValidationState;
import com.egnore.cluster.model.RoleType;
import com.egnore.cluster.model.ServiceType;

public class CMConfig {
	private String name;
	private String defaultValue;
	private String displayName;
	private String description;
	private String relatedName;
	private Boolean required;
	private ServiceType st;
	private RoleType rt;
//	private ValidationState validationState;
//	private String validationMessage;
//	private Boolean validationWarningsSuppressed;
	public CMConfig() {
		
	}

	public void init() {}

	public CMConfig(String name, String relatedName, String defaultValue, ServiceType st, RoleType rt) {
		this.name = name;
		this.relatedName = relatedName;
		this.defaultValue = defaultValue;
		this.st = st;
		this.rt = rt;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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
	public String getRelatedName() {
		return relatedName;
	}
	public void setRelatedName(String relatedName) {
		this.relatedName = relatedName;
	}
	public Boolean getRequired() {
		return required;
	}
	public void setRequired(Boolean required) {
		this.required = required;
	}

	public ServiceType getSt() {
		return st;
	}

	public void setSt(ServiceType st) {
		this.st = st;
	}

	public RoleType getRt() {
		return rt;
	}

	public void setRt(RoleType rt) {
		this.rt = rt;
	}
}
