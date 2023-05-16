/*
 * SystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import com.github.toolarium.system.command.dto.SystemCommand;
import com.github.toolarium.system.command.process.env.dto.ProcessEnvironment;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


/**
 * The system command executer builder
 * 
 * @author patrick
 */
public final class SystemCommandExecuterBuilder {
    private LinkedList<SystemCommand> systemCommandList;


    /**
     * Constructor for SystemCommandExecuterBuilder
     */
    private SystemCommandExecuterBuilder() {
        systemCommandList = new LinkedList<>();
    }


    /**
     * Create a new builder
     *
     * @return the builder
     */
    public static SystemCommandExecuterBuilder create() {
        return new SystemCommandExecuterBuilder().addSystemCommand();
    }

    
    /**
     * Add a new system command
     * 
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder addSystemCommand() {
        systemCommandList.add(new SystemCommand(new ProcessEnvironment()));
        return this;
    }

    
    /**
     * Add a new jvm / java system command
     * 
     * @param main the java main class
     * @return the jvm / java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder addJVMSystemCommand(String main) {
        return new JavaSystemCommandExecuterBuilder(this).main(main);
    }

    
    /**
     * Set the shell
     *
     * @param shell the shell
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder shell(String... shell) {
        getCurrentSystemCommand().setShell(Arrays.asList(shell));
        return this;
    }


    /**
     * Set the user
     *
     * @param user the user
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder user(String user) {
        getCurrentProcessEnvironment().setUser(user);
        return this;
    }
    
    
    /**
     * Set the working path
     *
     * @param workingPath the workingPath
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder workingPath(String workingPath) {
        getCurrentProcessEnvironment().setWorkingPath(new File(workingPath).getAbsolutePath());
        return this;
    }

    
    /**
     * Set the os
     *
     * @param os the os
     * @return the system command executer builder
    public SystemCommandExecuterBuilder os(String os) {
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
    public SystemCommandExecuterBuilder osVersion(String osVersion) {
        getCurrentProcessEnvironment().setOSversion(osVersion);
        return this;
    }
    */

    
    /**
     * Set the architecture
     *
     * @param architecture the architecture
     * @return the system command executer builder
    public SystemCommandExecuterBuilder architecture(String architecture) {
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
    public SystemCommandExecuterBuilder environmentVariable(String key, String value) {
        getCurrentProcessEnvironment().getEnvironmentVariables().put(key, value);
        return this;
    }

    
    /**
     * Add a new command 
     *
     * @param command the command to add
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder addToCommand(String command) {
        return addToCommand(command, command);
    }

    
    /**
     * Add a new command 
     *
     * @param command the command to add
     * @param displayCommand the command to add as display
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder addToCommand(String command, String displayCommand) {
        getCurrentSystemCommand().add(command, displayCommand);
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
        return addToCommand(keyValueSettings, keyPrefix, escapeValue, null);
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
        return SystemCommandExecuterFactory.getInstance().createSystemCommandExecuter(systemCommandList);
    }

    
    /**
     * Get the current system command
     * 
     * @return the current system command 
     */
    protected SystemCommand getCurrentSystemCommand() {
        return systemCommandList.getLast();
    }

    
    /**
     * Get the current system command
     * 
     * @return the system command 
     */
    protected ProcessEnvironment getCurrentProcessEnvironment() {
        return (ProcessEnvironment)getCurrentSystemCommand().getProcessEnvironment();
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
                builder.append(SystemCommand.SPACE);
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
