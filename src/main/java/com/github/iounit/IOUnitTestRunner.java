package com.github.iounit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.junit.runners.parameterized.TestWithParameters;

import com.github.iounit.annotations.IOInput;
import com.github.iounit.annotations.IOUnitEngine;
import com.github.iounit.filter.MatchesFileFilter;
import com.github.iounit.filter.VisibleFolderFilter;
import com.github.iounit.runner.IOUnitClassRunnerWithParameters;
import com.github.iounit.util.PackageToPath;

/**
 * Run a test over each *.cf* file in src/test/resources/com/cflint/tests
 * 
 * @author ryaneberly
 *
 */
public class IOUnitTestRunner extends ParentRunner<Runner> {

	Class<?> testClass;

	final File baseFolder;
	final boolean root;

	private MatchesFileFilter inputFileFilter;
	Class<? extends Object> engineClass;

	public IOUnitTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		IOInput ioInput = testClass.getAnnotation(IOInput.class);
		String folder = "src/test/resources";
		if (ioInput != null && !ioInput.folder().trim().isEmpty()) {
			folder = ioInput.folder().trim();
		}
		this.baseFolder = new File(PackageToPath.convert(folder, testClass));
		this.testClass = testClass;
		root = true;
		init();
	}

	public IOUnitTestRunner(Class<?> testClass, File baseFolder) throws InitializationError {
		super(testClass);
		this.baseFolder = baseFolder;
		this.testClass = testClass;
		root = false;
		init();
	}

	protected void init() {
		IOUnitEngine engineAnnotation = testClass.getAnnotation(IOUnitEngine.class);
		engineClass = (engineAnnotation != null) ? engineAnnotation.engineClass() : DEFAULT_ENGINE_CLASS;

		IOInput ioInput = testClass.getAnnotation(IOInput.class);
		String matcher = ".*";
		String exclude = ".*[.]expected[.].*";
		if (ioInput != null) {
			if (!ioInput.matches().trim().isEmpty()) {
				matcher = ioInput.matches();
			} else if (!ioInput.extension().trim().isEmpty()) {
				matcher = ".*[.]" + ioInput.extension().replaceFirst("^[.]", "");
			}
			if (!ioInput.exclude().trim().isEmpty()) {
				exclude = ioInput.exclude();
			}
		}
		inputFileFilter = new MatchesFileFilter(matcher, exclude);
	}

	private static final Class<?> DEFAULT_ENGINE_CLASS = com.github.iounit.runner.ExtensionBasedRunner.class;

	@Override
	protected List<Runner> getChildren() {
		final List<Runner> children = new ArrayList<Runner>();
		File[] files = baseFolder.listFiles(new VisibleFolderFilter());
		if (files != null) {
			for (File folder : files) {
				try {
					children.add(new IOUnitTestRunner(testClass, folder));
				} catch (InitializationError e) {
					throw new RuntimeException(e);
				}
			}
			final TestClass testInstanceClass = new TestClass(engineClass);

			for (File file : baseFolder.listFiles(inputFileFilter)) {
				TestWithParameters test = new TestWithParameters("[" + file.getName() + "]", testInstanceClass, // getTestClass(),
						Arrays.asList((Object) file));
				try {
					children.add(new IOUnitClassRunnerWithParameters(testClass, test));
				} catch (InitializationError e) {
					throw new RuntimeException(e);
				}
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

	@Override
	protected String getName() {
		return root ? super.getName() : baseFolder.getName();
	}
}
