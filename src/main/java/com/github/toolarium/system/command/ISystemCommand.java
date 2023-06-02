/*
 * ISystemCommand.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import com.github.toolarium.system.command.process.env.IProcessEnvironment;
import java.util.List;


/**
 * The command
 * 
 * @author patrick
 */
public interface ISystemCommand {

    /**
     * Get the process environment
     * 
     * @return the process environment
     */
    IProcessEnvironment getProcessEnvironment();
    
    
    /**
     * The shell to execute.
     *
     * @return the shell to use, by default null.
     */
    List<String> getShell();

    
    /**
     * The system command list which will be passed in the exact way to the system command.
     *
     * @return the command list
     */
    List<String> getCommandList();

    
    /**
     * Get the system command execution status result
     *
     * @return the system command execution status result
     */
    SystemCommandExecutionStatusResult getSystemCommandExecutionStatusResult();
    
    
    /**
     * The command as string
     * 
     * @param forDisplay true to prepare the arguments for display; otherwise false. This can be used to protect security relevant arguments e.g. java properties with a password
     * @return the arguments as string 
     */
    String toString(boolean forDisplay);
    
    
    /**
     * Defines the execution status result
     */
    enum SystemCommandExecutionStatusResult {
        SUCCESS,
        ERROR,
        SUCCESS_OR_ERROR
    }
}
