/*
 * Process.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto;

import com.github.toolarium.system.command.IProcess;
import com.github.toolarium.system.command.IProcessEnvironment;
import com.github.toolarium.system.command.ISystemCommand;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;


/**
 * Implements the {@link IProcess}
 * 
 * @author patrick
 */
public class Process implements IProcess {
    private IProcessEnvironment processEnvironment;
    private ISystemCommand systemCommand;
    private Long pid;
    private Instant startTime;
    private Duration totalCpuDuration;
    private Integer exitValue;
    private OutputStream inputStream;
    private InputStream outputStream;
    private InputStream errorStream;
    
    
    
    /**
     * Constructor for Process
     *
     * @param processEnvironment the process environment
     * @param pid the pid
     * @param systemCommand the system command
     * @param startTime the start time
     * @param totalCpuDuration the total cpu duration
     * @param exitValue the exist value
     * @param inputStream the input stream
     * @param outputStream the output stream
     * @param errorStream the error stream
     */
    public Process(IProcessEnvironment processEnvironment, ISystemCommand systemCommand, Long pid, Instant startTime, Duration totalCpuDuration, Integer exitValue, 
                   OutputStream inputStream, InputStream outputStream, InputStream errorStream) {
        this.processEnvironment = processEnvironment;
        this.systemCommand = systemCommand;
        this.pid = pid;
        this.startTime = startTime;
        this.totalCpuDuration = totalCpuDuration;
        this.exitValue = exitValue;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
        
        if (this.startTime == null) {
            this.startTime = Instant.now();            
        }
    }

    
    /**
     * Constructor for Process
     *
     * @param processEnvironment the process environment
     * @param systemCommand the system command
     */
    protected Process(IProcessEnvironment processEnvironment, ISystemCommand systemCommand) {
        this.processEnvironment = processEnvironment;
        this.systemCommand = systemCommand;
        //this.pid = pid;
        //this.startTime = startTime;
        //this.totalCpuDuration = totalCpuDuration;
        //this.exitValue = exitValue;
        //this.inputStream = inputStream;
        //this.outputStream = outputStream;
        //this.errorStream = errorStream;
    }

    
    /**
     * @see com.github.toolarium.system.command.IProcess#getProcessEnvironment()
     */
    @Override
    public IProcessEnvironment getProcessEnvironment() {
        return processEnvironment;
    }


    /**
     * @see com.github.toolarium.system.command.IProcess#getSystemCommand()
     */
    @Override
    public ISystemCommand getSystemCommand() {
        return systemCommand;
    }

    
    /**
     * @see com.github.toolarium.system.command.IProcess#getPid()
     */
    @Override
    public Long getPid() {
        return pid;
    }

    
    /**
     * @see com.github.toolarium.system.command.IProcess#getInputStream()
     */
    @Override
    public OutputStream getInputStream() {
        return inputStream;
    }
    
    
    /**
     * @see com.github.toolarium.system.command.IProcess#getOutputStream()
     */
    @Override
    public InputStream getOutputStream() {
        return outputStream;
    }

    
    /**
     * @see com.github.toolarium.system.command.IProcess#getErrorStream()
     */
    @Override
    public InputStream getErrorStream() {
        return errorStream;
    }

    
    /**
     * @see com.github.toolarium.system.command.IProcess#getStartTime()
     */
    @Override
    public Instant getStartTime() {
        return startTime;
    }

    
    /**
     * @see com.github.toolarium.system.command.IProcess#getTotalCpuDuration()
     */
    @Override
    public Duration getTotalCpuDuration() {
        return totalCpuDuration;
    }


    /**
     * @see com.github.toolarium.system.command.IProcess#getExitValue()
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
        return Objects.hash(processEnvironment, systemCommand, pid, exitValue, startTime);
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
        
        Process other = (Process) obj;
        return Objects.equals(processEnvironment, other.processEnvironment) && Objects.equals(systemCommand, other.systemCommand) && pid == other.pid
                && exitValue == other.exitValue && Objects.equals(startTime, other.startTime);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Process [processEnvironment=" + processEnvironment + ", systemCommand=" + systemCommand + ", pid=" + pid 
                + ", startTime=" + startTime + ", totalCpuDuration=" + totalCpuDuration + ", exitValue=" + exitValue + "]";
    }
}
