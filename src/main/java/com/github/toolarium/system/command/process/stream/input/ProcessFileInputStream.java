/*
 * ProcessFileInputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.input;

import com.github.toolarium.system.command.process.stream.IProcessInputStream;
import java.io.File;


/**
 * Implements the {@link IProcessInputStream} read from file.
 *  
 * @author patrick
 */
public class ProcessFileInputStream implements IProcessInputStream {
    private ProcessInputStreamSource processInputStreamSource;
    
    
    /**
     * Constructor for ProcessFileInputStream
     * 
     * @param file the file as input stream
     */
    public ProcessFileInputStream(File file) {
        processInputStreamSource = ProcessInputStreamSource.FILE;
        processInputStreamSource.setFile(file);
    }
    
    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessInputStream#getProcessInputStreamSource()
     */
    @Override
    public ProcessInputStreamSource getProcessInputStreamSource() {
        return processInputStreamSource;
    }
}
