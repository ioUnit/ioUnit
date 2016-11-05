package com.github.iounit.filter;

import java.io.File;
import java.io.FileFilter;

public class VisibleFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		return !pathname.isHidden() && !pathname.isDirectory();
	}

}