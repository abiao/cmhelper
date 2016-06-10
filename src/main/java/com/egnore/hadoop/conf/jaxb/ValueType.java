package com.egnore.hadoop.conf.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum ValueType {
	INT,
	DIR,
	STRING;
}
