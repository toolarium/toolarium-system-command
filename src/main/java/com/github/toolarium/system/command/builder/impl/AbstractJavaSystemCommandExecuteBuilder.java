/*
 * AbstractJavaSystemCommandExecuteBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder.impl;

import com.github.toolarium.system.command.dto.SystemCommand;
import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The abstract java system command executer builder
 * 
 * @author patrick
 */
public abstract class AbstractJavaSystemCommandExecuteBuilder extends AbstractCommandExecuterBuilder {
    private String jdkPath;
    private String javaMemorySettings;
    private String classPath;
    private String javaExecutable;
    private String javaAgent;
    private Map<String, String> systemProperties;
    private Set<String> senstivieSettings;
    private Map<String, String> parameters;
    //private Map<String, String> 
    
    
    /**
     * Constructor for AbstractJavaSystemCommandExecuteBuilder
     * 
     * @param systemCommandGroupList the system command group list
     */
    public AbstractJavaSystemCommandExecuteBuilder(ISystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
        this.jdkPath = null;
        this.javaMemorySettings = null;
        this.javaAgent = null;
        javaExecutable = "java";
        systemProperties = new LinkedHashMap<>();
        senstivieSettings = new HashSet<>();
        parameters = new LinkedHashMap<>();
    }

    
    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#shell(java.lang.String[])
     */
    @Override
    public AbstractJavaSystemCommandExecuteBuilder shell(String... shell) {
        return (AbstractJavaSystemCommandExecuteBuilder)super.shell(shell);
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#user(java.lang.String)
     */
    @Override
    public AbstractJavaSystemCommandExecuteBuilder user(String user) {
        return (AbstractJavaSystemCommandExecuteBuilder)super.user(user);
    }
    

    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#workingPath(java.lang.String)
     */
    @Override
    public AbstractJavaSystemCommandExecuteBuilder workingPath(String workingPath) {
        return (AbstractJavaSystemCommandExecuteBuilder)super.workingPath(workingPath);
    }

    
    /**
     * Set the os
     *
     * @param os the os
     * @return the system command executer builder
    @Override
    public AbstractJavaSystemCommandExecuteBuilder os(String os) {
        return (AbstractJavaSystemCommandExecuteBuilder)super.os(os);
    }
     */

    
    /**
     * Set the os version
     *
     * @param os the os version
     * @return the system command executer builder
    @Override
    public AbstractJavaSystemCommandExecuteBuilder osVersion(String osVersion) {
        return (AbstractJavaSystemCommandExecuteBuilder)super.osVersion(osVersion);
    }
    */

    
    /**
     * Set the architecture
     *
     * @param architecture the architecture
     * @return the system command executer builder
    @Override
    public AbstractJavaSystemCommandExecuteBuilder architecture(String architecture) {
        return (AbstractJavaSystemCommandExecuteBuilder)super.architecture(architecture);
    }
    */


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#environmentVariable(java.lang.String, java.lang.String)
     */
    @Override
    public AbstractJavaSystemCommandExecuteBuilder environmentVariable(String key, String value) {
        return (AbstractJavaSystemCommandExecuteBuilder)super.environmentVariable(key, value);
    }

    
    /**
     * Set the jdk path
     *
     * @param jdkPath the jdk path
     * @return the java system command executer builder
     */
    public AbstractJavaSystemCommandExecuteBuilder jdkPath(String jdkPath) {
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
    public AbstractJavaSystemCommandExecuteBuilder javaMemory(String initialSize, String maxSize) {
        this.javaMemorySettings = "";

        if (initialSize != null && !initialSize.isBlank()) {
            if (!javaMemorySettings.isBlank()) {
                javaMemorySettings += SystemCommand.SPACE;
            }
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
    public AbstractJavaSystemCommandExecuteBuilder javaExecutable(String javaExecutable) {
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
    public AbstractJavaSystemCommandExecuteBuilder javaAgent(String javaAgent) {
        if (javaAgent != null) {
            this.javaAgent = javaAgent.trim();
        }
        
        return this;
    }

    
    /**
     * Set the jvm user
     *
     * @param user the jvm user
     * @return the java system command executer builder
     */
    public AbstractJavaSystemCommandExecuteBuilder javaUser(String user) {
        if (user != null) {
            systemProperties.put("user.name", user.trim());
        }
        
        return this;
    }
    
    
    /**
     * Set the java temp path
     *
     * @param tempPath the temp path
     * @return the java system command executer builder
     */
    public AbstractJavaSystemCommandExecuteBuilder javaTempPath(String tempPath) {
        systemProperties.put("java.io.tmpdir", tempPath.trim());
        return this;
    }

    
    /**
     * Set the java headless
     *
     * @return the java system command executer builder
     */
    public AbstractJavaSystemCommandExecuteBuilder javaHeadless() {
        systemProperties.put("java. awt. headless", "true");
        return this;
    }

    
    /**
     * Add a system property
     *
     * @param key the key
     * @param value the value
     * @return the java system command executer builder
     */
    public AbstractJavaSystemCommandExecuteBuilder systemProperty(String key, String value) {
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
    public AbstractJavaSystemCommandExecuteBuilder systemProperty(String key, String value, boolean isSenstivieValue) {
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
    public AbstractJavaSystemCommandExecuteBuilder parameter(String parameter) {
        parameters.put(parameter, "");
        return this;
    }


    /**
     * @see com.github.toolarium.system.command.builder.impl.AbstractCommandExecuterBuilder#childBuild(com.github.toolarium.system.command.dto.list.ISystemCommandGroupList)
     * @throws IllegalArgumentException In case of an invalid argument
     */
    @Override
    protected void childBuild(ISystemCommandGroupList systemCommandGroupList) throws IllegalArgumentException {
        if (jdkPath != null && !jdkPath.isBlank()) {
            command(jdkPath);
        }
        
        command(javaExecutable, javaExecutable);
        
        if (classPath != null && !classPath.isBlank()) {
            command("-cp" + SystemCommand.SPACE + classPath);
        }
        
        if (javaAgent != null) {
            command("-javaagent" + SystemCommand.SPACE + javaAgent);
        }
        
        if (javaMemorySettings != null && !javaMemorySettings.isBlank()) {
            command(javaMemorySettings);
        }

        if (systemProperties != null && !systemProperties.isEmpty()) {
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            boolean encapsulateExpression;
            if (isWindows) {
                encapsulateExpression = true;
            } else {
                encapsulateExpression = false;
                systemCommandGroupList.forceRunAsScript();
            }
            command(systemProperties, "-D", !encapsulateExpression, encapsulateExpression, senstivieSettings);
        }

        command(javaMain());

        if (parameters != null && !parameters.isEmpty()) {
            command(parameters, null, false, true);
        }
    }


    /**
     * Get the java main
     * 
     * @return the java main 
     * @throws IllegalArgumentException In case of an invalid input
     */
    protected abstract String javaMain();
}