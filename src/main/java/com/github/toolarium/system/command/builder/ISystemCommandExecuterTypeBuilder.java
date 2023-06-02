/*
 * ISystemCommandExecuterTypeBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;


/**
 * Defines the system command executer type builder
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
     * Get a java system command executer builder
     *
     * @param main the java main
     * @return the java system command executer builder
     */
    JavaSystemCommandExecuterBuilder java(String main);
    
    
    /**
     * Get a docker system command executer builder
     *
     * @param image the docker image
     * @return the docker system command executer builder
     */
    DockerSystemCommandExecuterBuilder docker(String image);
}
