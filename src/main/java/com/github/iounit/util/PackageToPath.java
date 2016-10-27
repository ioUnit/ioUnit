package com.github.iounit.util;

import java.io.File;

public class PackageToPath {

	/**
	 * Build the path of a class.
	 * @param path
	 * @param testClass
	 * @return
	 */
	public static String convert(String path, Class<?> testClass){
		path = path.replace('/', '.').replace('\\', '.');
		String retval = path + (testClass.getPackage()==null? "" : "." + testClass.getPackage().getName());
		return retval.replace("..", ".").replace('.', File.separatorChar);
	}
}
