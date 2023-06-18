/*
 * ProcessEnvironment.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto.env;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Implements the {@link IProcessEnvironment}.
 * 
 * @author patrick
 */
public class ProcessEnvironment implements IProcessEnvironment, Serializable {
    private static final long serialVersionUID = -1324761748179122695L;
    private String user;
    private Boolean isSudoUser;
    private String workingPath;
    private Map<String, String> environmentVariables;
    private String os;
    private String osVersion;
    private String architecture;

    
    /**
     * Constructor for ProcessEnvironment
     */
    public ProcessEnvironment() {
        isSudoUser = false;
        if (System.getProperty("user.name") != null) {
            user = System.getProperty("user.name").trim();
        }
        
        workingPath = ".";
        if (System.getProperty("user.dir") != null) {
            workingPath = new File(System.getProperty("user.dir").trim()).getPath();
        }

        environmentVariables = new HashMap<>();
        environmentVariables.putAll(System.getenv());

        os = System.getProperty("os.name").trim().toLowerCase();
        osVersion = System.getProperty("os.version").trim();
        architecture = System.getProperty("os.arch").trim();
    }
    

    /**
     * @see com.github.toolarium.system.command.dto.env.IProcessEnvironment#getUser()
     */
    @Override
    public String getUser() {
        return user;
    }

    
    /**
     * Set the user
     *
     * @param user the user
     */
    public void setUser(String user) {
        this.user = user;
        this.isSudoUser = true;
    }


    /**
     * @see com.github.toolarium.system.command.dto.env.IProcessEnvironment#isSudoUser()
     */
    @Override
    public boolean isSudoUser() {
        return isSudoUser;
    }


    /**
     * @see com.github.toolarium.system.command.dto.env.IProcessEnvironment#getWorkingPath()
     */
    @Override
    public String getWorkingPath() {
        return workingPath;
    }

    
    /**
     * Set the working path
     * 
     * @param workingPath the working path
     */
    public void setWorkingPath(String workingPath) {        
        this.workingPath = workingPath;
    }

        
    /**
     * @see com.github.toolarium.system.command.dto.env.IProcessEnvironment#getEnvironmentVariables()
     */
    @Override
    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }


    /**
     * @see com.github.toolarium.system.command.dto.env.IProcessEnvironment#getEnvironmentVariable(java.lang.String)
     */
    @Override
    public String getEnvironmentVariable(String key) {
        return environmentVariables.get(key);
    }

    
    /**
     * Set the environment variable key / value
     *
     * @param key the key
     * @param value the value
     * @return the previous environment variable value
     */
    public String setEnvironmentVariable(String key, String value) {
        return environmentVariables.put(key, value);
    }

    
    /**
     * Set the environment variable key / value
     *
     * @param environmentVariables the environment variables to set
     */
    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    
    /**
     * Clear the environment variables
     */
    public void clearEnvironmentVariables() {
        environmentVariables.clear();
    }

    
    /**
     * @see com.github.toolarium.system.command.dto.env.IProcessEnvironment#getOS()
     */
    @Override
    public String getOS() {
        return os;
    }
    

    /**
     * @see com.github.toolarium.system.command.dto.env.IProcessEnvironment#getOSVersion()
     */
    @Override
    public String getOSVersion() {
        return osVersion;
    }
    

    /**
     * @see com.github.toolarium.system.command.dto.env.IProcessEnvironment#getArchitecture()
     */
    @Override
    public String getArchitecture() {
        return architecture;
    }

    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(user, isSudoUser, workingPath, environmentVariables);
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
        
        ProcessEnvironment other = (ProcessEnvironment) obj;
        return Objects.equals(user, other.user)
                && Objects.equals(isSudoUser, other.isSudoUser)
                && Objects.equals(workingPath, other.workingPath)
                && Objects.equals(environmentVariables, other.environmentVariables);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ProcessEnvironment [user=" + user + ", isSudoUser=" + isSudoUser + ", workingPath=" + workingPath + ", environmentVariables=" + environmentVariables +  "]";
    }
}
