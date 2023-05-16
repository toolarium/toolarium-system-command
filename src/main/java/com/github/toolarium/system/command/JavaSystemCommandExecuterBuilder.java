/*
 * JavaSystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import com.github.toolarium.system.command.dto.SystemCommand;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * Java system command executer builder
 * 
 * @author patrick
 */
public final class JavaSystemCommandExecuterBuilder {
    private SystemCommandExecuterBuilder parent;
    private String jdkPath;
    private String javaMemorySettings;
    private String classPath;
    private String javaExecutable;
    private String javaAgent;
    private String main;
    private Map<String, String> systemProperties;
    private Set<String> senstivieSettings;
    private Map<String, String> parameters;
    //private Map<String, String> 
    
    
    /**
     * Constructor for JavaSystemCommandExecuterBuilder
     * 
     * @param parent the system command executer builder
     */
    public JavaSystemCommandExecuterBuilder(SystemCommandExecuterBuilder parent) {
        this.parent = parent;
        this.jdkPath = null;
        this.javaMemorySettings = null;
        this.javaAgent = null;
        this.main = null;
        javaExecutable = "java";
        systemProperties = new LinkedHashMap<>();
        senstivieSettings = new HashSet<>();
        parameters = new LinkedHashMap<>();
    }

    
    /**
     * Set the jdk path
     *
     * @param jdkPath the jdk path
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder jdkPath(String jdkPath) {
        if (jdkPath != null) {
            this.jdkPath = jdkPath.trim();
        }
        
        return this;
    }

    
    /**
     * Set the java memory settings
     *
     * @param initialSize The initiali size in bytes, e.g. 512M. Sets the initial size of the Java heap. The default size is 2097152 (2MB). 
     *                    The values must be a multiple of, and greater than, 1024 bytes (1KB).
     *                    (The -server flag increases the default size to 32M.).
     *                    Sets the initial Java heap size for the Eden generation. The default value is 640K. 
     * @param maxSize The max size in bytes, e.g. 4G. Sets the maximum size to which the Java heap can grow. The default size is 64M.
     *                (The -server flag increases the default size to 128M.)
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder javaMemorySettings(String initialSize, String maxSize) {
        this.javaMemorySettings = "";

        if (initialSize != null && !initialSize.isBlank()) {
            this.javaMemorySettings += "-Xms" + initialSize.trim();
        }

        if (maxSize != null && !maxSize.isBlank()) {
            if (!javaMemorySettings.isBlank()) {
                javaMemorySettings += SystemCommand.SPACE;
            }
            this.javaMemorySettings += "-Xmx" + maxSize.trim();
        }
        
        return this;
    }

    
    /**
     * Set the java executable to set
     *
     * @param javaExecutable the java executable to set
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder javaExecutable(String javaExecutable) {
        if (javaExecutable != null) {
            this.javaExecutable = javaExecutable.trim();
        }
        
        return this;
    }

    
    /**
     * Set the java agent to set
     *
     * @param javaAgent the java agent to set
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder javaAgent(String javaAgent) {
        if (javaAgent != null) {
            this.javaAgent = javaAgent.trim();
        }
        
        return this;
    }

    
    /**
     * Set the java main
     *
     * @param main the java main
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder main(String main) {
        if (main != null) {
            this.main = main.trim();
        }
        
        return this;
    }

    
    /**
     * Set the jvm user
     *
     * @param user the jvm user
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder user(String user) {
        if (user != null) {
            systemProperties.put("user.name", user.trim());
        }
        
        return this;
    }
    
    
    /**
     * Set the working path
     *
     * @param workingPath the workingPath
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder workingPath(String workingPath) {
        parent.workingPath(workingPath);
        return this;
    }

    
    /**
     * Set the java temp path
     *
     * @param tempPath the temp path
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder tempPath(String tempPath) {
        systemProperties.put("java.io.tmpdir", tempPath.trim());
        return this;
    }

    
    /**
     * Set the java headless
     *
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder headless() {
        systemProperties.put("java. awt. headless", "true");
        return this;
    }

    
    /**
     * Add an environment variable
     *
     * @param key the key
     * @param value the value
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder environmentVariable(String key, String value) {
        parent.environmentVariable(key, value);
        return this;
    }

    
    /**
     * Add a system property
     *
     * @param key the key
     * @param value the value
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder systemProperty(String key, String value) {
        return systemProperty(key, value, false);
    }

    
    /**
     * Add a system property
     *
     * @param key the key
     * @param value the value
     * @param isSenstivieValue true if the value is sensitive
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder systemProperty(String key, String value, boolean isSenstivieValue) {
        systemProperties.put(key, value);
        
        if (isSenstivieValue && !senstivieSettings.contains(key)) {
            senstivieSettings.add(key);
        }
        return this;
    }

    
    /**
     * Add a program parameter
     *
     * @param parameter program parameter to add
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder parameters(String parameter) {
        parameters.put(parameter, "");
        return this;
    }

    
    /**
     * Add a new jvm / java system command
     * 
     * @param main the java main class
     * @return the jvm / java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder addJVMSystemCommand(String main) {
        buildJavaCommand();
        return parent.addJVMSystemCommand(main);
    }
    
    
    /**
     * Add a new system command
     * 
     * @return the system command executer builder
     */
    public SystemCommandExecuterBuilder addSystemCommand() {
        buildJavaCommand();
        return parent.addSystemCommand();
    }

    
    /**
     * Build the system executer
     *
     * @return the system executer
     */
    public ISystemCommandExecuter build() {
        buildJavaCommand();
        return parent.build();
    }


    /**
     * Build the java command
     * 
     * @throws IllegalArgumentException In case of invalid parameter combination
     */
    private void buildJavaCommand() throws IllegalArgumentException {
        
        if (jdkPath != null && !jdkPath.isBlank()) {
            parent.addToCommand(jdkPath);
        }
        
        parent.addToCommand(javaExecutable, javaExecutable);
        
        if (javaMemorySettings != null && !javaMemorySettings.isBlank()) {
            parent.addToCommand(javaMemorySettings);
        }
        
        if (classPath != null && !classPath.isBlank()) {
            parent.addToCommand("-cp" + SystemCommand.SPACE + classPath);
        }
        
        if (systemProperties != null && !systemProperties.isEmpty()) {
            parent.addToCommand(systemProperties, "-D", false, senstivieSettings);
        }
        
        if (javaAgent != null) {
            parent.addToCommand("-javaagent" + SystemCommand.SPACE + javaAgent);
        }
        
        if (main == null || main.isBlank()) {
            throw new IllegalArgumentException("Missing java main class!");
        }
        parent.addToCommand(main);
        
        if (parameters != null && !parameters.isEmpty()) {
            parent.addToCommand(parameters, null, false);
        }
    }
}
