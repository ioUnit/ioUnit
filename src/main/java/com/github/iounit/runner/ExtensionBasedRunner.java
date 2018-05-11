package com.github.iounit.runner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.github.iounit.annotations.IOUnitInput;
import com.github.iounit.annotations.IOUnitInputFile;
import com.github.iounit.annotations.IOTest;

public class ExtensionBasedRunner extends BaseIORunner {

    @Override
    public String run(final String input) {
        final Method[] methods = MethodUtils.getMethodsWithAnnotation(sourceTestClass, IOTest.class);
        // apply field annotations
        if (methods.length == 1) {
            final Object instance = sourceTestClass.isInstance(this) ? this : createInstance();
            applyFieldAnnotations(instance, input);
            final Object retval = callRunMethod(input, methods[0], instance);
            return retval == null ? null : retval.toString();
        } else if (methods.length > 1) {
            throw new RuntimeException(
                    "Class " + sourceTestClass.getName() + " should have only 1 method with @IOTest");
        }
        // Default implementation returns null
        return null;
    }

    protected void applyFieldAnnotations(final Object instance, final String input) {
        for (final Field field : FieldUtils.getFieldsListWithAnnotation(sourceTestClass, IOUnitInput.class)) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                field.set(instance, input);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(
                        "Unable to set field " + field.getName() + " on " + sourceTestClass.getName(), e);
            }
        }
        for (final Field field : FieldUtils.getFieldsListWithAnnotation(sourceTestClass, IOUnitInputFile.class)) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                if (field.getType().equals(String.class)) {
                    field.set(instance, file == null ? null : file.toString());
                } else {
                    field.set(instance, file);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(
                        "Unable to set field " + field.getName() + " on " + sourceTestClass.getName(), e);
            }
        }

    }

    protected Object callRunMethod(final String input, final Method method, final Object instance) {
        try {
            if (method.getParameterCount() > 0) {
                return method.invoke(instance, input);
            } else {
                return method.invoke(instance);
            }
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected Object createInstance() {
        try {
            return sourceTestClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

}
