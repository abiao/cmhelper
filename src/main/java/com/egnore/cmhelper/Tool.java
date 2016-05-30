package com.egnore.cmhelper;

public class Tool {
	public static void main(String[] args) throws Exception {
		try {
		Connection conn = new Connection("52.78.19.217");
		Cluster c = new Cluster(conn);
		c.createDefault();
		} catch (Exception e) {
			System.out.print(e.getMessage());
			System.out.print(e.getCause());
			throw e;
		}
	}
}
