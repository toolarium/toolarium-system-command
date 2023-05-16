/*
 * SystemCommand.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto;


import com.github.toolarium.system.command.ISystemCommand;
import com.github.toolarium.system.command.process.env.IProcessEnvironment;
import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
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

    private IProcessEnvironment processEnvironment;
    private List<String> shell;
    private List<String> commandList;
    private StringBuilder command;
    private StringBuilder displayCommand;
    private IProcessOutputStream processOutputStream;
    private IProcessOutputStream errorProcessOutputStream;
    
    /**
     * Constructor for SystemCommand
     * 
     * @param processEnvironment the process environment
     */
    public SystemCommand(IProcessEnvironment processEnvironment) {
        this.processEnvironment = processEnvironment;
        this.shell = null;
        this.commandList = new ArrayList<>();
        this.command = new StringBuilder();
        this.displayCommand = new StringBuilder();
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommand#getProcessEnvironment()
     */
    @Override
    public IProcessEnvironment getProcessEnvironment() {
        return processEnvironment;
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
    protected void setCommandList(List<String> commandList) {
        this.commandList = commandList;
    }
    
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(processEnvironment, processOutputStream, errorProcessOutputStream, shell, command, commandList, displayCommand);
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
        return Objects.equals(processEnvironment, other.processEnvironment)
                && Objects.equals(processOutputStream, other.processOutputStream)
                && Objects.equals(errorProcessOutputStream, other.errorProcessOutputStream)
                && Objects.equals(shell, other.shell)
                && Objects.equals(command, other.command) && Objects.equals(commandList, other.commandList)
                && Objects.equals(displayCommand, other.displayCommand);
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommand#toString(boolean)
     */
    @Override
    public String toString() {
        return toString(true);
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
