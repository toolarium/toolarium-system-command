/*
 * SystemCommand.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto;


import com.github.toolarium.system.command.ISystemCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implements the {@link ISystemCommand}.
 * 
 * @author patrick
 */
public class SystemCommand implements ISystemCommand {
    /** SPACE */
    public static final String SPACE = " ";

    private List<String> shell;
    private List<String> commandList;
    private StringBuilder command;
    private StringBuilder displayCommand;
    
    
    /**
     * Constructor for SystemCommand
     */
    public SystemCommand() {
        shell = null;
        commandList = new ArrayList<>();
        command = new StringBuilder();
        displayCommand = new StringBuilder();
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommand#getShell()
     */
    @Override
    public List<String> getShell() {
        return shell;
    }

    
    /**
     * Set the shell
     *
     * @param shell the shell
     */
    public void setShell(List<String> shell) {
        this.shell = shell;
    }
    
    
    /**
     * @see com.github.toolarium.system.command.ISystemCommand#getCommandList()
     */
    @Override
    public List<String> getCommandList() {
        return commandList;
    }

    
    /**
     * Add an additional part of the command 
     *
     * @param command the additional part of the command
     */
    public void add(String command) {
        this.add(command, command);
    }

    
    /**
     * Add an additional part of the command 
     *
     * @param command the additional part of the command
     * @param displayCommand the additional part of the command to display
     */
    public void add(String command, String displayCommand) {
        if (!commandList.isEmpty()) {
            this.command.append(SPACE);
            this.displayCommand.append(SPACE);
        }
            
        this.commandList.add(command);
        this.command.append(command);
        this.displayCommand.append(displayCommand);
    }


    /**
     * Set the command list
     *
     * @param commandList the command list
     */
    public void setCommandList(List<String> commandList) {
        this.commandList = commandList;
    }
    
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(shell, command, commandList, displayCommand);
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
        
        SystemCommand other = (SystemCommand) obj;
        return Objects.equals(shell, other.shell) && Objects.equals(command, other.command) && Objects.equals(commandList, other.commandList) && Objects.equals(displayCommand, other.displayCommand);
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommand#toString(boolean)
     */
    @Override
    public String toString(boolean forDisplay) {
        if (forDisplay) {
            return displayCommand.toString();
        }
        
        return command.toString();
    }
}
