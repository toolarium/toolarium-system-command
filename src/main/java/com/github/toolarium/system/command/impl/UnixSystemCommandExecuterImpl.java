/*
 * UnixSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.impl;

import com.github.toolarium.system.command.IProcessEnvironment;
import com.github.toolarium.system.command.ISystemCommand;
import java.util.Arrays;
import java.util.List;

/**
 * Implements a unix based system command executer
 * <p>to proper use under linux you have to close streams: <code> my.log 2&gt;&amp;1 &lt;/dev/zero &amp;</code></p>
 *
 * @author patrick
 */
public class UnixSystemCommandExecuterImpl extends AbstractSystemCommandExecuterImpl {
    
    /**
     * Constructor for WindowsSystemCommandExecuterImpl
     *
     * @param processEnvironment the process environment
     * @param systemCommand the system command
     */
    public UnixSystemCommandExecuterImpl(IProcessEnvironment processEnvironment, ISystemCommand systemCommand) {
        super(processEnvironment, systemCommand);
    }

    
    /**
     * @see com.github.toolarium.system.command.impl.AbstractSystemCommandExecuterImpl#getShellCommand(com.github.toolarium.system.command.IProcessEnvironment, com.github.toolarium.system.command.ISystemCommand)
     */
    @Override
    protected List<String> getShellCommand(IProcessEnvironment processEnvironment, ISystemCommand systemCommand) {
        
        if (systemCommand.getShell() == null || systemCommand.getShell().isEmpty()) {
            return Arrays.asList("sh", "-c");
        }

        return systemCommand.getShell();
    }
}
