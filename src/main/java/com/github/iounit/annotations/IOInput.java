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
@Target(ElementType.TYPE)
@Inherited
@Deprecated
public @interface IOInput {
	String matches() default "";

	String extension() default "";

	String exclude() default "";

	String folder() default "";
}