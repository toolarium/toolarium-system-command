/*
 * ISystemCommandExecuterTypeBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;

import com.github.toolarium.system.command.builder.impl.DockerSystemCommandExecuterBuilder;
import com.github.toolarium.system.command.builder.impl.JarSystemCommandExecuterBuilder;
import com.github.toolarium.system.command.builder.impl.JavaSystemCommandExecuterBuilder;
import com.github.toolarium.system.command.builder.impl.SystemCommandExecuterBuilder;


/**
 * The system command executer type builder
 * 
 * @author patrick
 */
public interface ISystemCommandExecuterTypeBuilder {
    
    /**
     * Get a system command executer builder
     *
     * @return the system command executer builder
     */
    SystemCommandExecuterBuilder system();

    
    /**
     * Get a jar system command executer builder
     *
     * @param jarFile the jar file
     * @return the java system command executer builder
     */
    JarSystemCommandExecuterBuilder jar(String jarFile);

    
    /**
     * Get a java system command executer builder
     *
     * @param main the java main
     * @return the java system command executer builder
     */
    JavaSystemCommandExecuterBuilder java(String main);
    
    
    /**
     * Get a docker system command executer builder
     *
     * @return the docker system command executer builder
     */
    DockerSystemCommandExecuterBuilder docker();
}
