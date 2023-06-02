/*
 * DockerSystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;

import com.github.toolarium.system.command.dto.ISystemCommandGroupList;


/**
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
     * @see com.github.toolarium.system.command.builder.AbstractCommandExecuterBuilder#childBuild(com.github.toolarium.system.command.dto.ISystemCommandGroupList)
     */
    @Override
    protected void childBuild(ISystemCommandGroupList systemCommandGroupList) throws IllegalArgumentException {
    }
}
