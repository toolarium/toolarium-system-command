/*
 * Slf4jProcessOutputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.output;

import com.github.toolarium.system.command.dto.group.ISystemCommandGroup;
import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.IProcessStreamExceptionHandler;
import com.github.toolarium.system.command.process.stream.handler.Slf4jProcessStreamExceptionHandler;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * Implements a {@link IProcessOutputStream} which suppports to write to SLF4J.
 *  
 * @author patrick
 */
public class Slf4jProcessOutputStream implements IProcessOutputStream {
    private final String id;
    private final Logger logger;
    private final Level level;
    private byte[] linePrefix;
    private IProcessStreamExceptionHandler processStreamExceptionHandler;
    private ByteArrayOutputStream stream;
    private String commandLine;

    
    /**
     * Constructor for Slf4jProcessOutputStream
     */
    public Slf4jProcessOutputStream() {
        this(Level.INFO);
    }

    
    /**
     * Constructor for Slf4jProcessOutputStream
     *
     * @param level the log level which is used to write
     */
    public Slf4jProcessOutputStream(final Level level) {
        this(level, null);
    }

    
    /**
     * Constructor for Slf4jProcessOutputStream
     *
     * @param level the log level which is used to write
     * @param linePrefix the prefix to add after every new line or null
     */
    public Slf4jProcessOutputStream(final Level level, String linePrefix) {
        this(LoggerFactory.getLogger(Slf4jProcessOutputStream.class), level, linePrefix);
    }

    
    /**
     * Constructor for Slf4jProcessOutputStream
     *
     * @param logger the logger to use
     * @param level the log level which is used to write
     */
    public Slf4jProcessOutputStream(final Logger logger, final Level level) {
        this(logger, level, null);
    }

    
    /**
     * Constructor for Slf4jProcessOutputStream
     *
     * @param logger the logger to use
     * @param level the log level which is used to write
     * @param linePrefix the prefix to add after every new line or null
     */
    public Slf4jProcessOutputStream(final Logger logger, final Level level, String linePrefix) {
        this(logger, level, linePrefix, new Slf4jProcessStreamExceptionHandler(Level.DEBUG));
    }

    
    /**
     * Constructor for Slf4jProcessOutputStream
     *
     * @param logger the logger to use
     * @param level the log level which is used to write
     * @param linePrefix the prefix to add after every new line or null
     * @param processStreamExceptionHandler the process stream exception handler
     */
    public Slf4jProcessOutputStream(final Logger logger, final Level level, String linePrefix, IProcessStreamExceptionHandler processStreamExceptionHandler) {
        this.id = ProcessStreamUtil.getInstance().getId();
        this.logger = logger;
        this.level = level;
        
        if (linePrefix != null) {
            this.linePrefix = linePrefix.getBytes();
        }
        
        this.processStreamExceptionHandler = processStreamExceptionHandler;
        this.stream = null;
        this.commandLine = null;
    }


    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        write(new byte[] {(byte) b }, 0, 1);
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (b == null || b.length == 0 || len <= 0 || off >= b.length) {
            return;
        }

        int offset = off;
        if (offset < 0) {
            offset = 0;
        }

        int length = len;
        if ((length + offset) > b.length) {
            length = b.length - offset;
        }

        int newline = -1;
        for (int i = offset; i < (length + offset); i++) {
            if (b[i] == '\n') {
                newline = i;
            }
        }
        
        if (stream == null) {
            stream = new ByteArrayOutputStream();
            if (commandLine != null && !commandLine.isEmpty()) {
                stream.write(("Execute [" + commandLine + "]:\n").getBytes());
            }
            
            if (linePrefix != null) {
                stream.write(linePrefix);
            }
        }
        
        if (newline < 0) {
            stream.write(b, offset, length);
        } else if (offset < newline) {
            stream.write(b, offset, newline);
            
            if (linePrefix != null) {
                stream.write(linePrefix);
            }
            
            write(stream.toString());
            stream = new ByteArrayOutputStream();
            write(b, newline, length);
        }
    }


    /**
     * Write a message
     *
     * @param msg the message
     */
    public void write(String msg) {
        if (Level.DEBUG.equals(level) && logger.isDebugEnabled()) {
            logger.info(msg);
        } else if (Level.INFO.equals(level) && logger.isInfoEnabled()) {
            logger.info(msg);
        } else if (Level.WARN.equals(level) && logger.isWarnEnabled()) {
            logger.warn(msg);
        } else if (Level.ERROR.equals(level) && logger.isErrorEnabled()) {
            logger.error(msg);
        }
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
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#start(com.github.toolarium.system.command.dto.ISystemCommand)
     */
    @Override
    public void start(ISystemCommandGroup systemCommandGroup) {
        if (systemCommandGroup == null) {
            return;
        }
        
        commandLine = systemCommandGroup.toString(true);
    }

    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return id;
    }


    /**
     * @see java.io.Flushable#flush()
     */
    @Override
    public void flush() throws IOException {
    }

    
    /**
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        if (stream != null && stream.toByteArray().length > 0) {
            write(stream.toString());
            stream.close();
            stream = null;
        }
    }
}
