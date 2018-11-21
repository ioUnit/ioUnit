package com.github.iounit.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestParms {

    @Test
    public void testNoParm(){
        assertEquals("foo/bar",Parms.deParam("foo/bar"));
    }
    @Test
    public void test1Parm(){
        System.setProperty("bar", "BAZ");
        assertEquals("foo/BAZ",Parms.deParam("foo/${bar}"));
    }
    @Test
    public void test1ParmMissing(){
        assertEquals("foo/",Parms.deParam("foo/${bar}"));
    }
    @Test
    public void test1ParmMissingWDefault(){
        assertEquals("foo/BOT",Parms.deParam("foo/${bar:BOT}"));
    }}

