package com.github.iounit.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.github.iounit.util.PackageToPath;

public class TestPackageToPath {

	@Test
	public void test(){
		String result = PackageToPath.convert("src", TestPackageToPath.class);
		Assert.assertEquals("src/com/github/iounit/util".replace('/', File.separatorChar), result);
	}
	

	@Test
	public void testTrailingChar(){
		String result = PackageToPath.convert("src/", TestPackageToPath.class);
		Assert.assertEquals("src/com/github/iounit/util".replace('/', File.separatorChar), result);
	}
}
