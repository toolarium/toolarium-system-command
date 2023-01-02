/*
 * WindowsSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.impl;


import com.github.toolarium.system.command.IProcessEnvironment;
import com.github.toolarium.system.command.ISystemCommand;
import java.util.Arrays;
import java.util.List;


/**
 * Implements a windows based system command executer
 *
 * @author patrick
 */
public class WindowsSystemCommandExecuterImpl extends AbstractSystemCommandExecuterImpl {

    /**
     * Constructor for WindowsSystemCommandExecuterImpl
     *
     * @param processEnvironment the process environment
     * @param systemCommand the system command
     */
    public WindowsSystemCommandExecuterImpl(IProcessEnvironment processEnvironment, ISystemCommand systemCommand) {
        super(processEnvironment, systemCommand);
    }


    /**
     * @see com.github.toolarium.system.command.impl.AbstractSystemCommandExecuterImpl#getShellCommand(com.github.toolarium.system.command.IProcessEnvironment, com.github.toolarium.system.command.ISystemCommand)
     */
    @Override
    protected List<String> getShellCommand(IProcessEnvironment processEnvironment, ISystemCommand systemCommand) {
        
        if (systemCommand.getShell() == null || systemCommand.getShell().isEmpty()) {
            if (processEnvironment.getOS().equalsIgnoreCase("Windows 95")) {
                return Arrays.asList("command.com", "/C");
            } else {
                return Arrays.asList("cmd.exe", "/c");
            }
        }

        return systemCommand.getShell();
    }
}
