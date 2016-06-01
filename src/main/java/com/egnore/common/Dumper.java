package com.egnore.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Dumper {
	PrintStream ps;
	public Dumper(String path) throws IOException {
		File file=new File(path);
		if(!file.exists())
			file.createNewFile();
		FileOutputStream out=new FileOutputStream(path);
		ps = new PrintStream(out);
	}
	
	public PrintStream getPrintStream() {
		return ps;
	}

	public void close() {
		ps.close();
	}
}
