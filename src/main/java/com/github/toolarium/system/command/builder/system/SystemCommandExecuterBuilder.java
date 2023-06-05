/*
 * SystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder.system;

import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import com.github.toolarium.system.command.dto.list.SystemCommandGroupList;
import java.util.Map;
import java.util.Set;


/**
 * The system command executer builder
 * 
 * @author patrick
 */
public final class SystemCommandExecuterBuilder extends AbstractCommandExecuterBuilder {


    /**
     * Constructor for SystemCommandExecuterBuilder
     */
    public SystemCommandExecuterBuilder() {
        this(new SystemCommandGroupList());
    }

    
    /**
     * Constructor for SystemCommandExecuterBuilder
     * 
     * @param systemCommandGroupList the system command group list
     */
    public SystemCommandExecuterBuilder(ISystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
    }

    
    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#shell(java.lang.String[])
     */
    @Override
    public SystemCommandExecuterBuilder shell(String... shell) {
        return (SystemCommandExecuterBuilder)super.shell(shell);
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#user(java.lang.String)
     */
    @Override
    public SystemCommandExecuterBuilder user(String user) {
        return (SystemCommandExecuterBuilder)super.user(user);
    }
    

    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#workingPath(java.lang.String)
     */
    @Override
    public SystemCommandExecuterBuilder workingPath(String workingPath) {
        return (SystemCommandExecuterBuilder)super.workingPath(workingPath);
    }

    
    /**
     * Set the os
     *
     * @param os the os
     * @return the system command executer builder
    @Override
    public SystemCommandExecuterBuilder os(String os) {
        return (SystemCommandExecuterBuilder)super.os(os);
    }
     */

    
    /**
     * Set the os version
     *
     * @param os the os version
     * @return the system command executer builder
    @Override
    public SystemCommandExecuterBuilder osVersion(String osVersion) {
        return (SystemCommandExecuterBuilder)super.osVersion(osVersion);
    }
    */

    
    /**
     * Set the architecture
     *
     * @param architecture the architecture
     * @return the system command executer builder
    @Override
    public SystemCommandExecuterBuilder architecture(String architecture) {
        return (SystemCommandExecuterBuilder)super.architecture(architecture);
    }
    */


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#environmentVariable(java.lang.String, java.lang.String)
     */
    @Override
    public SystemCommandExecuterBuilder environmentVariable(String key, String value) {
        return (SystemCommandExecuterBuilder)super.environmentVariable(key, value);
    }

    
    /**
     * Add a new command 
     *
     * @param command the command to add
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder command(String command) {
        super.command(command);
        return this;
    }


    /**
     * Add a new command 
     *
     * @param command the command to add
     * @param displayCommand the command to add as display
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder command(String command, String displayCommand) {
        super.command(command, displayCommand);
        return this;
    }


    /**
     * Add an additional part of the command, e.g. java properties
     *
     * @param keyValueSettings the key / value settings
     * @param keyPrefix the key prefix or null
     * @param encapsulateValue true to encapsulate values
     * @param encapsulateExpression true to encapsulate the full expression
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder command(final Map<String, String> keyValueSettings, final String keyPrefix, final boolean encapsulateValue, final boolean encapsulateExpression) {
        super.command(keyValueSettings, keyPrefix, encapsulateValue, encapsulateExpression);
        return this;
    }


    /**
     * Add an additional part of the command, e.g. java properties
     *
     * @param keyValueSettings the key / value settings
     * @param keyPrefix the key prefix or null
     * @param encapsulateValue true to encapsulate values
     * @param senstivieSettings the sensitive settings
     * @param encapsulateExpression true to encapsulate the full expression
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder command(final Map<String, String> keyValueSettings, final String keyPrefix, final boolean encapsulateValue, final boolean encapsulateExpression, final Set<String> senstivieSettings) {
        super.command(keyValueSettings, keyPrefix, encapsulateValue, encapsulateExpression, senstivieSettings);
        return this;
    }

    
    /**
     * @see com.github.toolarium.system.command.builder.system.AbstractCommandExecuterBuilder#childBuild(com.github.toolarium.system.command.dto.list.ISystemCommandGroupList)
     */
    @Override
    protected void childBuild(ISystemCommandGroupList systemCommandGroupList) throws IllegalArgumentException {
    }
}
