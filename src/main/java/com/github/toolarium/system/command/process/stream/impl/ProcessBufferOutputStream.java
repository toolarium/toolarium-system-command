/*
 * ProcessBufferOutputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.impl;

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
     * @see com.github.toolarium.system.command.process.stream.impl.AbstractProcessStream#toString()
     */
    @Override
    public String toString() {
        return ((ByteArrayOutputStream)getOutputStream()).toString();
    }
}
