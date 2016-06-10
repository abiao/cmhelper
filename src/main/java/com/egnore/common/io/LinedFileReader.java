package com.egnore.common.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class LinedFileReader {

	InputStreamReader reader;
	BufferedReader bufferedReader;

	public LinedFileReader(String path) throws UnsupportedEncodingException, FileNotFoundException {
		String encoding="utf8";
		File file=new File(path);
		if(file.isFile() && file.exists()) { //判断文件是否存在
			reader = new InputStreamReader(
					new FileInputStream(file),encoding);//考虑到编码格式
			bufferedReader = new BufferedReader(reader);
		}
	}

	public void close() throws IOException {
		bufferedReader.close();
		reader.close();
	}

	public String next() throws IOException {
		String s = bufferedReader.readLine();
		if (s != null) s.trim();
		return s;
	}
 }
