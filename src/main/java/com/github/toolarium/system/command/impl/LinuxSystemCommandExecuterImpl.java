/*
 * LinuxSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.impl;

import com.github.toolarium.system.command.ISystemCommand;
import java.util.List;


/**
 * Implements a linux based system command executer
 *
 * @author patrick
 */
public class LinuxSystemCommandExecuterImpl extends UnixSystemCommandExecuterImpl {
    
    /**
     * Constructor for LinuxSystemCommandExecuterImpl
     *
     * @param systemCommandList the system command list
     */
    public LinuxSystemCommandExecuterImpl(List<? extends ISystemCommand> systemCommandList) {
        super(systemCommandList);
    }
}
