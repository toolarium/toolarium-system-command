/*
 * ISystemCommandGroupList.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto.list;

import com.github.toolarium.system.command.dto.ISystemCommand;
import com.github.toolarium.system.command.dto.group.ISystemCommandGroup;
import java.util.Iterator;
import java.util.List;


/**
 * The system command group list is used to handle the case to group individual {@link ISystemCommandGroup}s. 
 * To offer the possibility to pipe {@link ISystemCommandGroup}  this abstraction is needed.
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
     * Check if this system command group list contains parts which runs as a script.
     * By default it depends if there are more than one system commands to execute.
     * This can be forced.
     *
     * @return true if it runs as script
     */
    boolean runAsScript();

    
    /**
     * Force to run as script.
     */
    void forceRunAsScript();

    
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
