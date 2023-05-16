/*
 * IProcessStreamExceptionHandler.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream;

import java.io.IOException;


/**
 * Defines the stream exception handler
 * 
 * @author patrick
 */
public interface IProcessStreamExceptionHandler {
    
    /**
     * Handle exception
     *
     * @param ex the exception
     */
    void handle(IOException ex);

}
