package com.github.iounit.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

	public static String read(InputStream is) throws IOException{
		int length = is.available();
		byte [] b = new byte[length];
		is.read(b);
		is.close();
		return new String(b,"UTF-8");
	}
	public static void write(String value, OutputStream is) throws IOException{
		is.write(value.getBytes("UTF-8"));
		is.flush();
		is.close();
	}
}
