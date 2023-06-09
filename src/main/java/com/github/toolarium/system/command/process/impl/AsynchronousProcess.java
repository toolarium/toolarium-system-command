/*
 * AsynchrounousProcess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.impl;

import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.liveness.IProcessLiveness;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements the {@link IAsynchronousProcess}.
 * 
 * @author patrick
 */
public class AsynchronousProcess extends AbstractProcess implements IAsynchronousProcess, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(AsynchronousProcess.class);
    private IProcessLiveness processLiveness;

    
    /**
     * Constructor for AsynchrounousProcess
     *
     * @param systemCommandGroupList the system command group list
     * @param pid the pid
     * @param startTime the start time
     * @param totalCpuDuration the total cpu duration
     * @param exitValue the exist value
     */
    private AsynchronousProcess(ISystemCommandGroupList systemCommandGroupList, Long pid, Instant startTime, Duration totalCpuDuration, Integer exitValue) {
        super(systemCommandGroupList, pid, startTime, totalCpuDuration, exitValue);
    }

    
    /**
     * Constructor for AsynchrounousProcess
     *
     * @param systemCommandGroupList the system command group list
     * @param processLiveness the process liveness
     */
    public AsynchronousProcess(ISystemCommandGroupList systemCommandGroupList, IProcessLiveness processLiveness) {
        this(systemCommandGroupList, null, null, null, null);
        this.processLiveness = processLiveness;
    }
    
    
    /**
     * @see com.github.toolarium.system.command.process.IProcess#getPid()
     */
    @Override
    public Long getPid() {
        if (getProcess() == null) {
            return null;
        }
        
        return getProcess().pid();
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IProcess#getStartTime()
     */
    @Override
    public Instant getStartTime() {
        if (getProcessLiveness() == null) {
            return null;
        }
        
        return getProcessLiveness().getStartupTime();
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IProcess#getTotalCpuDuration()
     */
    @Override
    public Duration getTotalCpuDuration() {
        if (getProcess() == null || getProcess().info() == null || getProcess().info().totalCpuDuration().isEmpty()) {
            return null;
        }
 
        return getProcess().info().totalCpuDuration().get();
    }

    
    /**
     * @see com.github.toolarium.system.command.process.impl.AbstractProcess#getExitValue()
     */
    @Override
    public Integer getExitValue() {
        if (getProcess() == null || getProcessLiveness() == null) {
            return null;
        }
        
        try {
            return getProcess().exitValue();
        } catch (IllegalThreadStateException e) {
            // this occures in case the thread is not finalised
            return null;
        }
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IAsynchronousProcess#waitFor()
     */
    @Override
    public int waitFor() throws InterruptedException {
        if (getProcess() == null || getProcessLiveness() == null) {
            return -1;
        }
        
        int result = getProcess().waitFor();
        while (isAlive()) {
            Thread.sleep(getProcessLiveness().getPollTimeout());
        }
        
        return result;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IAsynchronousProcess#waitFor(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
        if (getProcess() == null || getProcessLiveness() == null) {
            return true;
        }
        
        boolean result = getProcess().waitFor(timeout, unit);
        while (isAlive()) {
            Thread.sleep(getProcessLiveness().getPollTimeout());
        }

        return result;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IAsynchronousProcess#isAlive()
     */
    @Override
    public boolean isAlive() {
        if (getProcess() == null || getProcessLiveness() == null) {
            return false;
        }
        
        return getProcessLiveness().isAlive();
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IAsynchronousProcess#tryDestroy()
     */
    @Override
    public void tryDestroy() {
        if (getProcess() == null) {
            return;
        }
        
        getProcess().destroy();
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IAsynchronousProcess#destroy()
     */
    @Override
    public void destroy() {
        if (getProcess() == null) {
            return;
        }

        getProcess().destroyForcibly();
    }


    /**
     * @see com.github.toolarium.system.command.process.IAsynchronousProcess#getInputStream()
     */
    @Override
    public OutputStream getInputStream() {
        return getProcess().getOutputStream();
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IAsynchronousProcess#getProcessHandle()
     */
    @Override
    public ProcessHandle getProcessHandle() {
        if (getProcess() == null) {
            return null;
        }
        
        return getProcess().toHandle();
    }

    
    /**
     * @see com.github.toolarium.system.command.process.IAsynchronousProcess#close()
     */
    @Override
    public void close()  {
        if (getProcess().isAlive()) {
            try {
                getProcess().waitFor();
            } catch (InterruptedException e) {
                // NOP
            }
        }
        
        if ((processLiveness.getLockTimeout() == null || Instant.now().isAfter(processLiveness.getLockTimeout()))) {
            Path scriptPath = processLiveness.getScriptPath();
            if (scriptPath != null && scriptPath.toFile().exists()) {
                LOG.debug("Delete script path [" + scriptPath + "]...");
                ProcessStreamUtil.getInstance().deleteDirectory(scriptPath);
            }
        }
    }

    
    /**
     * Get the process liveness
     *
     * @return the process liveness
     */
    protected IProcessLiveness getProcessLiveness() {
        return processLiveness;
    }
    
    
    /**
     * @see com.github.toolarium.system.command.process.impl.AbstractProcess#toString()
     */
    @Override
    public String toString() {
        return super.toString();
    }

    
    /**
     * Get the process
     *
     * @return the process
     */
    protected java.lang.Process getProcess() {
        if (getProcessLiveness() == null) {
            return null;
        }
        
        return getProcessLiveness().getProcess();
    }
}
