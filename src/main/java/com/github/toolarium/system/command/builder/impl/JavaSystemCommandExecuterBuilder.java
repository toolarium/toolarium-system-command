/*
 * JavaSystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder.impl;

import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;


/**
 * The java system command executer builder
 * 
 * @author patrick
 */
public class JavaSystemCommandExecuterBuilder extends AbstractJavaSystemCommandExecuteBuilder {
    private String main;
    
    
    /**
     * Constructor for JavaSystemCommandExecuterBuilder
     * 
     * @param systemCommandGroupList the system command group list
     */
    public JavaSystemCommandExecuterBuilder(ISystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
        this.main = null;
    }

    
    /**
     * Set the java main
     *
     * @param main the java main
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder javaMain(String main) {
        if (main != null) {
            this.main = main.trim();
        }
        
        return this;
    }


    /**
     * @see com.github.toolarium.system.command.builder.impl.AbstractJavaSystemCommandExecuteBuilder#javaMain()
     */
    @Override
    protected String javaMain() {
        if (main == null || main.isBlank()) {
            throw new IllegalArgumentException("Missing java main class!");
        }
        return main;
    }
}
