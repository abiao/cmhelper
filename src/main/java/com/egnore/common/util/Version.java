package com.egnore.common.util;

public class Version {
	final static private int MAX_SEGMENT_NUMBER=3;
	public int[] segments = new int[MAX_SEGMENT_NUMBER];
	public Version(String v) {
		String[] segs = v.split("\\.|_");
		for (int i = 0; i < MAX_SEGMENT_NUMBER; i++) {
			segments[i] = 0;
//			if ((i < segs.length) && (segs[i] == null) || (segs[i].isEmpty()))
					
		}
	}
}
