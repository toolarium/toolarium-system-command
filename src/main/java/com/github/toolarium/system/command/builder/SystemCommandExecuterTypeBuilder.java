/*
 * SystemCommandExecuterTypeBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;

import com.github.toolarium.system.command.dto.ISystemCommandGroupList;


/**
 * Implements the {@link ISystemCommandExecuterBuilder}.
 *  
 * @author patrick
 */
public class SystemCommandExecuterTypeBuilder implements ISystemCommandExecuterTypeBuilder {
    private ISystemCommandGroupList systemCommandGroupList;
    

    /**
     * Constructor for SystemCommandExecuterTypeBuilder
     * 
     * @param systemCommandGroupList the system command group list
     */
    public SystemCommandExecuterTypeBuilder(ISystemCommandGroupList systemCommandGroupList) {
        this.systemCommandGroupList = systemCommandGroupList;
    }

    
    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterTypeBuilder#system()
     */
    @Override
    public SystemCommandExecuterBuilder system() {
        return new SystemCommandExecuterBuilder(systemCommandGroupList);
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterTypeBuilder#java(java.lang.String)
     */
    @Override
    public JavaSystemCommandExecuterBuilder java(String main) {
        return new JavaSystemCommandExecuterBuilder(systemCommandGroupList).javaMain(main);
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterTypeBuilder#docker(java.lang.String)
     */
    @Override
    public DockerSystemCommandExecuterBuilder docker(String image) {
        return new DockerSystemCommandExecuterBuilder(systemCommandGroupList);
    }

}
