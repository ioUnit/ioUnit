package com.github;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.github.iounit.annotations.IOUnitEngine;
import com.github.iounit.runner.BaseIORunner;

@RunWith(com.github.iounit.IOUnitTestRunner.class)
@IOUnitEngine(engineClass=TestSuite2.class)
public class TestSuite2 extends BaseIORunner{

	@Override
	public String run(String input) {
		return input.toUpperCase();
	}

}