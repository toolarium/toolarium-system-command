/*
 * ISystemCommandGroup.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto;

import com.github.toolarium.system.command.ISystemCommand;
import java.util.Iterator;

/**
 * Defines a system command group. This will be executed as a script.
 * 
 * @author patrick
 */
public interface ISystemCommandGroup {
    
    /**
     * Get the id of this command group
     *
     * @return the id of this command group
     */
    String getId();

    
    /**
     * Add a system command 
     *
     * @param systemCommand the system command
     */
    void add(ISystemCommand systemCommand);
    
    
    /**
     * Returns an iterator over the system command list.
     *
     * @return an iterator over the system command list
     */
    Iterator<ISystemCommand> iterator();


    /**
     * Get the size of the system command group.
     * 
     * @return the size of the system command group
     */
    int size();
    
    
    /**
     * Define if this system command group should run inside a script.
     * By default it depends if there are more than one system command.
     *
     * @return true to run as script
     */
    boolean runAsScript();
    
    
    /**
     * Force to run as script
     */
    void forceRunAsScript();

    
    /**
     * The system command group as string
     * 
     * @param forDisplay true to prepare the arguments for display; otherwise false. This can be used to protect security relevant arguments e.g. java properties with a password
     * @return the arguments as string 
     */
    String toString(boolean forDisplay);
}
