package com.github;
import org.junit.runner.RunWith;

import com.github.iounit.annotations.IOUnitInput;
import com.github.iounit.annotations.IOUnitInputFile;
import com.github.iounit.annotations.IOUnitTest;

@RunWith(com.github.iounit.IOUnitTestRunner.class)
public class TestSuiteWithAnnotations{

    @IOUnitInputFile
    String myFile;
    
    @IOUnitInput
    String myInput;
    
	@IOUnitTest
	public String run() {
	    return myInput.toUpperCase();
	}

}