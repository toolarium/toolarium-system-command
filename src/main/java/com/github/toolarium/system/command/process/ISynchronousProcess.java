/*
 * ISynchronousProcess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process;


/**
 * Defines the synchronous process
 *  
 * @author patrick
 */
public interface ISynchronousProcess extends IProcess {

    /**
     * Get the standard output 
     *
     * @return the standard output
     */
    String getOutput();
    
    
    /**
     * Get the error output 
     *
     * @return the error output
     */
    String getErrorOutput();
}
