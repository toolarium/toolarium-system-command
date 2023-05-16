/*
 * IProcess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process;

import com.github.toolarium.system.command.ISystemCommand;
import java.time.Duration;
import java.time.Instant;
import java.util.List;


/**
 * Defines the process
 * 
 * @author patrick
 */
public interface IProcess {
    
    /**
     * Get the system command list.
     *
     * @return the system command list of this process
     */
    List<? extends ISystemCommand> getSystemCommandList();

        
    /**
     * Get the process id.
     *
     * @return the native process id of the process or null if unknown
     */
    Long getPid();

    
    /**
     * Get the start time of the process.
     *
     * @return the start time of the process
     */
    Instant getStartTime();

    
    /**
     * Get the total cpu time accumulated of the process.
     *
     * @return the accumulated total cpu time
     */
    Duration getTotalCpuDuration();

    
    /**
     * Get the exit value for the process.
     *
     * @return the exit value of the process, by convention, the value {@code 0} indicates normal termination. It return null if it has not exit by now.
     * @throws IllegalThreadStateException if the process has not yet terminated
     */
    Integer getExitValue();
}
