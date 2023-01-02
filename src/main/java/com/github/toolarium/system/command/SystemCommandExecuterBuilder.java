/*
 * SystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import com.github.toolarium.system.command.dto.ProcessEnvironment;
import com.github.toolarium.system.command.dto.SystemCommand;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @author patrick
 */
public final class SystemCommandExecuterBuilder {
    private static final String SPACE = " ";
    private ProcessEnvironment processEnvironment;
    private SystemCommand systemCommand;


    /**
     * Constructor for SystemCommandExecuterBuilder
     */
    private SystemCommandExecuterBuilder() {
        processEnvironment = new ProcessEnvironment();
        systemCommand = new SystemCommand();
    }


    /**
     * Create a new builder
     *
     * @return the builder
     */
    public static SystemCommandExecuterBuilder create() {
        return new SystemCommandExecuterBuilder();
    }
    
    
    /**
     * Set the shell
     *
     * @param shell the shell
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder shell(String... shell) {
        systemCommand.setShell(Arrays.asList(shell));
        return this;
    }

    
    /**
     * Set the user
     *
     * @param user the user
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder user(String user) {
        processEnvironment.setUser(user);
        return this;
    }
    
    
    /**
     * Set the working path
     *
     * @param workingPath the workingPath
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder workingPath(String workingPath) {
        processEnvironment.setWorkingPath(workingPath);
        return this;
    }

    
    /**
     * Set the os
     *
     * @param os the os
     * @return the system command executer builder
    public SystemCommandExecuterBuilder os(String os) {
        processEnvironment.setOS(os);
        return this;
    }
    */

    
    /**
     * Set the os version
     *
     * @param os the os version
     * @return the system command executer builder
    public SystemCommandExecuterBuilder osVersion(String osVersion) {
        processEnvironment.setOSversion(osVersion);
        return this;
    }
    */

    
    /**
     * Set the architecture
     *
     * @param architecture the architecture
     * @return the system command executer builder
    public SystemCommandExecuterBuilder architecture(String architecture) {
        processEnvironment.setArchitecture(architecture);
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
    public SystemCommandExecuterBuilder environmentVariable(String key, String value) {
        processEnvironment.getEnvironmentVariables().put(key, value);
        return this;
    }

    
    /**
     * Add an additional part of the command 
     *
     * @param command the additional part of the command
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder addToCommand(String command) {
        return addToCommand(command, command);
    }

    
    /**
     * Add an additional part of the command 
     *
     * @param command the additional part of the command
     * @param displayCommand the additional part of the command to display
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder addToCommand(String command, String displayCommand) {
        systemCommand.add(command, displayCommand);
        return this;
    }

    
    /**
     * Add an additional part of the command, e.g. java properties
     *
     * @param keyValueSettings the key / value settings
     * @param keyPrefix the key prefix or null
     * @param escapeValue true to escape values
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder addToCommand(final Map<String, String> keyValueSettings, final String keyPrefix, final boolean escapeValue) {
        return addToCommand(keyValueSettings, keyPrefix, escapeValue);
    }

    
    /**
     * Add an additional part of the command, e.g. java properties
     *
     * @param keyValueSettings the key / value settings
     * @param keyPrefix the key prefix or null
     * @param escapeValue true to escape values
     * @param senstivieSettings the sensitive settings
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder addToCommand(final Map<String, String> keyValueSettings, final String keyPrefix, final boolean escapeValue, final Set<String> senstivieSettings) {
        addToCommand(toString(keyValueSettings, keyPrefix, escapeValue, null), toString(keyValueSettings, keyPrefix, escapeValue, senstivieSettings));
        return this;
    }


    /**
     * Build the system executer
     *
     * @return the system executer
     */
    public ISystemCommandExecuter build() {
        return SystemCommandExecuterFactory.getInstance().createSystemCommandExecuter(processEnvironment, systemCommand);
    }

    
    /**
     * Convert a list to a string
     *
     * @param map the map
     * @param keyPrefix the key prefix or null
     * @param escapeValue true to escape values
     * @param senstivieSettings the sensitive settings
     * @return the string
     */
    protected String toString(final Map<String, String> map, final String keyPrefix, final boolean escapeValue, final Set<String> senstivieSettings) {
        StringBuilder builder = new StringBuilder();
        
        int i = 0;
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (i > 0) {
                builder.append(SPACE);
            }

            if (keyPrefix != null && !keyPrefix.isBlank()) {
                builder.append(keyPrefix);
            }
            
            builder.append(e.getKey());
            
            if (e.getValue() != null && !e.getValue().isBlank()) {
                builder.append("=");
                
                if (senstivieSettings != null && senstivieSettings.contains(e.getKey())) {
                    builder.append("...");
                } else {
                    if (escapeValue) {
                        builder.append("\"");
                    }
                    builder.append(e.getValue());
                    if (escapeValue) {
                        builder.append("\"");
                    }
                }
            }
            
            i++;
        }

        return builder.toString();
    }
}
