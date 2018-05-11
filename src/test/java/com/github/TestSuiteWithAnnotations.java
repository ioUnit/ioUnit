package com.github;
import org.junit.runner.RunWith;

import com.github.iounit.annotations.IOUnitInput;
import com.github.iounit.annotations.IOUnitInputFile;
import com.github.iounit.annotations.IOTest;

@RunWith(com.github.iounit.IOUnitTestRunner.class)
public class TestSuiteWithAnnotations{

    @IOUnitInputFile
    String myFile;
    
    @IOUnitInput
    String myInput;
    
	@IOTest
	public String run() {
	    return myInput.toUpperCase();
	}

}