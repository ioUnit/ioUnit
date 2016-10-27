package com.github.iounit;


import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParametersFactory;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;

import com.github.iounit.annotations.IOInput;
import com.github.iounit.runner.SQLScriptRunner;
import com.github.iounit.util.Annotations;
import com.github.iounit.util.PackageToPath;


/**
 * Run a test over each *.cf* file in src/test/resources/com/cflint/tests
 * 
 * @author ryaneberly
 *
 */
public class IOUnitTestRunner extends ParentRunner<Runner>{

	Class<?> testClass;

	final File baseFolder;
	final boolean root;

	public IOUnitTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		this.baseFolder = new File(PackageToPath.convert("src/test/resources", testClass));
		this.testClass=testClass;
		root=true;
	}
	public IOUnitTestRunner(Class<?> testClass,File baseFolder) throws InitializationError {
		super(testClass);
		this.baseFolder = baseFolder;
		this.testClass=testClass;
		root=false;
		
	}
	
    /**
     * The <code>LocatorClass</code> annotation specifies the class that lives in the same project folder
     * 
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface LocatorClass {
        /**
         * @return the classes to be run
         */
        public Class<?> value();
    }
    /**
     * The <code>Folder</code> annotation specifies the root project folder of the test inputs
     * 
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface Folder {
        /**
         * @return the classes to be run
         */
        public String value();
    }


    private static final ParametersRunnerFactory DEFAULT_FACTORY = new BlockJUnit4ClassRunnerWithParametersFactory();
    
	@Override
	protected List<Runner> getChildren() {
		List<Runner> children = new ArrayList<Runner>();
		for(File folder: baseFolder.listFiles(new VisibleFolderFilter())){
			try {
				children.add( new IOUnitTestRunner(testClass,folder));
				
			} catch (InitializationError e) {
				//notifier.fireTestFailure(new Failure(child.getDescription(), e));
				e.printStackTrace();
			}
		}
		for(File file: baseFolder.listFiles(new VisibleFileFilter())){
			Class<?> theClass = SQLScriptRunner.class;
			TestClass testClass = new TestClass(theClass);
			TestWithParameters test = new TestWithParameters("[" + file.getName() + "]", testClass,//getTestClass(),
	                Arrays.asList((Object)file));
			try {
				children.add( DEFAULT_FACTORY
				        .createRunnerForTestWithParameters(test));
			} catch (InitializationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getCauses());
			}
		}
		return children;
	}

	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner runner, RunNotifier notifier) {
		runner.run(notifier);

	}
	
	  class VisibleFilter implements FileFilter{

			@Override
			public boolean accept(File pathname) {
				return !pathname.isHidden();
			}
	    	
	    }
	  class VisibleFolderFilter implements FileFilter{

			@Override
			public boolean accept(File pathname) {
				return !pathname.isHidden() && pathname.isDirectory() && !pathname.getName().startsWith(".")
						&& pathname.list().length > 0;
			}
	    	
	    }
	  class VisibleFileFilter implements FileFilter{

			@Override
			public boolean accept(File pathname) {
				return !pathname.isHidden() && !pathname.isDirectory();
			}
	    	
	    }
		
		@Override
		protected String getName() {
			// TODO Auto-generated method stub
			return root?super.getName() : baseFolder.getName();
		}
}
