package com.github.iounit.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameter;

import com.github.iounit.runner.IOUnitClassRunnerWithParameters.SuiteClass;
import com.github.iounit.util.FileUtils;

public abstract class BaseIORunner {

	@Parameter
	public File file;

	@SuiteClass
	public Class<?> sourceTestClass;

	@Test
	public void runTest() throws FileNotFoundException, IOException {
		String input = FileUtils.read(new FileInputStream(file));
		final String output = run(input);
		FileUtils.write(output,new FileOutputStream(determineOutFile(file)));
	}

	
	private File determineOutFile(File file2) {
		return new File(file2.getPath().replaceAll("(.*)[.]([^.]+)", "$1.expected.$2"));
	}

	public abstract String run(String input);
	
}
