/*
 * IProcessEnvironment.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import java.util.Map;

/**
 * Defines the process environment
 * 
 * @author patrick
 */
public interface IProcessEnvironment {
    
    /**
     * Get the user.
     *
     * @return the user
     */
    String getUser();

    
    /**
     * Get the working path
     * 
     * @return the working path
     */
    String getWorkingPath();

    
    /**
     * Get the environment variables
     *
     * @return the environment variables
     */
    Map<String, String> getEnvironmentVariables();

    
    /**
     * Get the environment variable value
     *
     * @param key the key
     * @return the environment variable value
     */
    String getEnvironmentVariable(String key);
    
    
    /**
     * Get the operating system
     *
     * @return the operating system
     */
    String getOS();
    
    
    /**
     * Get the operation system version
     *
     * @return the operation system version
     */
    String getOSVersion();
    
    
    /**
     * Get the device architecture
     *
     * @return the device architecture
     */
    String getArchitecture();
}
