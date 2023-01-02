/*
 * LinuxSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.impl;

import com.github.toolarium.system.command.IProcessEnvironment;
import com.github.toolarium.system.command.ISystemCommand;

/**
 * Implements a linux based system command executer
 *
 * @author patrick
 */
public class LinuxSystemCommandExecuterImpl extends UnixSystemCommandExecuterImpl {
    
    /**
     * Constructor for LinuxSystemCommandExecuterImpl
     *
     * @param processEnvironment the process environment
     * @param systemCommand the system command
     */
    public LinuxSystemCommandExecuterImpl(IProcessEnvironment processEnvironment, ISystemCommand systemCommand) {
        super(processEnvironment, systemCommand);
    }
}
