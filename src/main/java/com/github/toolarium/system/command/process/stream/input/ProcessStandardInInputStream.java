/*
 * ProcessInputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.input;

import com.github.toolarium.system.command.process.stream.IProcessInputStream;


/**
 * Implements the {@link IProcessInputStream}.
 *  
 * @author patrick
 */
public class ProcessStandardInInputStream implements IProcessInputStream {
    private ProcessInputStreamSource processInputStreamSource;
    
    
    /**
     * Constructor for ProcessStandardInInputStream
     */
    public ProcessStandardInInputStream() {
        processInputStreamSource = ProcessInputStreamSource.INHERIT;
    }
    
    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessInputStream#getProcessInputStreamSource()
     */
    @Override
    public ProcessInputStreamSource getProcessInputStreamSource() {
        return processInputStreamSource;
    }
}
