/*
 * IProcessLiveness.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.liveness;

import java.time.Instant;

/**
 * Defines the process liveness
 * 
 * @author patrick
 */
public interface IProcessLiveness extends Runnable {
    
    /**
     * Verify if the underlining process is alive 
     *
     * @return true if it is alive
     */
    boolean isAlive();

    
    /**
     * Get the startup time
     *
     * @return the startup time
     */
    Instant getStartupTime();

    
    /**
     * Get the process poll timeout
     *
     * @return the process poll timeout
     */
    long getPollTimeout();
        
    
    /**
     * Get the process
     *
     * @return the process
     */
    java.lang.Process getProcess();
    
}
