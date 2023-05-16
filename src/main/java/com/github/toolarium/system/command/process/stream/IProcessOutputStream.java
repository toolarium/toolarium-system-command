/*
 * IProcessOutputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;


/**
 * The process output stream
 *  
 * @author patrick
 */
public interface IProcessOutputStream extends Closeable, Flushable {

    /**
     * Write to the stream
     * 
     * @param b the byte to write
     * @throws IOException in case of an I/O error
     */
    void write(int b) throws IOException;

    
    /**
     * Write to the stream
     * 
     * @param b the bytes to write
     * @throws IOException in case of an I/O error
     */
    void write(byte[] b) throws IOException;

    
    /**
     * Write to the stream
     * 
     * @param b the bytes to write
     * @param off the offset
     * @param len the length
     * @throws IOException in case of an I/O error
     */
    void write(byte[] b, int off, int len) throws IOException;


    /**
     * Verify if the stream is handled quiet
     *
     * @return true if it is quiet
     */
    boolean isQuiet();
    
    
    /**
     * Get the line preifx
     *
     * @return the line preifx
     */
    byte[] getLinePrefix();
    
    
    /**
     * Get the process stream exception handler
     *
     * @return the process stream exception handler
     */
    IProcessStreamExceptionHandler getProcessStreamExceptionHandler();
    
}
