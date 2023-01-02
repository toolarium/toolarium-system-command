/*
 * ICommand.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import java.util.List;

/**
 * The command
 * 
 * @author patrick
 */
public interface ISystemCommand {

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
     * The command as string
     * 
     * @param forDisplay true to prepare the arguments for display; otherwise false. This can be used to protect security relevant arguments e.g. java properties with a password
     * @return the arguments as string 
     */
    String toString(boolean forDisplay);
}
