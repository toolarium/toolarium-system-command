/*
 * ISystemCommandGroupList.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto.list;

import com.github.toolarium.system.command.dto.group.ISystemCommandGroup;
import java.time.Instant;
import java.util.Iterator;


/**
 * The system command group list is used to handle the case to group individual {@link ISystemCommandGroup}s. 
 * To offer the possibility to pipe {@link ISystemCommandGroup} this abstraction is needed.
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
     * Check if this system command group list contains parts which runs as a script.
     * By default it depends if there are more than one system commands to execute.
     * This can be forced.
     *
     * @return true if it runs as script
     */
    boolean runAsScript();

    
    /**
     * Check if the system command group list locked or not
     *
     * @return the lock timeout in seconds
     */
    boolean isLocked();
    
    
    /**
     * Reset the lock.
     */
    void resetLock();

    
    /**
     * Get the lock timeout
     *
     * @return the lock timeout
     */
    Instant getLockTimeout();
    
    
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
