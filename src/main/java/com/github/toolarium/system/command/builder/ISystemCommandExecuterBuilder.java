/*
 * ISystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;

import com.github.toolarium.system.command.executer.ISystemCommandExecuter;


/**
 * Defines the system command executer builder
 *  
 * @author patrick
 */
public interface ISystemCommandExecuterBuilder {
    
    /**
     * On success of the current command start a next command
     *
     * @return the builder
     */
    ISystemCommandExecuterTypeBuilder onSuccess();

    
    /**
     * On error of the current command start a next command
     *
     * @return the builder
     */
    ISystemCommandExecuterTypeBuilder onError();

    
    /**
     * On success or error of the current command start a next command
     *
     * @return the builder
     */
    ISystemCommandExecuterTypeBuilder onSuccessOrError();

    
    /**
     * Pipe the output of the the current command into a next command
     *
     * @return the builder
     */
    ISystemCommandExecuterTypeBuilder pipe();

    
    /**
     * Set the shell of the current command
     *
     * @param shell the shell
     * @return the system command executer builder
     */
    ISystemCommandExecuterBuilder shell(String... shell);


    /**
     * Set the user of the current command
     *
     * @param user the user
     * @return the system command executer builder
     */
    ISystemCommandExecuterBuilder user(String user);
    
    
    /**
     * Set the working path of the current command
     *
     * @param workingPath the workingPath
     * @return the system command executer builder
     */
    ISystemCommandExecuterBuilder workingPath(String workingPath);

    
    /**
     * Set the os
     *
     * @param os the os
     * @return the system command executer builder
    public ISystemCommandExecuterBuilder os(String os) {
        if (!getCurrentProcessEnvironment().getOS().equals(os)) {
        }
        return this;
    }
     */

    
    /**
     * Set the os version
     *
     * @param os the os version
     * @return the system command executer builder
    public ISystemCommandExecuterBuilder osVersion(String osVersion) {
        getCurrentProcessEnvironment().setOSversion(osVersion);
        return this;
    }
    */

    
    /**
     * Set the architecture
     *
     * @param architecture the architecture
     * @return the system command executer builder
    public ISystemCommandExecuterBuilder architecture(String architecture) {
        getCurrentProcessEnvironment().setArchitecture(architecture);
        return this;
    }
    */

    
    /**
     * Add an environment variable
     *
     * @param key the key
     * @param value the value
     * @return the system command executer builder
     */
    ISystemCommandExecuterBuilder environmentVariable(String key, String value);

    
   
    /**
     * Build the system executer
     *
     * @return the system executer
     */
    ISystemCommandExecuter build();
}
