package com.github.iounit;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.junit.runners.parameterized.TestWithParameters;

import com.github.iounit.annotations.IOInput;
import com.github.iounit.annotations.IOTest;
import com.github.iounit.annotations.IOUnitEngine;
import com.github.iounit.annotations.IOUnitInput;
import com.github.iounit.annotations.IOUnitInputFile;
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

    public IOUnitTestRunner(final Class<?> testClass) throws InitializationError {
        super(testClass);
        //Deprecated @IOInput first.
        final IOInput ioInput = testClass.getAnnotation(IOInput.class);
        String folder = "src/test/resources";
        String packageName = testClass.getPackage().getName();
        if (ioInput != null && !ioInput.folder().trim().isEmpty()) {
            folder = ioInput.folder().trim();
        }
        File tempSourceFolder=null;
        final Method[] methods = getTestMethods(testClass);
        
        if(methods.length>0){
            final IOTest ioTest = methods[0].getAnnotation(IOTest.class);
            if (ioTest != null && !ioTest.inputFolder().trim().isEmpty()) {
                tempSourceFolder = new File(ioTest.inputFolder().trim());
            }
            if (ioTest != null && !ioTest.sourceFolder().trim().isEmpty()) {
                folder = ioTest.sourceFolder().trim();
            }
            if (ioTest != null && !ioTest.sourcePackage().trim().isEmpty()) {
                packageName = ioTest.sourcePackage().trim();
            }
            for(Parameter parameter:methods[0].getParameters() ){
                IOUnitInput inputAnno = parameter.getAnnotation(IOUnitInput.class);
                if(inputAnno!=null && !inputAnno.folder().trim().isEmpty()){
                    tempSourceFolder = new File(inputAnno.folder().trim());
                }
                IOUnitInputFile inputFileAnno = parameter.getAnnotation(IOUnitInputFile.class);
                if(inputFileAnno!=null && !inputFileAnno.folder().trim().isEmpty()){
                    tempSourceFolder = new File(inputFileAnno.folder().trim());
                }
            }

        }
        for(Field parameter:FieldUtils.getFieldsListWithAnnotation(testClass, IOUnitInput.class) ){
            IOUnitInput inputAnno = parameter.getAnnotation(IOUnitInput.class);
            if(inputAnno!=null && !inputAnno.folder().trim().isEmpty()){
                tempSourceFolder = new File(inputAnno.folder().trim());
            }
        }
        for(Field parameter:FieldUtils.getFieldsListWithAnnotation(testClass, IOUnitInputFile.class) ){
            IOUnitInputFile inputFileAnno = parameter.getAnnotation(IOUnitInputFile.class);
            if(inputFileAnno!=null && !inputFileAnno.folder().trim().isEmpty()){
                tempSourceFolder = new File(inputFileAnno.folder().trim());
            }
        }
        if(tempSourceFolder==null){
            tempSourceFolder = new File(PackageToPath.convert(folder, packageName));
        }
        this.baseFolder = tempSourceFolder;
        this.testClass = testClass;
        root = true;
        init(methods.length>0?methods[0]:null);
    }


    protected Method[] getTestMethods(final Class<?> testClass) {
        final Method[] methods = MethodUtils.getMethodsWithAnnotation(testClass, IOTest.class);
        final Method[] methods2 = MethodUtils.getMethodsWithAnnotation(testClass, Test.class);
        ArrayList<Method> retval = new ArrayList<Method>(Arrays.asList(methods));
        retval.addAll(Arrays.asList(methods2));
        return retval.toArray(new Method[retval.size()]);
    }
    

    private IOUnitTestRunner(final Class<?> testClass, final File baseFolder) throws InitializationError {
        super(testClass);
        this.baseFolder = baseFolder;
        this.testClass = testClass;
        root = false;
    }
    public IOUnitTestRunner createChild(final File baseFolder) throws InitializationError{
        IOUnitTestRunner retval = new IOUnitTestRunner(testClass,baseFolder);
        retval.inputFileFilter = inputFileFilter;
        retval.engineClass = engineClass;
        return retval;
    }
    final static String DEFAULT_matcher = "(.*)\\.input\\.(.*)";
    final static String DEFAULT_exclude = ".*[.]expected[.].*";
