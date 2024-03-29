/*
 * UnixSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.executer.impl;

import com.github.toolarium.system.command.dto.ISystemCommand;
import com.github.toolarium.system.command.dto.SystemCommand;
import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
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
     * @param systemCommandGroupList the system command group list
     */
    public UnixSystemCommandExecuterImpl(ISystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getShellStartCommand(java.lang.String, com.github.toolarium.system.command.dto.ISystemCommand)
     */
    @Override
    public List<String> getShellStartCommand(String id, ISystemCommand systemCommand) {
        List<String> cmdList = new ArrayList<>();
        if (systemCommand.getShell() == null || systemCommand.getShell().isEmpty()) {
            cmdList.addAll(Arrays.asList("sh", "-c"));           
        } else {
            cmdList.addAll(systemCommand.getShell());
        }

        if (systemCommand.getProcessEnvironment().isSudoUser() && systemCommand.getProcessEnvironment().getUser() != null && !systemCommand.getProcessEnvironment().getUser().isBlank()) {  
            cmdList.addAll(getSudo(systemCommand.getProcessEnvironment().getUser().trim()));
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
        return ".sh";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getScriptFileHeader()
     */
    @Override
    public String getScriptFileHeader() {
        return "#!/bin/sh";
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
        return "#";
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getNotExistEnvironmentVariableCommand(java.lang.String)
     */
    @Override
    public String getNotExistEnvironmentVariableCommand(String envVariable) {
        return "! [ -n \"${" + envVariable + "}\" ] && ";
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentSetCommand()
     */
    @Override
    public String getEnvironmentSetCommand() {
        return "export" + SystemCommand.SPACE;
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentUnsetCommand()
     */
    @Override
    public String getEnvironmentUnsetCommand() {
        return "unset" + SystemCommand.SPACE;
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentChangeDirectoryCommand()
     */
    @Override
    public String getEnvironmentChangeDirectoryCommand() {
        return "cd" + SystemCommand.SPACE;
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentAssignCommand()
     */
    @Override
    public String getEnvironmentAssignCommand() {
        return "=\"";
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
        return "\n";
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
        return Arrays.asList("su", "\"" + username + "\""); 
    }
}
