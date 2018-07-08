package com.github.iounit.runner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * A {@link BlockJUnit4ClassRunner} with parameters support. Parameters can be
 * injected via constructor or into annotated fields.
 */
public class IOUnitClassRunnerWithParameters extends BlockJUnit4ClassRunnerWithParameters {
	protected final Object[] parameters;
	private Class<?> suiteClass;

	public IOUnitClassRunnerWithParameters(Class<?> suiteClass, TestWithParameters test) throws InitializationError {
		super(x(test));
		this.suiteClass = suiteClass;
		parameters = test.getParameters().toArray(new Object[test.getParameters().size()]);
	}
	
	public static TestWithParameters x(TestWithParameters test){
         return test;
	}

	@Override
	public Object createTest() throws Exception {
		final Object retval = super.createTest();
		for (final Field f : FieldUtils.getFieldsWithAnnotation(retval.getClass(), SuiteClass.class)) {
			f.set(retval, suiteClass);
		}
		return retval;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface SuiteClass {
		/**
		 * Method that returns the class of the Test Suite
		 *
		 * @return the class of the calling Test Suite.
		 */
		Class<?> value() default Object.class;
	}
}