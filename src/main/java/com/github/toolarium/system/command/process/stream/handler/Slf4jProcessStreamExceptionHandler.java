/*
 * Slf4jProcessStreamExceptionHandler.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.handler;

import com.github.toolarium.system.command.process.stream.IProcessStreamExceptionHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * Implements an SLF4J {@link IProcessStreamExceptionHandler}.
 *  
 * @author patrick
 */
public class Slf4jProcessStreamExceptionHandler implements IProcessStreamExceptionHandler {
    private final Logger logger;
    private final Level level;

    
    /**
     * Constructor for Slf4jProcessStreamExceptionHandler
     */
    public Slf4jProcessStreamExceptionHandler() {
        this(Level.WARN);
    }

    
    /**
     * Constructor for Slf4jProcessStreamExceptionHandler
     * 
     * @param level the log level which is used to write
     */
    public Slf4jProcessStreamExceptionHandler(final Level level) {
        this(LoggerFactory.getLogger(Slf4jProcessStreamExceptionHandler.class), level);
    }

    
    /**
     * Constructor for Slf4jProcessStreamExceptionHandler
     * 
     * @param logger the logger to use
     * @param level the log level which is used to write
     */
    public Slf4jProcessStreamExceptionHandler(final Logger logger, final Level level) {
        this.logger = logger;
        this.level = level;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessStreamExceptionHandler#handle(java.io.IOException)
     */
    @Override
    public void handle(IOException ex) {
        if (Level.DEBUG.equals(level) && logger.isDebugEnabled()) {
            logger.info(ex.getMessage(), ex);
        } else if (Level.INFO.equals(level) && logger.isInfoEnabled()) {
            logger.info(ex.getMessage(), ex);
        } else if (Level.WARN.equals(level) && logger.isWarnEnabled()) {
            logger.warn(ex.getMessage(), ex);
        } else if (Level.ERROR.equals(level) && logger.isErrorEnabled()) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
