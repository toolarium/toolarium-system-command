/*
 * ProcessOutputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.output;

import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.IProcessStreamExceptionHandler;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Implements the {@link IProcessOutputStream}
 * 
 * @author patrick
 */
public class ProcessOutputStream implements IProcessOutputStream {
    private final String id;
    private OutputStream os;
    private boolean quiet;
    private byte[] linePrefix;
    private IProcessStreamExceptionHandler processStreamExceptionHandler;
    
    
    /**
     * Constructor for ProcessOutputStream
     */
    public ProcessOutputStream() {
        this(System.out, false, (byte[])null, null);
    }

    
    /**
     * Constructor for ProcessOutputStream
     *
     * @param os the output stream
     */
    public ProcessOutputStream(OutputStream os) {
        this(os, false, (byte[])null, null);
    }

    
    /**
     * Constructor for ProcessOutputStream
     *
     * @param os the output stream
     * @param processStreamExceptionHandler the process stream exception handler
     */
    public ProcessOutputStream(OutputStream os, IProcessStreamExceptionHandler processStreamExceptionHandler) {
        this(os, false, (byte[])null, processStreamExceptionHandler);
    }

    
    /**
     * Constructor for ProcessOutputStream
     *
     * @param os the output stream
     * @param quiet true to suppress the output
     * @param linePrefix the prefix to add after every new line or null
     * @param processStreamExceptionHandler the process stream exception handler
     */
    public ProcessOutputStream(OutputStream os, boolean quiet, String linePrefix, IProcessStreamExceptionHandler processStreamExceptionHandler) {
        this(os, quiet, (byte[])null, processStreamExceptionHandler);
        
        if (linePrefix != null) {
            this.linePrefix = linePrefix.getBytes();
        }
    }

    
    /**
     * Constructor for ProcessOutputStream
     *
     * @param os the output stream
     * @param quiet true to suppress the output
     * @param linePrefix the prefix to add after every new line or null
     * @param processStreamExceptionHandler the process stream exception handler
     */
    public ProcessOutputStream(OutputStream os, boolean quiet, byte[] linePrefix, IProcessStreamExceptionHandler processStreamExceptionHandler) {
        this.id = ProcessStreamUtil.getInstance().getId();
        this.os = os;
        this.quiet = quiet;
        this.linePrefix = linePrefix;
        this.processStreamExceptionHandler = processStreamExceptionHandler;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        if (os != null) {
            os.write(b);
        }
    }


    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) throws IOException {
        if (os != null) {
            os.write(b);
        }
    }


    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (os != null) {
            os.write(b);
        }
    }


    /**
     * @see java.io.Flushable#flush()
     */
    @Override
    public void flush() throws IOException {
        if (os != null) {
            os.flush();
        }
    }


    /**
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        if (os != null) {
            try {
                os.flush();
            } catch (IOException e) {
                if (processStreamExceptionHandler != null) {
                    processStreamExceptionHandler.handle(e);
                } else {
                    throw e;
                }
            }
            
            os = null;
        }
    }


    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#isQuiet()
     */
    @Override
    public boolean isQuiet() {
        return quiet;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#getLinePrefix()
     */
    @Override
    public byte[] getLinePrefix() {
        return linePrefix;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#getProcessStreamExceptionHandler()
     */
    @Override
    public IProcessStreamExceptionHandler getProcessStreamExceptionHandler() {
        return processStreamExceptionHandler;
    }
    
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return id;
    }

    
    /**
     * Get the output stream
     *
     * @return the output stream
     */
    protected OutputStream getOutputStream() {
        return os;
    }
}
