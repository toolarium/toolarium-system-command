/*
 * ProcessDiscardInputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.input;


/**
 * Implements an empty / discarded process input stream
 * 
 * @author patrick
 */
public class ProcessDiscardInputStream extends ProcessBufferInputStream {
    
    /**
     * Constructor for ProcessBufferInputStream
     */
    public ProcessDiscardInputStream() {
        super("");
    }
}
