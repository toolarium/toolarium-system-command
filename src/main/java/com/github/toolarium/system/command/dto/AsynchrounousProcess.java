/*
 * AsynchrounousProcess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto;

import com.github.toolarium.system.command.IAsynchronousProcess;
import com.github.toolarium.system.command.IProcessEnvironment;
import com.github.toolarium.system.command.ISystemCommand;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;


/**
 * Implements the {@link IAsynchronousProcess}.
 * 
 * @author patrick
 */
public class AsynchrounousProcess extends Process implements IAsynchronousProcess {
    private java.lang.Process process;
    private Instant startupTime;

    
    /**
     * Constructor for AsynchrounousProcess
     *
     * @param processEnvironment the process environment
     * @param systemCommand the system command
     * @param pid the pid
     * @param startTime the start time
     * @param totalCpuDuration the total cpu duration
     * @param exitValue the exist value
     * @param inputStream the input stream
     * @param outputStream the output stream
     * @param errorStream the error stream
     */
    private AsynchrounousProcess(IProcessEnvironment processEnvironment, ISystemCommand systemCommand, Long pid, Instant startTime, Duration totalCpuDuration, Integer exitValue,
                                 OutputStream inputStream, InputStream outputStream, InputStream errorStream) {
        super(processEnvironment, systemCommand, pid, startTime, totalCpuDuration, exitValue, inputStream, outputStream, errorStream);
    }

    
    /**
     * Constructor for AsynchrounousProcess
     *
     * @param processEnvironment the process environment
     * @param systemCommand the system command
     * @param process the process
     */
    public AsynchrounousProcess(IProcessEnvironment processEnvironment, ISystemCommand systemCommand, java.lang.Process process) {
        super(processEnvironment, systemCommand);
        this.process = process;
        
        if (getProcess() == null || getProcess().info() == null || getProcess().info().startInstant().isEmpty()) {
            startupTime = Instant.now();            
        } else {
            startupTime = getProcess().info().startInstant().get();
        }
    }
    
    
    /**
     * @see com.github.toolarium.system.command.IProcess#getPid()
     */
    @Override
    public Long getPid() {
        if (getProcess() == null) {
            return null;
        }
        
        return getProcess().pid();
    }

    
    /**
     * @see com.github.toolarium.system.command.IProcess#getInputStream()
     */
    @Override
    public OutputStream getInputStream() {
        if (getProcess() == null) {
            return null;
        }
        
        return getProcess().getOutputStream();
    }
    
    
    /**
     * @see com.github.toolarium.system.command.IProcess#getOutputStream()
     */
    @Override
    public InputStream getOutputStream() {
        if (getProcess() == null) {
            return null;
        }
        
        return getProcess().getInputStream();
    }

    
    /**
     * @see com.github.toolarium.system.command.IProcess#getErrorStream()
     */
    @Override
    public InputStream getErrorStream() {
        if (getProcess() == null) {
            return null;
        }
        
        return getProcess().getErrorStream();
    }

    
    /**
     * @see com.github.toolarium.system.command.IProcess#getStartTime()
     */
    @Override
    public Instant getStartTime() {
        return startupTime;
    }

    
    /**
     * @see com.github.toolarium.system.command.IProcess#getTotalCpuDuration()
     */
    @Override
    public Duration getTotalCpuDuration() {
        if (getProcess() == null || getProcess().info() == null || getProcess().info().totalCpuDuration().isEmpty()) {
            return null;
        }
 
        return getProcess().info().totalCpuDuration().get();
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.Process#getExitValue()
     */
    @Override
    public Integer getExitValue() {
        if (getProcess() == null) {
            return null;
        }
        
        return getProcess().exitValue();
    }

    
    /**
     * @see com.github.toolarium.system.command.IAsynchronousProcess#waitFor()
     */
    @Override
    public int waitFor() throws InterruptedException {
        if (getProcess() == null) {
            return -1;
        }
        
        return getProcess().waitFor();
    }

    
    /**
     * @see com.github.toolarium.system.command.IAsynchronousProcess#waitFor(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
        if (getProcess() == null) {
            return true;
        }
        
        return getProcess().waitFor(timeout, unit);
    }

    
    /**
     * @see com.github.toolarium.system.command.IAsynchronousProcess#isAlive()
     */
    @Override
    public boolean isAlive() {
        if (getProcess() == null) {
            return false;
        }
        
        return getProcess().isAlive();
    }

    
    /**
     * @see com.github.toolarium.system.command.IAsynchronousProcess#tryDestroy()
     */
    @Override
    public void tryDestroy() {
        if (getProcess() == null) {
            return;
        }
        
        getProcess().destroy();
    }

    
    /**
     * @see com.github.toolarium.system.command.IAsynchronousProcess#destroy()
     */
    @Override
    public void destroy() {
        if (getProcess() == null) {
            return;
        }

        getProcess().destroyForcibly();
    }

    
    /**
     * @see com.github.toolarium.system.command.IAsynchronousProcess#getProcessHandle()
     */
    @Override
    public ProcessHandle getProcessHandle() {
        if (getProcess() == null) {
            return null;
        }
        
        return getProcess().toHandle();
    }
    
    
    /**
     * Get the process
     *
     * @return the process
     */
    protected java.lang.Process getProcess() {
        return process;
    }
}
