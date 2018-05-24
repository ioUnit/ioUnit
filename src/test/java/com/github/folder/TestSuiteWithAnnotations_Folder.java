package com.github.folder;
import org.junit.runner.RunWith;
import com.github.iounit.annotations.*;
import com.github.iounit.IOUnitTestRunner;

@RunWith(IOUnitTestRunner.class)
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