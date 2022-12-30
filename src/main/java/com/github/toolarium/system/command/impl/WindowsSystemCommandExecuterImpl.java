/*
 * WindowsSystemCommandExecuterImpl.java
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
public class WindowsSystemCommandExecuterImpl extends AbstractSystemCommandExecuterImpl {
    /**
     * @see com.github.toolarium.system.command.impl.AbstractSystemCommandExecuterImpl#preparePlatformDependentCommandList(java.lang.String, java.util.List)
     */
    @Override
    protected List<String> preparePlatformDependentCommandList(String osName, List<String> commandList) {
        List<String> commandParameterList = new ArrayList<String>();
        if (osName.equals("Windows 95")) {
            commandParameterList.add("command.com");
            commandParameterList.add("/C");
        } else {
            commandParameterList.add("cmd.exe");
            commandParameterList.add("/c");
        }

        commandParameterList.addAll(commandList);
        return commandParameterList;
    }
}
