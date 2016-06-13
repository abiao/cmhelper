package com.egnore.common.log;

import java.io.PrintStream;

public class Log {
	protected PrintStream ps = (new Dumper()).getPrintStream(); 

	static public Log getInstance(Class c) {
		return new Log();
	}

	public void error(String message) {
		ps.println(message);
	}

	public void warn(String message) {
		ps.println(message);
	}

	public void info(String message) {
		ps.println(message);
	}

	public void debug(String message) {
		ps.println(message);
	}
}
