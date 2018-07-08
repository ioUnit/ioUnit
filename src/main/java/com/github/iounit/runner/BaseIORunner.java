package com.github.iounit.runner;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameter;

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
        if (!expected.replaceAll("\r\n?", "\n").equals(output.replaceAll("\r\n?", "\n"))) {
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
        if (matcher.matches() && matcher.groupCount() > 0) {
            return new File(matcher.group(1) + ".expected." + (matcher.groupCount() > 1 ? matcher.group(2) : "txt"));
        } else {
            return new File(file2.getPath().replaceAll("(.*)[.]([^.]+)", "$1.expected.$2"));
        }
    }
    private File determineFailureOutFile(final File file2) {
        final Matcher matcher = Pattern.compile(getMatcher()).matcher(file2.getPath());
        if (matcher.matches() && matcher.groupCount() > 0) {
            return new File(matcher.group(1) + ".failed." + (matcher.groupCount() > 1 ? matcher.group(2) : "txt"));
        } else {
            return new File(file2.getPath().replaceAll("(.*)[.]([^.]+)", "$1.expected.$2"));
        }
    }

    private String getMatcher() {
        final Method[] methods = MethodUtils.getMethodsWithAnnotation(sourceTestClass, IOTest.class);
        final IOTest ioInput = methods.length > 0 ? methods[0].getAnnotation(IOTest.class) : null;
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
    
    private boolean saveFailure() {
        final Method[] methods = MethodUtils.getMethodsWithAnnotation(sourceTestClass, IOTest.class);
        final IOTest testInfo = methods.length > 0 ? methods[0].getAnnotation(IOTest.class) : null;
        if (testInfo != null) {
            return testInfo.saveFailedOutput();
        }
        return true;
    }

    public abstract String run(String input) throws Exception;

}
