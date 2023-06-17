/*
 * SystemCommandGroupList.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto.list;

import com.github.toolarium.system.command.dto.ISystemCommand;
import com.github.toolarium.system.command.dto.group.ISystemCommandGroup;
import com.github.toolarium.system.command.dto.group.SystemCommandGroup;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 * Implements the {@link ISystemCommandGroupList}.
 * 
 * @author patrick
 */
public class SystemCommandGroupList implements ISystemCommandGroupList, Serializable {
    private static final int ONE_HOUR = 60 * 60;
    private static final long serialVersionUID = -7355466348182867999L;
    private final String id;
    private List<SystemCommandGroup> systemCommandGroupList;
    private int lockTimeoutInSeconds;
    private Instant lockTimeout;

    
    /**
     * Constructor for SystemCommandGroupList
     */
    public SystemCommandGroupList() {
        this.id = ProcessStreamUtil.getInstance().getId();
        this.systemCommandGroupList = new ArrayList<>();
        this.lockTimeoutInSeconds = ONE_HOUR;
        this.lockTimeout = null;
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.list.ISystemCommandGroupList#getId()
     */
    @Override
    public String getId() {
        return id;
    }


    /**
     * Add system command list to the current group
     *
     * @param systemCommands the system command list to the current group 
     */
    public void add(ISystemCommand... systemCommands) {
        add(Arrays.asList(systemCommands));
    }

    
    /**
     * Add system command list to the current group
     *
     * @param systemCommands the system command list to the current group
     */
    public void add(List<? extends ISystemCommand> systemCommands) {
        final SystemCommandGroup systemCommandGroup;
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
     * Add a system command group
     *
     * @param systemCommandGroup the system command group
     */
    public void add(SystemCommandGroup systemCommandGroup) {
        systemCommandGroupList.add(systemCommandGroup);
    }


    /**
     * @see com.github.toolarium.system.command.dto.list.ISystemCommandGroupList#runAsScript()
     */
    @Override
    public boolean runAsScript() {
        boolean runAsScript = false;
        
        if (systemCommandGroupList != null && !systemCommandGroupList.isEmpty()) {
            for (ISystemCommandGroup systemCommandGroup : systemCommandGroupList) {
                if (systemCommandGroup.runAsScript()) {
                    runAsScript = true;
                    break;
                }
            }
        }
        
        return runAsScript;
    }


    /**
     * Force to run as script.
     */
    public void forceRunAsScript() {
        if (systemCommandGroupList != null && !systemCommandGroupList.isEmpty()) {
            systemCommandGroupList.get(systemCommandGroupList.size() - 1).forceRunAsScript();
        }
    }

    
    /**
     * Lock the system command group list (default timeout).
     */
    public void lock() {
        lock(lockTimeoutInSeconds);
    }


    /**
     * Lock the system command group list.
     *
     * @param lockTimeoutInSeconds the period in seconds. In case of null the duration will be reset with value of last lock timeout.
     */
    public void lock(Integer lockTimeoutInSeconds) {
        if (lockTimeoutInSeconds == null) {
            this.lockTimeout = null;
        } else {
            this.lockTimeoutInSeconds = lockTimeoutInSeconds;
        }
        
        this.lockTimeout = Instant.ofEpochMilli(Instant.now().toEpochMilli() + (1000 * this.lockTimeoutInSeconds));
    }


    /**
     * @see com.github.toolarium.system.command.dto.list.ISystemCommandGroupList#getLockTimeout()
     */
    @Override
    public Instant getLockTimeout() {
        return lockTimeout;
    }


    /**
     * @see com.github.toolarium.system.command.dto.list.ISystemCommandGroupList#isLocked()
     */
    @Override
    public boolean isLocked() {
        return lockTimeout != null && Instant.now().isAfter(lockTimeout);
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.list.ISystemCommandGroupList#resetLock()
     */
    @Override
    public void resetLock() {
        lock(null);
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.list.ISystemCommandGroupList#newGroup()
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
     * @see com.github.toolarium.system.command.dto.list.ISystemCommandGroupList#iterator()
     */
    @Override
    public Iterator<ISystemCommandGroup> iterator() {
        return new Iterator<ISystemCommandGroup>() {
            private Iterator<SystemCommandGroup> it = systemCommandGroupList.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public ISystemCommandGroup next() {
                return it.next();
            }
        };
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.group.ISystemCommandGroup#size()
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
        return Objects.hash(id, systemCommandGroupList, lockTimeout);
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
        return Objects.equals(id, other.id) && Objects.equals(systemCommandGroupList, other.systemCommandGroupList) && Objects.equals(lockTimeout, other.lockTimeout);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(true);
    }


    /**
     * @see com.github.toolarium.system.command.dto.list.ISystemCommandGroupList#toString(boolean)
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
