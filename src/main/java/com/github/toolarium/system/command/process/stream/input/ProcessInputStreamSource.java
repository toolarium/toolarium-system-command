/*
 * ProcessInputStreamSource.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.input;

import java.io.File;


/**
 * The process input stream source.
 * 
 * @author patrick
 */
public enum ProcessInputStreamSource {
    /** default, taken from parent process */
    INHERIT(null, null),
    
    /** no input */
    DISCARD(null, ""),   
    
    /** pipe input */
    PIPE(null, null),
    
    /** buffer input */
    BUFFER(null, ""),
    
    /** buffer input */
    FILE(null, "");
    
    
    private File file;
    private String buffer;
    
    
    /**
     * Constructor for ProcessInputStreamSource
     *
     * @param file the file
     * @param buffer the buffer
     */
    ProcessInputStreamSource(File file, String buffer) {
        this.file = file;
        this.buffer = buffer;
    }

    
    /**
     * Get the file
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    
    /**
     * Set the file
     *
     * @param file the file
     * @throws IllegalStateException In case if invalid access.
     */
    public void setFile(File file) {
        if (!FILE.equals(this)) {
            throw new IllegalStateException();
        }
        
        this.file = file;
    }


    /**
     * Get the buffer
     *
     * @return the buffer
     */
    public String getBuffer() {
        return buffer;
    }
    
    
    /**
     * Set the buffer
     *
     * @param buffer the buffer
     * @throws IllegalStateException In case if invalid access.
     */
    public void setBuffer(String buffer) {
        if (!BUFFER.equals(this)) {
            throw new IllegalStateException();
        }
        
        this.buffer = buffer;
    }
}
