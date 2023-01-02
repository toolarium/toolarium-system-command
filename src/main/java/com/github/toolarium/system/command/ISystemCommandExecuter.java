/*
 * ISystemCommandExecuter.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

/**
 * Defines the system command executer
 *
 * @author patrick
 */
public interface ISystemCommandExecuter {
    
    /**
     * Start a new command synchronous process. You will get back the terminated process.
     *
     * @return the terminated process
     */
    IProcess runSynchronous();

    
    /**
     * Start a new command synchronous process. You will get back the terminated process. In case it could not be
     * finished in the given time it will be destroyed.
     *
     * @param numberOfSecondsToWait the number of seconds to wait. If the value less or equal 0, it waits until it is executed
     * @return the process
     */
    IProcess runSynchronous(int numberOfSecondsToWait);

    
    /**
     * Start a new command asynchronous process. You will get back the started process.
     *
     * @return the process
     */
    IAsynchronousProcess runAsynchronous();
}
