/*
 * LinuxSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.executer.impl;

import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;


/**
 * Implements a linux based system command executer
 *
 * @author patrick
 */
public class LinuxSystemCommandExecuterImpl extends UnixSystemCommandExecuterImpl {
    
    /**
     * Constructor for LinuxSystemCommandExecuterImpl
     *
     * @param systemCommandGroupList the system command group list
     */
    public LinuxSystemCommandExecuterImpl(ISystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
    }
    
    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getShellStartCommand(com.github.toolarium.system.command.ISystemCommand)
    @Override
    public List<String> getShellStartCommand(ISystemCommand systemCommand) {
        List<String> cmdList = new ArrayList<>();
        String currentUser = System.getProperty("user.name").trim();
        if (systemCommand.getProcessEnvironment().getUser() != null && !systemCommand.getProcessEnvironment().getUser().isBlank() && !currentUser.equals(systemCommand.getProcessEnvironment().getUser().trim())) {
            cmdList.addAll(getSudo(systemCommand.getProcessEnvironment().getUser().trim()));
        }
        
        if (systemCommand.getShell() == null || systemCommand.getShell().isEmpty()) {
            cmdList.addAll(Arrays.asList("/bin/bash", "-c"));           
        } else {
            cmdList.addAll(systemCommand.getShell());
        }
        
        return cmdList;
    }
     */
}
