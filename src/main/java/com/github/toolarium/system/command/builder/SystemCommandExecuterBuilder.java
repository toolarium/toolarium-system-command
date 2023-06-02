/*
 * SystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;

import com.github.toolarium.system.command.dto.ISystemCommandGroupList;
import com.github.toolarium.system.command.dto.SystemCommandGroupList;
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
     * Add a new command 
     *
     * @param command the command to add
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder command(String command) {
        getSystemCommand().add(command, command);
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
        getSystemCommand().add(command, displayCommand);
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
        command(keyValueSettings, keyPrefix, encapsulateValue, encapsulateExpression, null);
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
        getSystemCommand().add(toString(keyValueSettings, keyPrefix, encapsulateValue, encapsulateExpression, null), toString(keyValueSettings, keyPrefix, encapsulateValue, encapsulateExpression, senstivieSettings));
        return this;
    }

    
    /**
     * @see com.github.toolarium.system.command.builder.AbstractCommandExecuterBuilder#childBuild(com.github.toolarium.system.command.dto.ISystemCommandGroupList)
     */
    @Override
    protected void childBuild(ISystemCommandGroupList systemCommandGroupList) throws IllegalArgumentException {
    }
}
