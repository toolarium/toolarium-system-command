/*
 * SystemCommandExecuter.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;


/**
 * Defines the system command executer
 *
 * @author patrick
 */
public interface SystemCommandExecuter {
    
    /**
     * Execute a new command
     *
     * @param path the path to execute the command or null
     * @param commandList the command list which will be passed in the exact way to the system command.
     * @return the process
     */
    Process executeCommand(String path, String... commandList);


    /**
     * Execute a new command
     *
     * @param path the path to execute the command or null
     * @param numberOfSecondsToWait the number of seconds to wait. If the value less or equal 0, it waits until it is executed
     * @param commandList the command list which will be passed in the exact way to the system command.
     * @return the process
     */
    Process executeCommand(String path, int numberOfSecondsToWait, String... commandList);
}
