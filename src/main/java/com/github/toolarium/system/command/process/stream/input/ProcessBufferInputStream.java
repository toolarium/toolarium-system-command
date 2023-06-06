/*
 * ProcessBufferInputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.input;


/**
 * Implements a buffered process input stream
 * 
 * @author patrick
 */
public class ProcessBufferInputStream extends ProcessStandardInInputStream {
    private ProcessInputStreamSource processInputStreamSource;
    
    
    /**
     * Constructor for ProcessBufferInputStream
     * 
     * @param buffer the buffer as input stream
     */
    public ProcessBufferInputStream(String buffer) {
        processInputStreamSource = ProcessInputStreamSource.BUFFER;
        processInputStreamSource.setBuffer(buffer);
    }
    
    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessInputStream#getProcessInputStreamSource()
     */
    @Override
    public ProcessInputStreamSource getProcessInputStreamSource() {
        return processInputStreamSource;
    }
}
