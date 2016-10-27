package com.github.iounit.runner;

import java.io.File;

import org.junit.Test;

import com.github.iounit.annotations.IOInput;

public class SQLScriptRunner {

	private String input;
	final File file;
	public SQLScriptRunner(File file){
		this.file = file;
	}
	@Test
	public void test1(){
		System.out.println("SQL:" + file);
		System.out.println("SQL input:" + input.substring(1,20));
	}
	@IOInput
	public void setInput(String input) {
		this.input = input;
	}
}