/**
 * This is verbose lookups on the annotations at the class, method and parameter levels.
 * could be simplified.
 * @param method
 */
    protected void init(Method method) {
        final IOUnitEngine engineAnnotation = testClass.getAnnotation(IOUnitEngine.class);
        engineClass = (engineAnnotation != null) ? engineAnnotation.engineClass() : DEFAULT_ENGINE_CLASS;
        String matcher = "(.*)\\.input\\.(.*)";
        String exclude = ".*[.]expected[.].*";
        if(method !=null){
            final IOTest ioTest = method.getAnnotation(IOTest.class);
            if (ioTest != null) {
                if (!ioTest.inputMatches().trim().isEmpty()) {
                    matcher = ioTest.inputMatches();
                } else if (!ioTest.inputExtension().trim().isEmpty()) {
                    matcher = "(.*)[.](" + ioTest.inputExtension().replaceFirst("^[.]", "") + ")";
                }
                if (!ioTest.inputExclude().trim().isEmpty()) {
                    exclude = ioTest.inputExclude();
                }
            }
            for(Parameter parameter:method.getParameters() ){
                IOUnitInput inputAnno = parameter.getAnnotation(IOUnitInput.class);
                if(inputAnno!=null){
                    if (!inputAnno.matches().trim().isEmpty()) {
                        matcher = inputAnno.matches();
                    } else if (!inputAnno.extension().trim().isEmpty()) {
                        matcher = "(.*)[.](" + inputAnno.extension().replaceFirst("^[.]", "") + ")";
                    }
                    if (!inputAnno.exclude().trim().isEmpty()) {
                        exclude = inputAnno.exclude();
                    }
                }
                IOUnitInputFile inputFileAnno = parameter.getAnnotation(IOUnitInputFile.class);
                if(inputFileAnno!=null){
                    if (!inputFileAnno.matches().trim().isEmpty()) {
                        matcher = inputFileAnno.matches();
                    } else if (!inputFileAnno.extension().trim().isEmpty()) {
                        matcher = "(.*)[.](" + inputFileAnno.extension().replaceFirst("^[.]", "") + ")";
                    }
                    if (!inputFileAnno.exclude().trim().isEmpty()) {
                        exclude = inputFileAnno.exclude();
                    }
                }
            }
        }
        for(Field parameter:FieldUtils.getFieldsListWithAnnotation(testClass, IOUnitInput.class) ){
            IOUnitInput inputAnno = parameter.getAnnotation(IOUnitInput.class);
            if(inputAnno!=null){
                if (!inputAnno.matches().trim().isEmpty()) {
                    matcher = inputAnno.matches();
                } else if (!inputAnno.extension().trim().isEmpty()) {
                    matcher = "(.*)[.](" + inputAnno.extension().replaceFirst("^[.]", "") + ")";
                }
                if (!inputAnno.exclude().trim().isEmpty()) {
                    exclude = inputAnno.exclude();
                }
            }
        }
        for(Field parameter:FieldUtils.getFieldsListWithAnnotation(testClass, IOUnitInputFile.class) ){
            IOUnitInputFile inputFileAnno = parameter.getAnnotation(IOUnitInputFile.class);
            if(inputFileAnno!=null){
                if (!inputFileAnno.matches().trim().isEmpty()) {
                    matcher = inputFileAnno.matches();
                } else if (!inputFileAnno.extension().trim().isEmpty()) {
                    matcher = "(.*)[.](" + inputFileAnno.extension().replaceFirst("^[.]", "") + ")";
                }
                if (!inputFileAnno.exclude().trim().isEmpty()) {
                    exclude = inputFileAnno.exclude();
                }
            }
        }

        if(!DEFAULT_exclude.equals(exclude) || !DEFAULT_matcher.equals(matcher)){
            inputFileFilter = new MatchesFileFilter(matcher, exclude);
        }else{
            final IOInput ioInput = testClass.getAnnotation(IOInput.class);
            if (ioInput != null) {
                if (!ioInput.matches().trim().isEmpty()) {
                    matcher = ioInput.matches();
                } else if (!ioInput.extension().trim().isEmpty()) {
                    matcher = "(.*)[.](" + ioInput.extension().replaceFirst("^[.]", "") + ")";
                }
                if (!ioInput.exclude().trim().isEmpty()) {
                    exclude = ioInput.exclude();
                }
            }
            inputFileFilter = new MatchesFileFilter(matcher, exclude);
        }
    }

    private static final Class<?> DEFAULT_ENGINE_CLASS = com.github.iounit.runner.ExtensionBasedRunner.class;

    @Override
    protected List<Runner> getChildren() {
        final List<Runner> children = new ArrayList<Runner>();
        final File[] files = baseFolder.listFiles(new VisibleFolderFilter());
        if (files != null) {
            for (final File folder : files) {
                try {
                    children.add(createChild(folder));
                } catch (final InitializationError e) {
                    throw new RuntimeException(e);
                }
            }
            final TestClass testInstanceClass = new TestClass(engineClass);

            for (final File file : baseFolder.listFiles(inputFileFilter)) {
                final Matcher matcher = inputFileFilter.getPattern().matcher(file.getName());
                String testName = file.getName();
                if (matcher.matches() && matcher.groupCount() > 0) {
                    testName = matcher.group(1);
                }
                final TestWithParameters test = new TestWithParameters("[" + testName + "]", testInstanceClass, // getTestClass(),
                        Arrays.asList((Object) file));
                try {
                    children.add(new IOUnitClassRunnerWithParameters(testClass, test));
                } catch (final InitializationError e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return children;
    }

    @Override
    protected Description describeChild(final Runner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(final Runner runner, final RunNotifier notifier) {
        runner.run(notifier);
    }

    @Override
    protected String getName() {
        return root ? super.getName() : baseFolder.getName();
    }
}
