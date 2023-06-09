/*
 * StreamUtilTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.toolarium.system.command.util.RandomGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;


/**
 * Test the stream util
 * 
 * @author patrick
 */
public class StreamUtilTest {

    /**
     * Insert prefix by each new line
     */
    @Test void insertPrefix() {
        assertInsertPrefix("this\nis\n\na\nsimple\ntest", ">>");
        assertInsertPrefix("this\nis\n\na\nsimple\ntest\n", "->");
        assertInsertPrefix(RandomGenerator.generateRandom(4000), "->");
        
    }

    
    /**
     * Copy stream
     */
    @Test void copyStream() {
        assertCopyStream("this\nis\n\na\nsimple\ntest", "->");
        assertCopyStream("this\nis\n\na\nsimple\ntest\n", "->");
        assertCopyStream(RandomGenerator.generateRandom(4000), "->");
    }
    
    
    /**
     * Assert insert prefix 
     *
     * @param input the input
     * @param prefix the prefix
     */
    private void assertInsertPrefix(String input, String prefix) {
        assertEquals(input.replaceAll("\n", "\n" + prefix), // simple string replacement
                     new String(ProcessStreamUtil.getInstance().insertPrefix(input.getBytes(), input.length(), prefix.getBytes())));
    }    
    

    /**
     * Assert process data
     *
     * @param input the input
     * @param prefix the prefix
     */
    private void assertCopyStream(String input, String prefix) {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        InputStream inputstream = new ByteArrayInputStream(input.getBytes());
        
        try {
            while (inputstream.available() != 0) {
                ProcessStreamUtil.getInstance().pipeAvailableBytes(inputstream, target, prefix, null);
            }
        } catch (IOException e) {
            // NOP
        }
        
        assertEquals(input.replaceAll("\n", "\n" + prefix), // simple string replacement
                     target.toString());
    }
}
