/*
 * AbstractProcess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.dto;

import com.github.toolarium.system.command.ISystemCommand;
import com.github.toolarium.system.command.dto.PlatformDependentSystemCommand;
import com.github.toolarium.system.command.process.IProcess;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;


/**
 * Implements the abstract process base class {@link IProcess}
 * 
 * @author patrick
 */
public abstract class AbstractProcess implements IProcess {
    private PlatformDependentSystemCommand platformDependentSystemCommand;
    private Long pid;
    private Instant startTime;
    private Duration totalCpuDuration;
    private Integer exitValue;   
    
    
    /**
     * Constructor for Process
     *
     * @param platformDependentSystemCommand the platform dependent system command
     * @param pid the pid
     * @param startTime the start time
     * @param totalCpuDuration the total cpu duration
     * @param exitValue the exist value
     */
    public AbstractProcess(PlatformDependentSystemCommand platformDependentSystemCommand, Long pid, Instant startTime, Duration totalCpuDuration, Integer exitValue) {
        this.platformDependentSystemCommand = platformDependentSystemCommand;
        this.pid = pid;
        this.startTime = startTime;
        this.totalCpuDuration = totalCpuDuration;
        this.exitValue = exitValue;
        
        if (this.startTime == null) {
            this.startTime = Instant.now();            
        }
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IProcess#getSystemCommandList()
     */
    @Override
    public List<? extends ISystemCommand> getSystemCommandList() {
        return platformDependentSystemCommand.getSystemCommandList();
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
        return Objects.hash(platformDependentSystemCommand, pid, exitValue, startTime);
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
        return Objects.equals(platformDependentSystemCommand, other.platformDependentSystemCommand) && pid == other.pid
                && exitValue == other.exitValue && Objects.equals(startTime, other.startTime);
    }

    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Process [platformDependentSystemCommand=" + platformDependentSystemCommand + ", pid=" + getPid() 
               + ", startTime=" + getStartTime() + ", totalCpuDuration=" + getTotalCpuDuration() + ", exitValue=" + getExitValue() + "]";
    }


    /**
     * Get the temp path
     *
     * @return the temp path or null
     */
    protected Path getTempPath() {
        return platformDependentSystemCommand.getTempPath();
    }
}
