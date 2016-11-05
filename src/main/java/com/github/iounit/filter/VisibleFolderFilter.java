package com.github.iounit.filter;

import java.io.File;
import java.io.FileFilter;

public class VisibleFolderFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		return !pathname.isHidden() && pathname.isDirectory() && !pathname.getName().startsWith(".")
				&& pathname.list().length > 0;
	}

}