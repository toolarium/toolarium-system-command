/*
 * ScriptUtilTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


/**
 * Test the {@link ProcessBuilderUtil}.
 *  
 * @author patrick
 */
public class ScriptUtilTest {

    /**
     * Test the prepare string
     */
    @Test public void testPrepareString() {
        assertEquals("....", ScriptUtil.getInstance().prepareString(".", 4));
        assertEquals("####", ScriptUtil.getInstance().prepareString("#", 4));
        assertEquals(":::::", ScriptUtil.getInstance().prepareString("::", 5));
    }
}
