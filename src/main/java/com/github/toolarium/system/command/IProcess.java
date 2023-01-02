/*
 * IProcess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;


/**
 * Defines the process
 * 
 * @author patrick
 */
public interface IProcess {
    
    /**
     * Get the process environment
     * 
     * @return the process environment
     */
    IProcessEnvironment getProcessEnvironment();

    
    /**
     * Get the system command of the process.
     *
     * @return the system command of this process
     */
    ISystemCommand getSystemCommand();

        
    /**
     * Get the process id.
     *
     * @return the native process id of the process or null if unknown
     */
    Long getPid();

    
    /**
     * Get the standard input stream connected the process. 
     *
     * @return the standard input stream connected to the process
     */
    OutputStream getInputStream();

    
    /**
     * Get the standard output stream connected the process. 
     *
     * @return the standard output stream connected to the process
     */
    InputStream getOutputStream();

    
    /**
     * Get the standard error stream connected the process. 
     *
     * @return the error stream connected to the process
     */
    InputStream getErrorStream();


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
