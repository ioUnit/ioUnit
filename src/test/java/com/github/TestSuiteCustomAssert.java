package com.github;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.github.iounit.annotations.IOAssert;
import com.github.iounit.annotations.IOUnitEngine;
import com.github.iounit.runner.BaseIORunner;

@RunWith(com.github.iounit.IOUnitTestRunner.class)
@IOUnitEngine(engineClass=TestSuiteCustomAssert.class)
public class TestSuiteCustomAssert extends BaseIORunner{

	@Override
	public String run(String input) {
		return input.toUpperCase();
	}
	
	@IOAssert
	public static void myAssrt(String foo, String bar){
	    System.out.println("compare " + foo + " to " + bar);
	    
	}

}