/*
 * ProcessBufferOutputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.output;

import com.github.toolarium.system.command.process.stream.IProcessStreamExceptionHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Implements a buffered process output stream
 *  
 * @author patrick
 */
public class ProcessBufferOutputStream extends ProcessOutputStream {
    
    /**
     * Constructor for ProcessBufferOutputStream
     */
    public ProcessBufferOutputStream() {
        super(new ByteArrayOutputStream());
    }

    
    /**
     * Constructor for ProcessBufferOutputStream
     *
     * @param linePrefix the prefix to add after every new line or null
     */
    public ProcessBufferOutputStream(String linePrefix) {
        super(new ByteArrayOutputStream(), linePrefix, null);
    }

    
    /**
     * Constructor for ProcessBufferOutputStream
     *
     * @param linePrefix the prefix to add after every new line or null
     * @param processStreamExceptionHandler the process stream exception handler
     */
    public ProcessBufferOutputStream(String linePrefix, IProcessStreamExceptionHandler processStreamExceptionHandler) {
        super(new ByteArrayOutputStream(), linePrefix, processStreamExceptionHandler);
    }

    
    /**
     * Tests if this string starts with the specified prefix.
     * 
     * @param prefix the prefix.
     * @return true if the prefix exists
     */
    public boolean startsWith(String prefix) {
        return toString().startsWith(prefix);
    }

    
    /**
     * Search in the current collected buffer for a specific string.
     * 
     * @param str to search
     * @return the position or -1 if it could not be found
     */
    public int indexOf(String str) {
        return toString().indexOf(str);
    }


    /**
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        // NOP
    }

    
    /**
     * Get the buffered output
     *
     * @return the buffered output
     */
    public byte[] getBuffer() {
        return ((ByteArrayOutputStream)getOutputStream()).toByteArray();
    }
    

    /**
     * @see com.github.toolarium.system.command.process.stream.output.ProcessOutputStream#toString()
     */
    @Override
    public String toString() {
        return ((ByteArrayOutputStream)getOutputStream()).toString();
    }
}
