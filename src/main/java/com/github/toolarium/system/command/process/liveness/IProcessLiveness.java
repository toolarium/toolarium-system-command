/*
 * IProcessLiveness.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.liveness;

import java.nio.file.Path;
import java.time.Instant;

/**
 * The process liveness
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
    
    
    /**
     * Get the process id
     *
     * @return the process id
     */
    Long getProcessId();

    
    /**
     * Cleanup the script path after process end
     *
     * @return true to cleanup automated
     */
    boolean autoCleanupScriptPath();

    
    /**
     * Get the script path of this process or null if there is no folder
     *
     * @return the script folder
     */
    Path getScriptPath();
}
