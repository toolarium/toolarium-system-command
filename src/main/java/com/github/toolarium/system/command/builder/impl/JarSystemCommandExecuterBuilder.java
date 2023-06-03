/*
 * JarSystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder.impl;

import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;


/**
 * The jar system command executer builder
 *  
 * @author patrick
 */
public class JarSystemCommandExecuterBuilder extends AbstractJavaSystemCommandExecuteBuilder {
    private String jar;
    
    
    /**
     * Constructor for JarSystemCommandExecuterBuilder
     * 
     * @param systemCommandGroupList the system command group list
     */
    public JarSystemCommandExecuterBuilder(ISystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
        this.jar = null;
    }

    
    /**
     * Set the jar 
     *
     * @param jar the java jar reference
     * @return the jar system command executer builder
     */
    public JarSystemCommandExecuterBuilder jar(String jar) {
        if (jar != null) {
            this.jar = jar.trim();
        }
        
        return this;
    }


    /**
     * @see com.github.toolarium.system.command.builder.impl.AbstractJavaSystemCommandExecuteBuilder#javaMain()
     */
    @Override
    protected String javaMain() {
        return jar;
    }

    
}
