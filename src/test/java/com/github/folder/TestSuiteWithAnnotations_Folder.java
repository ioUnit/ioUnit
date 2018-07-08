package com.github.folder;
import org.junit.Before;
import org.junit.runner.RunWith;
import com.github.iounit.annotations.*;

import org.junit.Assert;

import com.github.iounit.IOUnitTestRunner;

@RunWith(IOUnitTestRunner.class)
public class TestSuiteWithAnnotations_Folder{

    @IOUnitInputFile
    String myFile;
    boolean before = false;
    
    @IOUnitInput(folder="src\\test\\resources\\com\\github\\iounit\\testSuite1")
    String myInput;
    
	@IOTest
	public String run() {
	    Assert.assertTrue(before);
	    return myInput.toUpperCase();
	}
	
	@Before
	public void setUp(){
	    before=true;
	}
	

}