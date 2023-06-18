/*
 * WindowsSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.executer.impl;


import com.github.toolarium.system.command.dto.ISystemCommand;
import com.github.toolarium.system.command.dto.SystemCommand;
import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import com.github.toolarium.system.command.process.util.ProcessBuilderUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Implements a windows based system command executer
 *
 * @author patrick
 */
public class WindowsSystemCommandExecuterImpl extends AbstractSystemCommandExecuterImpl {
    private static final String TEMP_VARIABLE = "%" + ProcessBuilderUtil.TEMP + "%";

    /**
     * Constructor for WindowsSystemCommandExecuterImpl
     *
     * @param systemCommandGroupList the system command group list
     */
    public WindowsSystemCommandExecuterImpl(ISystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getShellStartCommand(java.lang.String, com.github.toolarium.system.command.dto.ISystemCommand)
     */
    @Override
    public List<String> getShellStartCommand(String id, ISystemCommand systemCommand) {
        List<String> cmdList = new ArrayList<>();
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

        if (systemCommand.getProcessEnvironment().isSudoUser() && systemCommand.getProcessEnvironment().getUser() != null && !systemCommand.getProcessEnvironment().getUser().isBlank()) {
            cmdList.addAll(getSudo(systemCommand.getProcessEnvironment().getUser().trim()));
            
            // enhance to start as minimize
            systemCommand.getCommandList().add(0, "start /min /wait");
            systemCommand.getCommandList().add("^>" + TEMP_VARIABLE + "/" + id + ".out");
            systemCommand.getCommandList().add("2^>" + TEMP_VARIABLE + "/" + id + ".err");
            systemCommand.getCommandList().add("^& exit");
        }

        return cmdList;
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getShellEndCommand(java.lang.String, com.github.toolarium.system.command.dto.ISystemCommand)
     */
    @Override
    public List<String> getShellEndCommand(String id, ISystemCommand systemCommand) {
        return null;
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getScriptFileExtension()
     */
    @Override
    public String getScriptFileExtension() {
        return ".bat";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getScriptFileHeader()
     */
    @Override
    public String getScriptFileHeader() {
        return "@ECHO OFF" + getEndOfLine() + "SETLOCAL ENABLEDELAYEDEXPANSION";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getScriptFileFooter()
     */
    @Override
    public String getScriptFileFooter() {
        return "";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getScriptFileComment()
     */
    @Override
    public String getScriptFileComment() {
        return "::";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getNotExistEnvironmentVariableCommand(java.lang.String)
     */
    @Override
    public String getNotExistEnvironmentVariableCommand(String envVariable) {
        return "IF .%" + envVariable + "%==. ";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentSetCommand()
     */
    @Override
    public String getEnvironmentSetCommand() {
        return "SET" + SystemCommand.SPACE + "\"";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentUnsetCommand()
     */
    @Override
    public String getEnvironmentUnsetCommand() {
        return getEnvironmentSetCommand();
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentChangeDirectoryCommand()
     */
    @Override
    public String getEnvironmentChangeDirectoryCommand() {
        return "cd /d" + SystemCommand.SPACE;
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentAssignCommand()
     */
    @Override
    public String getEnvironmentAssignCommand() {
        return "=";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentAssignCommandEnd()
     */
    @Override
    public String getEnvironmentAssignCommandEnd() {
        return "\"";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEndOfLine()
     */
    @Override
    public String getEndOfLine() {
        return "\r\n";
    }
    

    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getCommandOnSuccessStart()
     */
    @Override
    public String getCommandOnSuccessStart() {
        return " && (";
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getCommandOnSuccessEnd()
     */
    @Override
    public String getCommandOnSuccessEnd() {
        return getEndOfLine() + ")";
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getCommandOnErrorStart()
     */
    @Override
    public String getCommandOnErrorStart() {
        return " || (";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getCommandOnErrorEnd()
     */
    @Override
    public String getCommandOnErrorEnd() {
        return getCommandOnSuccessEnd();
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getSudo(java.lang.String)
     */
    @Override
    public List<String> getSudo(String username) {
        return Arrays.asList("runas", "/user:\"" + username + "\"", "/env", "/savecred"); 
    }
}
