/*
 * WindowsSystemCommandExecuterImpl.java
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
 * Implements a windows based system command executer
 *
 * @author patrick
 */
public class WindowsSystemCommandExecuterImpl extends AbstractSystemCommandExecuterImpl {

    /**
     * Constructor for WindowsSystemCommandExecuterImpl
     *
     * @param systemCommandList the system command list
     */
    public WindowsSystemCommandExecuterImpl(List<? extends ISystemCommand> systemCommandList) {
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
            if (systemCommand.getProcessEnvironment() != null 
                && systemCommand.getProcessEnvironment().getOS() != null
                && systemCommand.getProcessEnvironment().getOS().equalsIgnoreCase("Windows 95")) {
                cmdList.addAll(Arrays.asList("command.com", "/C"));
            } else {
                cmdList.addAll(Arrays.asList("cmd.exe", "/c"));
            }
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
        return ".bat";
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getScriptFileHeader()
     */
    @Override
    public String getScriptFileHeader() {
        return "@ECHO OFF";
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
        return "::";
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentSetCommand()
     */
    @Override
    public String getEnvironmentSetCommand() {
        return "SET" + SystemCommand.SPACE + "\"";
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentUnsetCommand()
     */
    @Override
    public String getEnvironmentUnsetCommand() {
        return getEnvironmentSetCommand();
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentChangeDirectoryCommand()
     */
    @Override
    public String getEnvironmentChangeDirectoryCommand() {
        return "cd /d" + SystemCommand.SPACE;
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentAssignCommand()
     */
    @Override
    public String getEnvironmentAssignCommand() {
        return "=";
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
        return "\r\n";
    }
    

    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getSudo(java.lang.String)
     */
    @Override
    public List<String> getSudo(String username) {
        return Arrays.asList("runas", "/user", "\"" + username + "\"", "/savecred"); 
    }
}
