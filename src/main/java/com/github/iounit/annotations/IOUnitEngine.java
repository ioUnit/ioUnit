package com.github.iounit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>IOUnitEngine</code> annotation specifies the class the will execute
 * the test, process the input and produce the output
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface IOUnitEngine {
	Class<?> engineClass();
}