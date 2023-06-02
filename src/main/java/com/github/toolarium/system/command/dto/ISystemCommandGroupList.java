/*
 * ISystemCommandGroupList.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto;

import com.github.toolarium.system.command.ISystemCommand;
import java.util.Iterator;
import java.util.List;


/**
 * Defines a system command group list
 * 
 * @author patrick
 */
public interface ISystemCommandGroupList {

    /**
     * Get the id of this system command list
     *
     * @return the id of this system command list
     */
    String getId();
    
    
    /**
     * Add system command list to the current group
     *
     * @param systemCommands the system command list to the current group
     */
    void add(List<? extends ISystemCommand> systemCommands);

    
    /**
     * Add system command list to the current group
     *
     * @param systemCommands the system command list to the current group 
     */
    void add(ISystemCommand... systemCommands);

 
    /**
     * Add a system command group
     *
     * @param systemCommandGroup the system command group
     */
    void add(ISystemCommandGroup systemCommandGroup);
    
    
    /**
     * Force to run current system command group as script
     */
    void forceSystenCommandGroupRunAsScript();

    
    /**
     * Start a new system command group
     */
    void newGroup();
    
    
    /**
     * Returns an iterator over the system command group.
     *
     * @return an iterator over the system command group
     */
    Iterator<ISystemCommandGroup> iterator();

    
    /**
     * Get the size of the system command group list.
     * 
     * @return the size of the system command group list
     */
    int size();
    
    
    /**
     * The system command group list as string
     * 
     * @param forDisplay true to prepare the arguments for display; otherwise false. This can be used to protect security relevant arguments e.g. java properties with a password
     * @return the arguments as string 
     */
    String toString(boolean forDisplay);
}
