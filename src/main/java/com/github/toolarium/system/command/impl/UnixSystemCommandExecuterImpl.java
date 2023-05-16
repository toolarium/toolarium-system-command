/*
 * UnixSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.impl;

import com.github.toolarium.system.command.ISystemCommand;
import com.github.toolarium.system.command.dto.SystemCommand;
import java.util.ArrayList;
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
     * @param systemCommandList the system command list
     */
    public UnixSystemCommandExecuterImpl(List<? extends ISystemCommand> systemCommandList) {
        super(systemCommandList);
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getShellCommand(com.github.toolarium.system.command.ISystemCommand)
     */
    @Override
    public List<String> getShellCommand(ISystemCommand systemCommand) {
        List<String> cmdList = new ArrayList<>();
        String currentUser = System.getProperty("user.name").trim();
        if (systemCommand.getProcessEnvironment().getUser() != null && !systemCommand.getProcessEnvironment().getUser().isBlank() && !currentUser.equals(systemCommand.getProcessEnvironment().getUser().trim())) {
            cmdList.addAll(getSudo(systemCommand.getProcessEnvironment().getUser().trim()));
        }
        
        if (systemCommand.getShell() == null || systemCommand.getShell().isEmpty()) {
            cmdList.addAll(Arrays.asList("sh", "-c"));
        } else {
            cmdList.addAll(systemCommand.getShell());
        }
        
        return cmdList;
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getScriptFileExtension()
     */
    @Override
    public String getScriptFileExtension() {
        return ".sh";
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getScriptFileHeader()
     */
    @Override
    public String getScriptFileHeader() {
        return "#!/bin/sh";
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getScriptFileFooter()
     */
    @Override
    public String getScriptFileFooter() {
        return "";
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getScriptFileComment()
     */
    @Override
    public String getScriptFileComment() {
        return "#";
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentSetCommand()
     */
    @Override
    public String getEnvironmentSetCommand() {
        return "export" + SystemCommand.SPACE;
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentUnsetCommand()
     */
    @Override
    public String getEnvironmentUnsetCommand() {
        return "unset" + SystemCommand.SPACE;
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentChangeDirectoryCommand()
     */
    @Override
    public String getEnvironmentChangeDirectoryCommand() {
        return "cd" + SystemCommand.SPACE;
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentAssignCommand()
     */
    @Override
    public String getEnvironmentAssignCommand() {
        return "=\"";
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentAssignCommandEnd()
     */
    @Override
    public String getEnvironmentAssignCommandEnd() {
        return "\"";
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEndOfLine()
     */
    @Override
    public String getEndOfLine() {
        return "\n";
    }
    

    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getSudo(java.lang.String)
     */
    @Override
    public List<String> getSudo(String username) {
        return Arrays.asList("sudo", "-u", "\"" + username + "\""); 
    }
}
