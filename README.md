# ioUnit

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5059b019f9b246a99eb4e17f39a4cdda)](https://app.codacy.com/app/ryaneberly/ioUnit?utm_source=github.com&utm_medium=referral&utm_content=ioUnit/ioUnit&utm_campaign=Badge_Grade_Dashboard)

##### Test data should be easy

ioUnit is an opinionated way to use JUnit parameterized tests to process input files and compare with expected output.

This provides a simple alternative to hardcoding fragile strings in your java JUnit testcases.  The framework is light on the amount of code required by your tests.
* Provide the inputs.
* Implement how the output is generated.
* Run once to get a baseline output.

IOUnit is built on JUnit 4, but you can run it in projects using JUnit 5 with the [JUnit 4 runner](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4-running).

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
    
Alternatively the input can be received as a parameter.

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
        public String run(final String myInput) {
            return myInput.toUpperCase();
        }
    }

## Get Started
Add this to you build.gradle deps{}  (or mvn):


    // https://mvnrepository.com/artifact/com.github.iounit/iounit
    compile group: 'com.github.iounit', name: 'iounit', version: '0.8.0'
    
## Providing your own comparitor
By default iounit does a basic String.equals() comparison between the expected and actual.  Sometimes this is insufficient if your data contains something dynamic like an ID or timestamp.  You could pre-scrub those fields from your output, or provide your own comparison mechanism that is more relevant to your use case.

Simply annotation a static method in your test class with @IOAssert and provide a different implementation.  To fail the comparison the method must throw an exception.  Here is an example of doing a JSON comparison with JSON-Unit
	
    
    import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
    import static net.javacrumbs.jsonunit.core.Option.*;
    import com.github.iounit.annotations.IOAssert;
    
	@IOAssert
	public static void jsonCompare(String expected, String actual) throws IOException {
		assertThatJson(actual).when(IGNORING_EXTRA_FIELDS)
				.isEqualTo(expected);
	}
 
