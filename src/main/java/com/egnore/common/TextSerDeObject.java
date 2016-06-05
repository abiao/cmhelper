package com.egnore.common;

import java.io.IOException;
import java.io.PrintStream;

import com.egnore.common.io.LinedFileReader;

public interface TextSerDeObject {
	public void dump(PrintStream ps);
	public void loadFromLinedStrings(LinedFileReader reader) throws IOException;
}
