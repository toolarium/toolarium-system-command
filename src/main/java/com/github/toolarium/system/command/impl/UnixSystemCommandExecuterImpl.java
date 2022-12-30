/*
 * UnixSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a unix based system command executer
 *
 * @author patrick
 */
public class UnixSystemCommandExecuterImpl extends AbstractSystemCommandExecuterImpl {
    private String shell;
    
    
    /**
     * Constructor for UnixSystemCommandExecuterImpl
     */
    public UnixSystemCommandExecuterImpl() {
        shell = "sh";
    }

    
    /**
     * Constructor for UnixSystemCommandExecuterImpl
     * 
     * @param shell the used shell
     */
    public UnixSystemCommandExecuterImpl(String shell) {
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
        return commandParameterList;
    }
}
