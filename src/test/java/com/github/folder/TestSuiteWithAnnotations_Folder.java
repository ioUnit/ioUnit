package com.github.folder;
import org.junit.runner.RunWith;

import com.github.iounit.annotations.IOUnitInput;
import com.github.iounit.annotations.IOUnitInputFile;
import com.github.iounit.annotations.IOTest;

@RunWith(com.github.iounit.IOUnitTestRunner.class)
public class TestSuiteWithAnnotations_Folder{

    @IOUnitInputFile
    String myFile;
    
    @IOUnitInput(folder="src\\test\\resources\\com\\github\\iounit\\testSuite1")
    String myInput;
    
	@IOTest
	public String run() {
	    return myInput.toUpperCase();
	}

}