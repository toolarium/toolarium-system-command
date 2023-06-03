/*
 * SystemCommandGroup.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto.group;

import com.github.toolarium.system.command.dto.ISystemCommand;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 * Implements the {@link ISystemCommandGroup}.
 *  
 * @author patrick
 */
public class SystemCommandGroup implements ISystemCommandGroup, Serializable {
    private static final long serialVersionUID = 1715658593303346978L;
    private final String id;
    private List<ISystemCommand> systemCommandList;
    private Boolean runAsScript;
    
    
    /**
     * Constructor for SystemCommandGroup
     */
    public SystemCommandGroup() {
        this.id = "L" + ProcessStreamUtil.getInstance().getId();
        this.systemCommandList = new ArrayList<>();
        this.runAsScript = null;
    }
    

    /**
     * @see com.github.toolarium.system.command.dto.group.ISystemCommandGroup#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    
    /**
     * Add a system command 
     *
     * @param systemCommand the system command
     */
    public void add(ISystemCommand systemCommand) {
        systemCommandList.add(systemCommand);
    }


    /**
     * @see com.github.toolarium.system.command.dto.group.ISystemCommandGroup#iterator()
     */
    @Override
    public Iterator<ISystemCommand> iterator() {
        return systemCommandList.iterator();
    }


    /**
     * @see com.github.toolarium.system.command.dto.group.ISystemCommandGroup#size()
     */
    @Override
    public int size() {
        return systemCommandList.size();
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.group.ISystemCommandGroup#runAsScript()
     */
    @Override
    public boolean runAsScript() {
        if (runAsScript != null) {
            return runAsScript.booleanValue();
        }
        
        return systemCommandList.size() > 1;
    }


    /**
     * @see com.github.toolarium.system.command.dto.group.ISystemCommandGroup#forceRunAsScript()
     */
    @Override
    public void forceRunAsScript() {
        this.runAsScript = Boolean.TRUE;
    }

    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(systemCommandList);
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
        
        SystemCommandGroup other = (SystemCommandGroup) obj;
        return Objects.equals(systemCommandList, other.systemCommandList);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(true);
    }


    /**
     * @see com.github.toolarium.system.command.dto.group.ISystemCommandGroup#toString(boolean)
     */
    @Override
    public String toString(boolean forDisplay) {
        if (systemCommandList == null || systemCommandList.isEmpty()) {
            return "";
        }

        StringBuilder displayCommand = new StringBuilder();
        for (ISystemCommand systemCommand : systemCommandList) {
            if (displayCommand.length() > 0) {
                displayCommand.append("\n");
            }
            
            //displayCommand.append(ScriptUtil.getInstance().prepareCommandList(getShellCommand(systemCommand)));
            //displayCommand.append(SystemCommand.SPACE);
            displayCommand.append(systemCommand.toString(forDisplay));
        }

        return displayCommand.toString();
    }
}
