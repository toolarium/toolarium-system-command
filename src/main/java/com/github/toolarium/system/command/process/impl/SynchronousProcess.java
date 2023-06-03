/*
 * SynchronousProcess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.impl;

import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import com.github.toolarium.system.command.process.ISynchronousProcess;
import java.time.Duration;
import java.time.Instant;


/**
 * Implements the {@link ISynchronousProcess}.
 *  
 * @author patrick
 */
public class SynchronousProcess extends AbstractProcess implements ISynchronousProcess {
    private final String out;
    private final String errorOut;

    
    /**
     * Constructor for Process
     *
     * @param systemCommandGroupList the system command group list
     * @param pid the pid
     * @param startTime the start time
     * @param totalCpuDuration the total cpu duration
     * @param exitValue the exist value
     * @param out the output
     * @param errorOut the error output
     */
    public SynchronousProcess(final ISystemCommandGroupList systemCommandGroupList, 
                              final Long pid, 
                              final Instant startTime, 
                              final Duration totalCpuDuration, 
                              final Integer exitValue,
                              final String out,
                              final String errorOut) {
        super(systemCommandGroupList, pid, startTime, totalCpuDuration, exitValue);
        this.out = out;
        this.errorOut = errorOut;
    }


    /**
     * @see com.github.toolarium.system.command.process.ISynchronousProcess#getOutput()
     */
    @Override
    public String getOutput() {
        return out;
    }


    /**
     * @see com.github.toolarium.system.command.process.ISynchronousProcess#getErrorOutput()
     */
    @Override
    public String getErrorOutput() {
        return errorOut;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.impl.AbstractProcess#toString()
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
