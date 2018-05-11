# ioUnit
An opinionated way to use junit parameterized tests to process input files and compare with expected output.

This provides a simple alternative to hardcoding fragile strings in your java JUnit testcases.  The framework is light on the amount of code required by your tests.
* Provide the inputs.
* Implement how the output is generated.
* Run once to get a baseline output.

## Quick Examples
The following example testcase will run every file in the src/test/resources/com/github folder and its subfolders that is of the form &ast;.input.&ast; as test and compare its output with a &ast;.expected.txt file in the same folder.  This sample test converts the input to upper case.

    import org.junit.runner.RunWith;
    import com.github.iounit.annotations.*;
    import com.github.iounit.IOUnitTestRunner;

    @RunWith(IOUnitTestRunner.class)
    public class TestFileSuite{

        @IOUnitInput(folder="src/test/resources/com/github")
        String myInput;

        @IOTest
        public String run() {
            return myInput.toUpperCase();
        }
    }
    
Alternatively the input can be recieved as a parameter.

    @RunWith(IOUnitTestRunner.class)
    public class TestFileSuite{

        @IOTest
        public String run(@IOUnitInput(folder="src/test/resources/com/github") String myInput) {
            return myInput.toUpperCase();
        }
    }
    
By default the package of the test suite is used for the path.  The following example will run all inputs in src/test/resources/com/github/iounit.

    package com.github.iounit;
    @RunWith(IOUnitTestRunner.class)
    public class TestFileSuite{

        @IOTest
        public String run() String myInput) {
            return myInput.toUpperCase();
        }
    }

