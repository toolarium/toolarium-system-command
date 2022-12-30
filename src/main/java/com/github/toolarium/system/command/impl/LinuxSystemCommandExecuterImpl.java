/*
 * LinuxSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.impl;


import java.util.ArrayList;
import java.util.List;


/**
 * Implements a linux based system command executer
 *
 * @author patrick
 */
public class LinuxSystemCommandExecuterImpl extends AbstractSystemCommandExecuterImpl {
    private String shell;
    
    
    /**
     * Constructor for LinuxSystemCommandExecuterImpl
     */
    public LinuxSystemCommandExecuterImpl() {
        shell = "sh";
    }

    
    /**
     * Constructor for LinuxSystemCommandExecuterImpl
     * 
     * @param shell the used shell
     */
    public LinuxSystemCommandExecuterImpl(String shell) {
        this.shell = shell;
    }

    
    /**
     * @see com.github.toolarium.system.command.impl.AbstractSystemCommandExecuterImpl#preparePlatformDependentCommandList(java.lang.String, java.util.List)
     */
    @Override
    protected List<String> preparePlatformDependentCommandList(String osName, List<String> commandList) {
        List<String> commandParameterList = new ArrayList<String>();
        commandParameterList.add(shell);
        commandParameterList.add("-c");
        commandParameterList.addAll(commandList);
        // to proper use under linux you have to close streams: > my.log 2>&1 </dev/zero &
        return commandParameterList;
    }
}
