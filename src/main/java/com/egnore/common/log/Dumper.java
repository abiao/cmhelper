package com.egnore.common.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Dumper {
	final static PrintStream DEFAULT_STREAM = System.out;
	PrintStream ps;

	public Dumper() {
		this(null);
	}

	public Dumper(String path) {
		if ((path == null) || (path.isEmpty())) {
			ps = DEFAULT_STREAM;
		} else {				
			File file=new File(path);
			try {
				if(!file.exists())
					file.createNewFile();
				FileOutputStream out = new FileOutputStream(path);
				ps = new PrintStream(out);
			} catch (IOException e) {
				ps = DEFAULT_STREAM;
			}
		}
	}
	
	public PrintStream getPrintStream() {
		return ps;
	}

	public void close() {
		if (!DEFAULT_STREAM.equals(ps))
			ps.close();
	}
}
