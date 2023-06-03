/*
 * AbstractProcess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.impl;

import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import com.github.toolarium.system.command.process.IProcess;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;


/**
 * Implements the abstract process base class {@link IProcess}
 * 
 * @author patrick
 */
public abstract class AbstractProcess implements IProcess {
    private ISystemCommandGroupList systemCommandGroupList;
    private Long pid;
    private Instant startTime;
    private Duration totalCpuDuration;
    private Integer exitValue;   
    
    
    /**
     * Constructor for Process
     *
     * @param systemCommandGroupList the system command group list
     * @param pid the pid
     * @param startTime the start time
     * @param totalCpuDuration the total cpu duration
     * @param exitValue the exist value
     */
    public AbstractProcess(ISystemCommandGroupList systemCommandGroupList, Long pid, Instant startTime, Duration totalCpuDuration, Integer exitValue) {
        this.systemCommandGroupList = systemCommandGroupList;
        this.pid = pid;
        this.startTime = startTime;
        this.totalCpuDuration = totalCpuDuration;
        this.exitValue = exitValue;

        if (this.startTime == null) {
            this.startTime = Instant.now();            
        }
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IProcess#getSystemCommandGroupList()
     */
    @Override
    public ISystemCommandGroupList getSystemCommandGroupList() {
        return systemCommandGroupList;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IProcess#getPid()
     */
    @Override
    public Long getPid() {
        return pid;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IProcess#getStartTime()
     */
    @Override
    public Instant getStartTime() {
        return startTime;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IProcess#getTotalCpuDuration()
     */
    @Override
    public Duration getTotalCpuDuration() {
        return totalCpuDuration;
    }


    /**
     * @see com.github.toolarium.system.command.process.IProcess#getExitValue()
     */
    @Override
    public Integer getExitValue() {
        return exitValue;
    }

    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(systemCommandGroupList, pid, exitValue, startTime);
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
            
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        AbstractProcess other = (AbstractProcess) obj;
        return Objects.equals(systemCommandGroupList, other.systemCommandGroupList) && Objects.equals(pid, other.pid)
                && exitValue == other.exitValue && Objects.equals(startTime, other.startTime);
    }

    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Process [systemCommandGroupList=" + systemCommandGroupList + ", pid=" + getPid() 
               + ", startTime=" + getStartTime() + ", totalCpuDuration=" + getTotalCpuDuration() + ", exitValue=" + getExitValue() + "]";
    }
}
