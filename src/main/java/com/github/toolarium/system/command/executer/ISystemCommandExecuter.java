/*
 * ISystemCommandExecuter.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.executer;

import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.ISynchronousProcess;
import com.github.toolarium.system.command.process.impl.ProcessInputStreamSource;
import com.github.toolarium.system.command.process.stream.IProcessOutputStream;


/**
 * The system command executer.
 *
 * @author patrick
 */
public interface ISystemCommandExecuter {
    
    /**
     * Start a new command synchronous process. You will get back the terminated process.
     *
     * @return the terminated process
     */
    ISynchronousProcess runSynchronous();

    
    /**
     * Start a new command synchronous process. You will get back the terminated process. In case it could not be
     * finished in the given time it will be destroyed.
     *
     * @param numberOfSecondsToWait the number of seconds to wait. If the value less or equal 0, it waits until it is executed
     * @return the process
     */
    ISynchronousProcess runSynchronous(int numberOfSecondsToWait);

    
    /**
     * Start a new command asynchronous process. You will get back the started process.
     * 
     * @param inputStreamSource the process input stream source stream
     * @param numberOfSecondsToWait the number of seconds to wait. If the value less or equal 0, it waits until it is executed
     * @return the asynchronous process which is already started
     */
    ISynchronousProcess runSynchronous(ProcessInputStreamSource inputStreamSource, int numberOfSecondsToWait);

    
    /**
     * Start a new command asynchronous process. You will get back the started process.
     * 
     * @param inputStreamSource the process input stream source stream
     * @param numberOfSecondsToWait the number of seconds to wait. If the value less or equal 0, it waits until it is executed
     * @param pollTimeout the poll timeout to handle the output streams
     * @return the asynchronous process which is already started
     */
    ISynchronousProcess runSynchronous(ProcessInputStreamSource inputStreamSource, int numberOfSecondsToWait, long pollTimeout);

    
    /**
     * Start a new command asynchronous process. You will get back the started process.
     * 
     * @return the asynchronous process which is already started
     */
    IAsynchronousProcess runAsynchronous();

    
    /**
     * Start a new command asynchronous process. You will get back the started process.
     * 
     * @param inputStreamSource the process input stream source stream
     * @param processOut the process output stream
     * @param processErr the process error stream
     * @return the asynchronous process which is already started
     */
    IAsynchronousProcess runAsynchronous(ProcessInputStreamSource inputStreamSource, IProcessOutputStream processOut, IProcessOutputStream processErr);

    
    /**
     * Start a new command asynchronous process. You will get back the started process.
     * 
     * @param inputStreamSource the process input stream source stream
     * @param processOut the process output stream
     * @param processErr the process error stream
     * @param pollTimeout the poll timeout to handle the output streams
     * @return the asynchronous process which is already started
     */
    IAsynchronousProcess runAsynchronous(ProcessInputStreamSource inputStreamSource, IProcessOutputStream processOut, IProcessOutputStream processErr, long pollTimeout);
}
