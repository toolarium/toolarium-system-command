/*
 * ProcessStreamConsumer.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.liveness.impl;

import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Process stream consumer
 * 
 * @author patrick
 */
public class ProcessStreamConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessStreamConsumer.class);
    private InputStream source;
    private IProcessOutputStream processOutputStream;
    private long totalBytes;
    private File optionalSourceFile;
    private InputStream optionalSource;

    
    /**
     * Constructor for ProcessStreamConsumer
     *
     * @param source the input stream
     * @param processOutputStream the process output stream
     * @param optionalSourceFile the optional source file
     */
    public ProcessStreamConsumer(InputStream source, IProcessOutputStream processOutputStream, File optionalSourceFile) {
        this.source = source;
        this.processOutputStream = processOutputStream;
        this.totalBytes = 0;
        this.optionalSourceFile = optionalSourceFile;
        this.optionalSource = null;
    }

    
    /**
     * Pipe the available bytes from the source stream to the target. In case a prefix is defined it will be inserted after a newline.
     * 
     * @return the number of piped data; -1 in case of an error
     */
    public int pipeAvailableBytes() {
        if (processOutputStream != null) {
            int result = 0;
            if (source != null) {
                result = ProcessStreamUtil.getInstance().pipeAvailableBytes(source, processOutputStream);
                if (result == -1) {
                    source = closeInputstream(source);
                } else {
                    addTotalBytes(result);
                }
            }

            prepareOptionalSource();
            if (optionalSource != null) {
                int optionalResult = ProcessStreamUtil.getInstance().pipeAvailableBytes(optionalSource, processOutputStream);
                if (optionalResult == -1) {
                    optionalSource = closeInputstream(optionalSource);
                } else {
                    addTotalBytes(optionalResult);
                    result += optionalResult;
                }
            }

            if (source == null && optionalSource == null) {
                close();
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
        
        source = closeInputstream(source);
        optionalSource = closeInputstream(optionalSource);
        return successfulClosed;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ProcessStreamConsumer [processOutputStream=" + processOutputStream + ", totalBytes=" + totalBytes + "]";
    }


    /**
     * Close input stream
     *
     * @param stream the stream to clsoe
     * @return the closed stream
     */
    private InputStream closeInputstream(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // NOP
            }
        }
        
        return null;
    }


    /**
     * Prepare the optional source
     */
    private void prepareOptionalSource() {
        if (optionalSource == null && optionalSourceFile != null && optionalSourceFile.exists() && optionalSourceFile.length() > 0) {
            try {
                LOG.debug("Prepare optional source file " + optionalSourceFile + " to read.");
                optionalSource = new BufferedInputStream(new FileInputStream(optionalSourceFile));
            } catch (FileNotFoundException e) {
                // NOP
            }
        }
    }
   
    
    /**
     * Add total bytes
     * 
     * @param result the result
     */
    private void addTotalBytes(int result) {
        if (result > 0) {
            totalBytes += result;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Pipped " + result + " byte(s) of stream [" + processOutputStream + "]");
            }
        }
    }
}
