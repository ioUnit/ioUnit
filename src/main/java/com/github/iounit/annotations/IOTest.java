package com.github.iounit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>LocatorClass</code> annotation specifies the class that lives in
 * the same project folder
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface IOTest {
    String inputMatches() default "";

    String inputExtension() default "";
    String outputExtension() default "";

    String inputExclude() default "";

    String inputFolder() default "";
    String sourceFolder() default "";
    String sourcePackage() default "";
    boolean saveFailedOutput() default true;
}