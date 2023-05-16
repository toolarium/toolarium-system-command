/*
 * SystemCommandExecuterPlatformSupportWrapper.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.util.dto;

import com.github.toolarium.system.command.ISystemCommand;
import com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport;
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
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#writeToFile(java.nio.file.Path, java.lang.String)
     */
    @Override
    public void writeToFile(Path file, String content) throws IOException {
        systemCommandExecuterPlatformSupport.writeToFile(file, content);
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getSudo(java.lang.String)
     */
    @Override
    public List<String> getSudo(String username) {
        return handleNull(systemCommandExecuterPlatformSupport.getSudo(username));
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getShellCommand(com.github.toolarium.system.command.ISystemCommand)
     */
    @Override
    public List<String> getShellCommand(ISystemCommand systemCommand) {
        return handleNull(systemCommandExecuterPlatformSupport.getShellCommand(systemCommand));
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getScriptFileHeader()
     */
    @Override
    public String getScriptFileHeader() {
        return handleNull(systemCommandExecuterPlatformSupport.getScriptFileHeader());
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getScriptFileFooter()
     */
    @Override
    public String getScriptFileFooter() {
        return handleNull(systemCommandExecuterPlatformSupport.getScriptFileFooter());
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getScriptFileExtension()
     */
    @Override
    public String getScriptFileExtension() {
        return handleNull(systemCommandExecuterPlatformSupport.getScriptFileExtension());
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getScriptFileComment()
     */
    @Override
    public String getScriptFileComment() {
        return handleNull(systemCommandExecuterPlatformSupport.getScriptFileComment());
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentUnsetCommand()
     */
    @Override
    public String getEnvironmentUnsetCommand() {
        return handleNull(systemCommandExecuterPlatformSupport.getEnvironmentUnsetCommand());
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentSetCommand()
     */
    @Override
    public String getEnvironmentSetCommand() {
        return handleNull(systemCommandExecuterPlatformSupport.getEnvironmentSetCommand());
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentChangeDirectoryCommand()
     */
    @Override
    public String getEnvironmentChangeDirectoryCommand() {
        return handleNull(systemCommandExecuterPlatformSupport.getEnvironmentChangeDirectoryCommand());
    }
    
    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentAssignCommandEnd()
     */
    @Override
    public String getEnvironmentAssignCommandEnd() {
        return handleNull(systemCommandExecuterPlatformSupport.getEnvironmentAssignCommandEnd());
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEnvironmentAssignCommand()
     */
    @Override
    public String getEnvironmentAssignCommand() {
        return handleNull(systemCommandExecuterPlatformSupport.getEnvironmentAssignCommand());
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#getEndOfLine()
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
