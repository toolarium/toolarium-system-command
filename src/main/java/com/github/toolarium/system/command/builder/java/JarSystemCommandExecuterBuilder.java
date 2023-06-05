/*
 * JarSystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder.java;

import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;


/**
 * The jar system command executer builder
 *  
 * @author patrick
 */
public class JarSystemCommandExecuterBuilder extends AbstractJavaSystemCommandExecuteBuilder {
    private String jarParameter;
    private String jar;
    
    
    /**
     * Constructor for JarSystemCommandExecuterBuilder
     * 
     * @param systemCommandGroupList the system command group list
     */
    public JarSystemCommandExecuterBuilder(ISystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
        this.jar = null;
        jarParameter = "-jar";
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
     * @see com.github.toolarium.system.command.builder.java.AbstractJavaSystemCommandExecuteBuilder#finalizeJavaExecutable()
     */
    @Override
    protected void finalizeJavaExecutable() {
        command(jarParameter);
    }


    /**
     * @see com.github.toolarium.system.command.builder.java.AbstractJavaSystemCommandExecuteBuilder#javaMain()
     */
    @Override
    protected String javaMain() {
        if (jar == null || jar.isBlank()) {
            throw new IllegalArgumentException("Missing jar / java main class!");
        }
        return jar;
    }
}
