/*
 * SystemCommandGroupList.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto;

import com.github.toolarium.system.command.ISystemCommand;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 * 
 * @author patrick
 */
public class SystemCommandGroupList implements ISystemCommandGroupList, Serializable {
    private static final long serialVersionUID = -7355466348182867999L;
    private final String id;
    private List<ISystemCommandGroup> systemCommandGroupList;
    private Path tempPath;

    
    /**
     * Constructor for SystemCommandGroupList
     */
    public SystemCommandGroupList() {
        this.id = "L" + ProcessStreamUtil.getInstance().getId();
        this.systemCommandGroupList = new ArrayList<>();
        this.tempPath = null;
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.ISystemCommandGroupList#getId()
     */
    @Override
    public String getId() {
        return id;
    }


    /**
     * @see com.github.toolarium.system.command.dto.ISystemCommandGroupList#add(com.github.toolarium.system.command.ISystemCommand[])
     */
    @Override
    public void add(ISystemCommand... systemCommands) {
        add(Arrays.asList(systemCommands));
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.ISystemCommandGroupList#add(java.util.List)
     */
    @Override
    public void add(List<? extends ISystemCommand> systemCommands) {
        final ISystemCommandGroup systemCommandGroup;
        if (systemCommandGroupList.isEmpty()) {
            systemCommandGroup = new SystemCommandGroup();
            add(systemCommandGroup);
        } else {
            systemCommandGroup = systemCommandGroupList.get(systemCommandGroupList.size() - 1);
        }
        
        for (ISystemCommand s : systemCommands) {
            systemCommandGroup.add(s);
        }
    }

        
    /**
     * @see com.github.toolarium.system.command.dto.ISystemCommandGroupList#add(com.github.toolarium.system.command.dto.ISystemCommandGroup)
     */
    @Override
    public void add(ISystemCommandGroup systemCommandGroup) {
        systemCommandGroupList.add(systemCommandGroup);
    }


    /**
     * @see com.github.toolarium.system.command.dto.ISystemCommandGroupList#forceSystenCommandGroupRunAsScript()
     */
    @Override
    public void forceSystenCommandGroupRunAsScript() {
        if (systemCommandGroupList != null && !systemCommandGroupList.isEmpty()) {
            systemCommandGroupList.get(systemCommandGroupList.size() - 1).forceRunAsScript();
        }
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.ISystemCommandGroupList#newGroup()
     */
    @Override
    public void newGroup() {
        if (!systemCommandGroupList.isEmpty()) {
            if (systemCommandGroupList.get(0).size() > 0) {
                add(new SystemCommandGroup());
            }
        }
    }


    /**
     * @see com.github.toolarium.system.command.dto.ISystemCommandGroupList#iterator()
     */
    @Override
    public Iterator<ISystemCommandGroup> iterator() {
        return systemCommandGroupList.iterator();
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.ISystemCommandGroup#size()
     */
    @Override
    public int size() {
        return systemCommandGroupList.size();
    }


    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, systemCommandGroupList, tempPath);
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
        
        SystemCommandGroupList other = (SystemCommandGroupList) obj;
        return Objects.equals(id, other.id) && Objects.equals(systemCommandGroupList, other.systemCommandGroupList)
                && Objects.equals(tempPath, other.tempPath);
    }
    

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(true);
    }


    /**
     * @see com.github.toolarium.system.command.dto.ISystemCommandGroupList#toString(boolean)
     */
    @Override
    public String toString(boolean forDisplay) {
        if (systemCommandGroupList == null || systemCommandGroupList.isEmpty()) {
            return "";
        }

        StringBuilder displayCommand = new StringBuilder();
        for (ISystemCommandGroup systemCommandGroup : systemCommandGroupList) {
            if (displayCommand.length() > 0) {
                displayCommand.append("\n");
            }
            
            displayCommand.append(systemCommandGroup.toString(forDisplay));
        }

        return displayCommand.toString();
    }
}
