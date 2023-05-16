/*
 * PlatformDependentSystemCommand.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto;

import com.github.toolarium.system.command.ISystemCommand;
import com.github.toolarium.system.command.process.env.IProcessEnvironment;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;


/**
 * Platform dependent system command
 * 
 * @author patrick
 */
public class PlatformDependentSystemCommand implements Serializable {
    private static final long serialVersionUID = -1346154671019240657L;
    private final String commandId;
    private Path tempPath;
    private final List<? extends ISystemCommand> systemCommandList;
    private final String displayCommand;


    /**
     * Constructor for PlatformDependentCommand
     *
     * @param systemCommandList the system command list
     * @param displayCommand the display command
     */
    public PlatformDependentSystemCommand(final List<? extends ISystemCommand> systemCommandList, final String displayCommand) {
        this.commandId = ProcessStreamUtil.getInstance().getId();
        this.systemCommandList = systemCommandList;
        this.displayCommand = displayCommand;
    }
    
    
    /**
     * Validate the command list
     * 
     * @throws IllegalArgumentException In case of a invalid command list
     */
    public void validate() {
        if (systemCommandList == null || systemCommandList.isEmpty() || systemCommandList.size() < 1) {
            throw new IllegalArgumentException("Invalid process environment list!");
        }
        
        for (ISystemCommand systemCommand : systemCommandList) {
            validateProcessEnvironment(systemCommand.getProcessEnvironment());
        }
    }

    
    /**
     * Get the command id
     * 
     * @return the command id
     */
    public String getCommandId() {
        return commandId;
    }

    
    /**
     * Get the process owned temp path
     *
     * @return the process owned temp path
     */
    public Path getTempPath() {
        return tempPath;
    }
    
    
    /**
     * Sets the temp path
     *
     * @param tempPath the temp path
     */
    public void setTempPath(Path tempPath) {
        this.tempPath = tempPath;
    }

    
    /**
     * Get the system command list
     *
     * @return the system command list
     */
    public List<? extends ISystemCommand> getSystemCommandList() {
        return systemCommandList;
    }
    
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(commandId, displayCommand, systemCommandList);
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        PlatformDependentSystemCommand other = (PlatformDependentSystemCommand) obj;
        return Objects.equals(commandId, other.commandId) && Objects.equals(displayCommand, other.displayCommand)
                && Objects.equals(systemCommandList, other.systemCommandList);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return displayCommand;
    }


    /**
     * Validate the process environment
     *
     * @param processEnvironment the process environment
     * @throws IllegalArgumentException In case of a invalid process environment
     */
    protected void validateProcessEnvironment(IProcessEnvironment processEnvironment) {
        //processEnvironment.getArchitecture()
        //processEnvironment.getOS()
        //processEnvironment.getOSVersion()
        //processEnvironment.getUser()

        if (processEnvironment.getEnvironmentVariables() == null) {
            throw new IllegalArgumentException("Invalid process environment list!");
        }
        
        if (processEnvironment.getWorkingPath() != null && !processEnvironment.getWorkingPath().isBlank()) {
            if (!new File(processEnvironment.getWorkingPath()).canWrite()) {
                throw new IllegalArgumentException("Invalid process environment, no write permission for path [" + processEnvironment.getWorkingPath() + "]!");
            }
        }
    }
}
