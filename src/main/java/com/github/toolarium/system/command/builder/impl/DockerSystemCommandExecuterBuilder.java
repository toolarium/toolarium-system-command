/*
 * DockerSystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder.impl;

import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;


/**
 * The docker system command executer builder
 * 
 * @author patrick
 */
public class DockerSystemCommandExecuterBuilder extends AbstractCommandExecuterBuilder {

    /**
     * Constructor for DockerSystemCommandExecuterBuilder
     *
     * @param systemCommandGroupList the system command group list
     */
    public DockerSystemCommandExecuterBuilder(ISystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
    }
    
    
    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#shell(java.lang.String[])
     */
    @Override
    public DockerSystemCommandExecuterBuilder shell(String... shell) {
        return (DockerSystemCommandExecuterBuilder)super.shell(shell);
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#user(java.lang.String)
     */
    @Override
    public DockerSystemCommandExecuterBuilder user(String user) {
        return (DockerSystemCommandExecuterBuilder)super.user(user);
    }
    

    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#workingPath(java.lang.String)
     */
    @Override
    public DockerSystemCommandExecuterBuilder workingPath(String workingPath) {
        return (DockerSystemCommandExecuterBuilder)super.workingPath(workingPath);
    }

    
    /**
     * Set the os
     *
     * @param os the os
     * @return the system command executer builder
    @Override
    public DockerSystemCommandExecuterBuilder os(String os) {
        return (DockerSystemCommandExecuterBuilder)super.os(os);
    }
     */

    
    /**
     * Set the os version
     *
     * @param os the os version
     * @return the system command executer builder
    @Override
    public DockerSystemCommandExecuterBuilder osVersion(String osVersion) {
        return (DockerSystemCommandExecuterBuilder)super.osVersion(osVersion);
    }
    */

    
    /**
     * Set the architecture
     *
     * @param architecture the architecture
     * @return the system command executer builder
    @Override
    public DockerSystemCommandExecuterBuilder architecture(String architecture) {
        return (DockerSystemCommandExecuterBuilder)super.architecture(architecture);
    }
    */


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#environmentVariable(java.lang.String, java.lang.String)
     */
    @Override
    public DockerSystemCommandExecuterBuilder environmentVariable(String key, String value) {
        return (DockerSystemCommandExecuterBuilder)super.environmentVariable(key, value);
    }

    
    /**
     * @see com.github.toolarium.system.command.builder.impl.AbstractCommandExecuterBuilder#childBuild(com.github.toolarium.system.command.dto.list.ISystemCommandGroupList)
     */
    @Override
    protected void childBuild(ISystemCommandGroupList systemCommandGroupList) throws IllegalArgumentException {
    }
}
