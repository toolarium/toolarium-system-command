/*
 * ISystemCommandGroup.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto.group;

import com.github.toolarium.system.command.dto.ISystemCommand;
import java.util.Iterator;


/**
 * The system command group. In case of multiple {@link ISystemCommand}s it will be grouped.
 * In case a group contains multiple {@link ISystemCommand} it will be executed as a script. 
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
     * Check if this system command group contains parts which runs as a script.
     * By default it depends if there are more than one system commands to execute.
     * This can be forced.
     *
     * @return true if it runs as script
     */
    boolean runAsScript();

    
    /**
     * The system command group as string
     * 
     * @param forDisplay true to prepare the arguments for display; otherwise false. This can be used to protect security relevant arguments e.g. java properties with a password
     * @return the arguments as string 
     */
    String toString(boolean forDisplay);
}
