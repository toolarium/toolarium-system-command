/*
 * SystemCommandExecuterPlatformSupportWrapper.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.executer.impl;

import com.github.toolarium.system.command.dto.ISystemCommand;
import com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;


/**
 * Implements a {@link ISystemCommandExecuterPlatformSupport} wrapper.
 *  
 * @author patrick
 */
public class SystemCommandExecuterPlatformSupportWrapper implements ISystemCommandExecuterPlatformSupport, Serializable {
    private static final long serialVersionUID = -4185727614461155210L;
    private final ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport;
        
    
    /**
     * Constructor for SystemCommandExecuterPlatformSupportWrapper
     *
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     */
    public SystemCommandExecuterPlatformSupportWrapper(final ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport) {
        this.systemCommandExecuterPlatformSupport = systemCommandExecuterPlatformSupport;
    }
    
    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#writeToFile(java.nio.file.Path, java.lang.String)
     */
    @Override
    public void writeToFile(Path file, String content) throws IOException {
        systemCommandExecuterPlatformSupport.writeToFile(file, content);
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getSudo(java.lang.String)
     */
    @Override
    public List<String> getSudo(String username) {
        return handleNull(systemCommandExecuterPlatformSupport.getSudo(username));
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getShellStartCommand(java.lang.String, com.github.toolarium.system.command.dto.ISystemCommand)
     */
    @Override
    public List<String> getShellStartCommand(String id, ISystemCommand systemCommand) {
        return handleNull(systemCommandExecuterPlatformSupport.getShellStartCommand(id, systemCommand));
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getShellEndCommand(java.lang.String, com.github.toolarium.system.command.dto.ISystemCommand)
     */
    @Override
    public List<String> getShellEndCommand(String id, ISystemCommand systemCommand) {
        return handleNull(systemCommandExecuterPlatformSupport.getShellEndCommand(id, systemCommand));
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getScriptFileHeader()
     */
    @Override
    public String getScriptFileHeader() {
        return handleNull(systemCommandExecuterPlatformSupport.getScriptFileHeader());
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getScriptFileFooter()
     */
    @Override
    public String getScriptFileFooter() {
        return handleNull(systemCommandExecuterPlatformSupport.getScriptFileFooter());
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getScriptFileExtension()
     */
    @Override
    public String getScriptFileExtension() {
        return handleNull(systemCommandExecuterPlatformSupport.getScriptFileExtension());
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getScriptFileComment()
     */
    @Override
    public String getScriptFileComment() {
        return handleNull(systemCommandExecuterPlatformSupport.getScriptFileComment());
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentUnsetCommand()
     */
    @Override
    public String getEnvironmentUnsetCommand() {
        return handleNull(systemCommandExecuterPlatformSupport.getEnvironmentUnsetCommand());
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getNotExistEnvironmentVariableCommand(java.lang.String)
     */
    @Override
    public String getNotExistEnvironmentVariableCommand(String envVariable) {
        return handleNull(systemCommandExecuterPlatformSupport.getNotExistEnvironmentVariableCommand(envVariable));
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentSetCommand()
     */
    @Override
    public String getEnvironmentSetCommand() {
        return handleNull(systemCommandExecuterPlatformSupport.getEnvironmentSetCommand());
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentChangeDirectoryCommand()
     */
    @Override
    public String getEnvironmentChangeDirectoryCommand() {
        return handleNull(systemCommandExecuterPlatformSupport.getEnvironmentChangeDirectoryCommand());
    }
    
    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentAssignCommandEnd()
     */
    @Override
    public String getEnvironmentAssignCommandEnd() {
        return handleNull(systemCommandExecuterPlatformSupport.getEnvironmentAssignCommandEnd());
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEnvironmentAssignCommand()
     */
    @Override
    public String getEnvironmentAssignCommand() {
        return handleNull(systemCommandExecuterPlatformSupport.getEnvironmentAssignCommand());
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getCommandOnSuccessStart()
     */
    @Override
    public String getCommandOnSuccessStart() {
        return handleNull(systemCommandExecuterPlatformSupport.getCommandOnSuccessStart());
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getCommandOnSuccessEnd()
     */
    @Override
    public String getCommandOnSuccessEnd() {
        return handleNull(systemCommandExecuterPlatformSupport.getCommandOnSuccessEnd());
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getCommandOnErrorStart()
     */
    @Override
    public String getCommandOnErrorStart() {
        return handleNull(systemCommandExecuterPlatformSupport.getCommandOnErrorStart());
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getCommandOnErrorEnd()
     */
    @Override
    public String getCommandOnErrorEnd() {
        return handleNull(systemCommandExecuterPlatformSupport.getCommandOnErrorEnd());
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#getEndOfLine()
     */
    @Override
    public String getEndOfLine() {
        return handleNull(systemCommandExecuterPlatformSupport.getEndOfLine());
    }
  

    /**
     * Implements the the null handling 
     *
     * @param content the content
     * @return the content
     */
    protected String handleNull(String content) {
        if (content != null) {
            return content;
        } else {
            return "";
        }
    }

    
    /**
     * Implements the the null handling 
     *
     * @param content the content
     * @return the content
     */
    protected List<String> handleNull(List<String> content) {
        if (content != null) {
            return content;
        } else {
            return Collections.emptyList();
        }
    }
}
