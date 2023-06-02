/*
 * JavaSystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;

import com.github.toolarium.system.command.dto.ISystemCommandGroupList;
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
public class JavaSystemCommandExecuterBuilder extends AbstractCommandExecuterBuilder {
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
     * @param systemCommandGroupList the system command group list
     */
    public JavaSystemCommandExecuterBuilder(ISystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
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
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#shell(java.lang.String[])
     */
    @Override
    public JavaSystemCommandExecuterBuilder shell(String... shell) {
        return (JavaSystemCommandExecuterBuilder)super.shell(shell);
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#user(java.lang.String)
     */
    @Override
    public JavaSystemCommandExecuterBuilder user(String user) {
        return (JavaSystemCommandExecuterBuilder)super.user(user);
    }
    

    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#workingPath(java.lang.String)
     */
    @Override
    public JavaSystemCommandExecuterBuilder workingPath(String workingPath) {
        return (JavaSystemCommandExecuterBuilder)super.workingPath(workingPath);
    }

    
    /**
     * Set the os
     *
     * @param os the os
     * @return the system command executer builder
    @Override
    public AbstractCommandExecuterBuilder os(String os) {
        return (JavaSystemCommandExecuterBuilder)super.os(os);
    }
     */

    
    /**
     * Set the os version
     *
     * @param os the os version
     * @return the system command executer builder
    @Override
    public ISystemCommandExecuterBuilder osVersion(String osVersion) {
        return (JavaSystemCommandExecuterBuilder)super.osVersion(osVersion);
    }
    */

    
    /**
     * Set the architecture
     *
     * @param architecture the architecture
     * @return the system command executer builder
    @Override
    public ISystemCommandExecuterBuilder architecture(String architecture) {
        return (JavaSystemCommandExecuterBuilder)super.architecture(architecture);
    }
    */


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#environmentVariable(java.lang.String, java.lang.String)
     */
    @Override
    public JavaSystemCommandExecuterBuilder environmentVariable(String key, String value) {
        return (JavaSystemCommandExecuterBuilder)super.environmentVariable(key, value);
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
    public JavaSystemCommandExecuterBuilder javaMemory(String initialSize, String maxSize) {
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
    public JavaSystemCommandExecuterBuilder javaMain(String main) {
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
    public JavaSystemCommandExecuterBuilder javaUser(String user) {
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
    public JavaSystemCommandExecuterBuilder javaTempPath(String tempPath) {
        systemProperties.put("java.io.tmpdir", tempPath.trim());
        return this;
    }

    
    /**
     * Set the java headless
     *
     * @return the java system command executer builder
     */
    public JavaSystemCommandExecuterBuilder javaHeadless() {
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
    public JavaSystemCommandExecuterBuilder parameter(String parameter) {
        parameters.put(parameter, "");
        return this;
    }


    /**
     * @see com.github.toolarium.system.command.builder.AbstractCommandExecuterBuilder#childBuild(com.github.toolarium.system.command.dto.ISystemCommandGroupList)
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
                systemCommandGroupList.forceSystenCommandGroupRunAsScript();
            }
            command(systemProperties, "-D", !encapsulateExpression, encapsulateExpression, senstivieSettings);
        }

        if (main == null || main.isBlank()) {
            throw new IllegalArgumentException("Missing java main class!");
        }
        command(main);

        if (parameters != null && !parameters.isEmpty()) {
            command(parameters, null, false, true);
        }
    }
}
