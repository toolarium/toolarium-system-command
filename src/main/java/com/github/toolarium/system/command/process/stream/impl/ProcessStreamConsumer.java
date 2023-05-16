/*
 * ProcessStreamConsumer.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.impl;

import com.github.toolarium.system.command.process.stream.IProcessInputStream;
import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Process stream consumer
 * 
 * @author patrick
 */
public class ProcessStreamConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessStreamConsumer.class);
    private IProcessInputStream source;
    private IProcessOutputStream processOutputStream;
    private long totalBytes;

    
    /**
     * Constructor for ProcessStreamConsumer
     *
     * @param source the input stream
     * @param processOutputStream the process output stream
     */
    public ProcessStreamConsumer(IProcessInputStream source, IProcessOutputStream processOutputStream) {
        this.source = source;
        this.processOutputStream = processOutputStream;
        this.totalBytes = 0;
    }

    
    /**
     * Pipe the available bytes from the source stream to the target. In case a prefix is defined it will be inserted after a newline.
     * 
     * @return the number of piped data; -1 in case of an error
     */
    public int pipeAvailableBytes() {
        if (processOutputStream != null) {
            int result = ProcessStreamUtil.getInstance().pipeAvailableBytes(source, processOutputStream);
            if (result == -1) {
                close();
            }
            
            totalBytes += result;
            if (LOG.isDebugEnabled()) {
                if (result > 0) { 
                    LOG.debug("Pipped " + result + " byte(s) of stream [" + processOutputStream + "]");
                }
            }

            return result;
        }

        return -1;
    }

    
    /**
     * Pipe the available bytes from the source stream to the target. In case a prefix is defined it will be inserted after a newline.
     * 
     * @return true if the available bytes could be copied without errors.
     */
    public boolean close() {
        boolean successfulClosed = true;
        if (processOutputStream != null) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Close stream [" + processOutputStream + "] (processed " + totalBytes + " bytes(s)).");
                }
                
                processOutputStream.close();
            } catch (IOException e) {
                successfulClosed = false;
            } finally {
                processOutputStream = null;
            }
            
        }
        
        return successfulClosed;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ProcessStreamConsumer [processOutputStream=" + processOutputStream + ", totalBytes=" + totalBytes + "]";
    }
}
