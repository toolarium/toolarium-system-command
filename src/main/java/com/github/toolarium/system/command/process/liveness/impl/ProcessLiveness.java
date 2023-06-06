/*
 * ProcessLiveness.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.liveness.impl;

import com.github.toolarium.system.command.process.liveness.IProcessLiveness;
import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.BufferedInputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements the {@link IProcessLiveness}.
 *  
 * @author patrick
 */
public class ProcessLiveness implements IProcessLiveness, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessLiveness.class);
    private String id;
    private final List<Process> processList;
    private ProcessStreamConsumer outputStream;
    private ProcessStreamConsumer errorStream;
    private volatile boolean isAlive;
    private Instant startupTime;
    private long pollTimeout;
    private Path scriptPath;
    private boolean autoCleanupScriptPath;

    
    /**
     * Constructor for ProcessLiveness
     * 
     * @param id the id 
     * @param processList the process list
     * @param outputStream the output stream
     * @param errorStream the error output stream
     * @param scriptPath the script path
     * @param autoCleanupScriptPath true to cleanup automated
     * @param pollTimeout the poll timeout, e.g. 10
     */
    public ProcessLiveness(String id, 
                           final List<Process> processList, 
                           final IProcessOutputStream outputStream, 
                           final IProcessOutputStream errorStream,
                           final Path scriptPath,
                           final boolean autoCleanupScriptPath,
                           long pollTimeout) {
        this.id = id;
        this.processList = processList;
        Process process = getProcess();

        if (outputStream != null && process != null) {
            this.outputStream = new ProcessStreamConsumer(new BufferedInputStream(process.getInputStream()), outputStream);
        }
            
        if (errorStream != null && process != null) {
            this.errorStream = new ProcessStreamConsumer(new BufferedInputStream(process.getErrorStream()), errorStream);
        }
        
        this.scriptPath = scriptPath;
        this.autoCleanupScriptPath = autoCleanupScriptPath;
        this.pollTimeout = pollTimeout;
        this.isAlive = true;
        if (process == null || process.info() == null || process.info().startInstant().isEmpty()) {
            startupTime = Instant.now();            
        } else {
            startupTime = getProcess().info().startInstant().get();
        }
    }


    /**
     * @see com.github.toolarium.system.command.process.liveness.IProcessLiveness#isAlive()
     */
    @Override
    public boolean isAlive() {
        return isAlive;
    }


    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        Process process = getProcess();
        LOG.info("Start process liveness thread for process " + process.pid() + "...");
        isAlive = (process != null && process.isAlive()) || ((outputStream != null) || (errorStream != null));

        do {
            outputStream = pipeAvailableBytes(outputStream);
            errorStream = pipeAvailableBytes(errorStream);

            isAlive = (process != null && process.isAlive()) || (outputStream != null) || (errorStream != null);

            if (isAlive) {
                try {
                    Thread.sleep(getPollTimeout());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (isAlive());
        
        if (scriptPath != null) {
            LOG.info("Process ended (id:" + id + ", pid:" + process.pid() + ", script:" + scriptPath + ")");
        } else {
            LOG.info("Process ended (id:" + id + ", pid:" + process.pid() + ")");
        }
        
        if (autoCleanupScriptPath && scriptPath != null && scriptPath.toFile().exists()) {
            LOG.debug("Delete script path [" + scriptPath + "]...");
            ProcessStreamUtil.getInstance().deleteDirectory(scriptPath);
        }
    }

    
    /**
     * Read available bytes
     * 
     * @param processStreamConsumer the process consumer
     * @return the process consumer 
     */
    private ProcessStreamConsumer pipeAvailableBytes(ProcessStreamConsumer processStreamConsumer) {
        if (processStreamConsumer == null) {
            return processStreamConsumer;
        }
        
        if (processStreamConsumer.pipeAvailableBytes() >= 0) {
            return processStreamConsumer;
        }
        
        processStreamConsumer.close();
        return null;
    }


    /**
     * @see com.github.toolarium.system.command.process.liveness.IProcessLiveness#getStartupTime()
     */
    @Override
    public Instant getStartupTime() {
        return startupTime;
    }


    /**
     * @see com.github.toolarium.system.command.process.liveness.IProcessLiveness#getPollTimeout()
     */
    @Override
    public long getPollTimeout() {
        return pollTimeout;
    }


    /**
     * @see com.github.toolarium.system.command.process.liveness.IProcessLiveness#getProcess()
     */
    @Override
    public Process getProcess() {
        if (processList.size() <= 0) {
            return null;
        }
        
        if (processList.size() > 1) {
            for (Process process : processList) {
                if (process.isAlive()) {
                    return process;
                }
            }
        }
        
        return processList.get(processList.size() - 1);
    }

    
    /**
     * @see com.github.toolarium.system.command.process.liveness.IProcessLiveness#getProcessId()
     */
    @Override
    public Long getProcessId() {
        if (getProcess() != null) {
            return getProcess().pid();
        }
        
        return null;
    }
    
    
    /**
     * @see com.github.toolarium.system.command.process.liveness.IProcessLiveness#autoCleanupScriptPath()
     */
    @Override
    public boolean autoCleanupScriptPath() {
        return autoCleanupScriptPath;
    }


    /**
     * @see com.github.toolarium.system.command.process.liveness.IProcessLiveness#getScriptPath()
     */
    @Override
    public Path getScriptPath() {
        return scriptPath;
    }
    

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ProcessLiveness [startupTime=" + startupTime + ", isAlive=" + isAlive() + ", pid=" + getProcessId() +  " (" + processList + ")"
                + ", outputStream=" + outputStream + ", errorStream=" + errorStream + ", pollTimeout=" + pollTimeout
                + ", scriptPath=" + scriptPath + ", autoCleanupScriptPath=" + autoCleanupScriptPath + "]";
    }
}
