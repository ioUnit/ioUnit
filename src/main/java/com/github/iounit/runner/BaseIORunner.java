package com.github.iounit.runner;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameter;

import com.github.iounit.annotations.IOAssert;
import com.github.iounit.annotations.IOInput;
import com.github.iounit.annotations.IOTest;
import com.github.iounit.runner.IOUnitClassRunnerWithParameters.SuiteClass;
import com.github.iounit.util.FileUtils;

public abstract class BaseIORunner {

    @Parameter
    public File file;

    @SuiteClass
    public Class<?> sourceTestClass;

    @Test
    public void runTest() throws Exception {
        final String input = FileUtils.read(new FileInputStream(file));
        String expected = null;
        final String output = run(input);
        final File outFile = determineOutFile(file);
        if (!outFile.exists()) {
            FileUtils.write(output, new FileOutputStream(outFile));
        }
        expected = FileUtils.read(new FileInputStream(outFile));
        
     // Normalize new lines for compare
        final Object expectedObj = expected.replaceAll("\r\n?", "\n");
        final Object actualObj = output.replaceAll("\r\n?", "\n");

        //Support a custom assert method with @IOAssert
        final List<Method> asserts = getIOAssertMethods();
        boolean matched=true;
        if (asserts == null || asserts.isEmpty()) {
            matched = expectedObj.equals(actualObj);
        } else {
            for (Method m : asserts) {
                if (!Modifier.isStatic(m.getModifiers())) {
                    throw new RuntimeException("Method " + m.getName() + " annotated with @IOAssert must be static");
                }
                
                if (m.getParameterCount() != 2) {
                    throw new RuntimeException("Method " + m.getName() + " annotated with @IOAssert must have 2 args");
                }
                try {
                    m.invoke(null, expectedObj, actualObj);
                    matched = true;
                } catch (Exception e) {
                    matched = false;
                    break;
                }
            }
        }
        
        if (!matched) {
            if(saveFailure()){
                File failureFile = determineFailureOutFile(file);
                try{
                    FileUtils.write(output, new FileOutputStream(failureFile ));
                }catch(Exception e){
                    System.err.println("Could not write " + failureFile);
                }
            }
            if ("Y".equals(System.getProperty("IOUnitOverwriteOutput"))) {
                FileUtils.write(output, new FileOutputStream(outFile));
            } else {
                assertEquals(normalize(expected), normalize(output));
            }
        }
    }

    protected String normalize(final String input) {
        final StringBuilder sb = new StringBuilder(input.replaceAll("\r\n?", "\n"));
        int idx = sb.indexOf("\t");
        while (idx >= 0) {
            final int lineStart = Math.max(0, sb.substring(0, idx).lastIndexOf("\n") + 1);
            final StringBuilder tab = new StringBuilder("    ");
            if ((idx - lineStart) % 4 != 0) {
                tab.setLength((idx - lineStart) % 4);
            }
            sb.replace(idx, idx + 1, tab.toString());
            idx = sb.indexOf("\t");
        }
        return sb.toString();
    }

    /**
     * The first and 2nd group are used to determine the expected file name
     * 
     * @param file2
     * @return
     */
    private File determineOutFile(final File file2) {
        final Matcher matcher = Pattern.compile(getMatcher()).matcher(file2.getPath());
        final IOTest ioInput = getIOTestAnnotation();
        if (matcher.matches() && matcher.groupCount() > 0) {
            String extension = ioInput==null || ioInput.outputExtension().length()==0? "txt" : ioInput.outputExtension();
            return new File(matcher.group(1) + ".expected." + (matcher.groupCount() > 1 ? matcher.group(2) : extension));
        } else {
            String extension = ioInput==null || ioInput.outputExtension().length()==0? "$2" : ioInput.outputExtension();
            return new File(file2.getPath().replaceAll("(.*)[.]([^.]+)", "$1.expected." + extension));
        }
    }
    private File determineFailureOutFile(final File file2) {
        final Matcher matcher = Pattern.compile(getMatcher()).matcher(file2.getPath());
        if (matcher.matches() && matcher.groupCount() > 0) {
            return new File(matcher.group(1) + ".failed." + (matcher.groupCount() > 1 ? matcher.group(2) : "txt"));
        } else {
            return new File(file2.getPath().replaceAll("(.*)[.]([^.]+)", "$1.failed.$2"));
        }
    }

    
    private String getMatcher() {
        final IOTest ioInput = getIOTestAnnotation();
        if (ioInput != null) {
            if (!ioInput.inputMatches().trim().isEmpty()) {
                return ioInput.inputMatches();
            } else if (!ioInput.inputExtension().trim().isEmpty()) {
                return "(.*)[.]" + ioInput.inputExtension().replaceFirst("^[.]", "");
            }
        }
        final IOInput ioInputOld = sourceTestClass.getAnnotation(IOInput.class);
        if (ioInputOld != null) {
            if (!ioInputOld.matches().trim().isEmpty()) {
                return ioInputOld.matches();
            } else if (!ioInputOld.extension().trim().isEmpty()) {
                return "(.*)[.]" + ioInputOld.extension().replaceFirst("^[.]", "");
            }
        }

        return "(.*)\\.input\\.(.*)";
    }

    protected IOTest getIOTestAnnotation() {
        final Method[] methods = MethodUtils.getMethodsWithAnnotation(sourceTestClass, IOTest.class);
        final IOTest ioInput = methods.length > 0 ? methods[0].getAnnotation(IOTest.class) : null;
        return ioInput;
    }
    
    protected List<Method> getIOAssertMethods() {
        final Method[] methods = MethodUtils.getMethodsWithAnnotation(sourceTestClass, IOAssert.class);
        return methods.length > 0 ? Arrays.asList(methods) : null;
    }
    
    private boolean saveFailure() {
        final IOTest testInfo = getIOTestAnnotation();
        if (testInfo != null) {
            return testInfo.saveFailedOutput();
        }
        return true;
    }

    public abstract String run(String input) throws Exception;

}
