/*
 * DockerSystemCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder.docker;

import com.github.toolarium.system.command.builder.system.AbstractCommandExecuterBuilder;
import com.github.toolarium.system.command.dto.list.SystemCommandGroupList;
import com.github.toolarium.system.command.util.OSUtil;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The docker system command executer builder
 * 
 * @author patrick
 */
public class DockerSystemCommandExecuterBuilder extends AbstractCommandExecuterBuilder {
    private String dockerExecutable;
    private String dockerCommand;
    private Boolean interactive;
    private Boolean remove;
    private Boolean nameTag;
    private String name;
    private Integer port;
    private String image;
    private Map<String, String> parameters;

    
    /**
     * Constructor for DockerSystemCommandExecuterBuilder
     *
     * @param systemCommandGroupList the system command group list
     */
    public DockerSystemCommandExecuterBuilder(SystemCommandGroupList systemCommandGroupList) {
        super(systemCommandGroupList);
        dockerExecutable = "docker";
        dockerCommand = null;
        interactive = null;
        remove = null;
        nameTag = null;
        name = null;
        port = null;
        image = null;
        parameters = new LinkedHashMap<>();
    }
    
    
    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#shell(java.lang.String[])
     */
    @Override
    public DockerSystemCommandExecuterBuilder shell(String... shell) {
        return (DockerSystemCommandExecuterBuilder)super.shell(shell);
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#user(java.lang.String)
     */
    @Override
    public DockerSystemCommandExecuterBuilder user(String user) {
        return (DockerSystemCommandExecuterBuilder)super.user(user);
    }
    

    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#workingPath(java.lang.String)
     */
    @Override
    public DockerSystemCommandExecuterBuilder workingPath(String workingPath) {
        return (DockerSystemCommandExecuterBuilder)super.workingPath(workingPath);
    }

    
    /**
     * Set the os
     *
     * @param os the os
     * @return the system command executer builder
    @Override
    public DockerSystemCommandExecuterBuilder os(String os) {
        return (DockerSystemCommandExecuterBuilder)super.os(os);
    }
     */

    
    /**
     * Set the os version
     *
     * @param os the os version
     * @return the system command executer builder
    @Override
    public DockerSystemCommandExecuterBuilder osVersion(String osVersion) {
        return (DockerSystemCommandExecuterBuilder)super.osVersion(osVersion);
    }
    */

    
    /**
     * Set the architecture
     *
     * @param architecture the architecture
     * @return the system command executer builder
    @Override
    public DockerSystemCommandExecuterBuilder architecture(String architecture) {
        return (DockerSystemCommandExecuterBuilder)super.architecture(architecture);
    }
    */


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#environmentVariable(java.lang.String, java.lang.String)
     */
    @Override
    public DockerSystemCommandExecuterBuilder environmentVariable(String key, String value) {
        return (DockerSystemCommandExecuterBuilder)super.environmentVariable(key, value);
    }

    
    /**
     * Set the docker image
     *
     * @param dockerExecutable the docker image
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder dockerExecutable(String dockerExecutable) {
        if (dockerExecutable != null) {
            this.dockerExecutable = dockerExecutable.trim();
        }
        
        return this;
    }

    
    /**
     * Set the docker command
     *
     * @param dockerCommand the docker command
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder dockerCommand(String dockerCommand) {
        if (dockerCommand != null) {
            this.dockerCommand = dockerCommand.trim();
        }
        
        return this;
    }

    
    /**
     * Interactive 
     * 
     * @param interactive true for interactive
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder interactive(boolean interactive) {
        this.interactive = interactive;
        return this;
    }
    
   
    /**
     * Automatically remove the container when it exits
     *
     * @param remove true to automatically remove the container when it exits
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder remove(boolean remove) {
        this.remove = remove;
        return this;
    }

    
    /**
     * Set the docker name
     *
     * @param name the docker name
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder name(String name) {
        if (name != null) {
            this.nameTag = Boolean.TRUE;
            this.name = name.trim();
        }
        
        return this;
    }

    
    /**
     * Set the docker port
     *
     * @param port the docker port
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder port(Integer port) {
        if (port != null) {
            this.port = port;
        }
        
        return this;
    }

    
    /**
     * Run a docker container
     *
     * @param image the image
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder run(String image) {
        if (image != null) {
            this.dockerCommand = "run";
            this.image = image.trim();
        }
        
        return this;
    }

    
    /**
     * Stop a docker container
     *
     * @param name the conatiner name to stop
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder stop(String name) {
        this.dockerCommand = "stop";
        this.name = name;
        this.nameTag = Boolean.FALSE;
        return this;
    }

    
    /**
     * Get docker images
     *
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder images() {
        this.dockerCommand = "images";
        this.name = "*";
        return this;
    }

    
    /**
     * Get docker images
     *
     * @param name the conatiner name to filter
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder images(String name) {
        this.dockerCommand = "images";
        this.name = name;
        this.nameTag = Boolean.FALSE;
        return this;
    }

    
    /**
     * Add a program parameter
     *
     * @param parameter program parameter to add
     * @return the docker system command executer builder
     */
    public DockerSystemCommandExecuterBuilder parameter(String parameter) {
        this.parameters.put(parameter, "");
        return this;
    }

    
    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#lock()
     */
    @Override
    public DockerSystemCommandExecuterBuilder lock() {
        return (DockerSystemCommandExecuterBuilder)super.lock();
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#lock(java.lang.Integer)
     */
    @Override
    public DockerSystemCommandExecuterBuilder lock(Integer lockTimeoutInSeconds) {
        return (DockerSystemCommandExecuterBuilder)super.lock(lockTimeoutInSeconds);
    }

    
    /**
     * @see com.github.toolarium.system.command.builder.system.AbstractCommandExecuterBuilder#childBuild(com.github.toolarium.system.command.dto.list.SystemCommandGroupList)
     * @throws IllegalArgumentException In case of an invalid argument
     */
    @Override
    protected void childBuild(SystemCommandGroupList systemCommandGroupList) throws IllegalArgumentException {
        command(dockerExecutable, dockerExecutable);
        command(dockerCommand, dockerCommand);

        if (interactive != null && interactive) {
            command("-it");
        }

        if (remove != null && remove) {
            command("--rm");
        }

        if (name != null && !name.isBlank()) {
            if (nameTag != null && nameTag) {
                command("--name");
            }
            
            command(name);
        }

        if (port != null) {
            command("-p");
            command(port + ":" + port);
        }

        if (image != null && !image.isBlank()) {
            command(image);
        }

        if (parameters != null && !parameters.isEmpty()) {
            command(parameters, null, false, false);
        }
        
        if (!OSUtil.getInstance().isWindows()) {
            systemCommandGroupList.forceRunAsScript();
        }
    }
}
