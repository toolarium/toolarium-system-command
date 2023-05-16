/*
 * ProcessLiveness.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.liveness.impl;

import com.github.toolarium.system.command.process.liveness.IProcessLiveness;
import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.impl.ProcessInputStream;
import com.github.toolarium.system.command.process.stream.impl.ProcessStreamConsumer;
import java.io.BufferedInputStream;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements the {@link IProcessLiveness}.
 *  
 * @author patrick
 */
public class ProcessLiveness implements IProcessLiveness, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessLiveness.class);
    private final Process process;
    private ProcessStreamConsumer outputStream;
    private ProcessStreamConsumer errorStream;
    private volatile boolean isAlive;
    private Instant startupTime;
    private long pollTimeout;

    
    /**
     * Constructor for ProcessLiveness
     *
     * @param process the process
     * @param outputStream the output stream
     * @param errorStream the error output stream
     * @param pollTimeout the poll timeout, e.g. 10
     */
    public ProcessLiveness(final Process process, final IProcessOutputStream outputStream, final IProcessOutputStream errorStream, long pollTimeout) {
        this.process = process;
        
        if (outputStream != null) {
            this.outputStream = new ProcessStreamConsumer(new ProcessInputStream(new BufferedInputStream(process.getInputStream())), outputStream);
        }
            
        if (errorStream != null) {
            this.errorStream = new ProcessStreamConsumer(new ProcessInputStream(new BufferedInputStream(process.getErrorStream())), errorStream);
        }
        
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
        LOG.info("Start process liveness thread for process " + process.pid() + "...");
        isAlive = (process != null && process.isAlive()) || ((outputStream != null) || (errorStream != null));

        do {
            outputStream = pipeAvailableBytes(outputStream);
            errorStream = pipeAvailableBytes(errorStream);

            isAlive = (process != null && process.isAlive()) || ((outputStream != null) || (errorStream != null));

            if (isAlive) {
                try {
                    Thread.sleep(getPollTimeout());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (isAlive());
        
        LOG.info("Ended process liveness thread for process " + process.pid() + ".");
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
        return process;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ProcessLiveness [startupTime=" + startupTime + ", pid=" + process.pid() + ", isAlive=" + isAlive() + ", outputStream=" + outputStream + ", errorStream=" + errorStream + ", pollTimeout=" + getPollTimeout() + "]";
    }
}
