package com.github.iounit.util;

import java.io.File;

public class PackageToPath {

	/**
	 * Build the path of a class.
	 * 
	 * @param path Path to convert
	 * @param testClass Class providing the relative package
	 * @return converted path
	 */
	public static String convert(String path, Class<?> testClass) {
		path = path.replace('/', '.').replace('\\', '.');
		String retval = path + (testClass.getPackage() == null ? "" : "." + testClass.getPackage().getName());
		return retval.replace("..", ".").replace('.', File.separatorChar);
	}
}
