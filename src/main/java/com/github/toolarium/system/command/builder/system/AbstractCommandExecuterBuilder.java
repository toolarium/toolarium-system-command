/*
 * AbstractCommandExecuterBuilder.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder.system;

import com.github.toolarium.system.command.SystemCommandExecuterFactory;
import com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder;
import com.github.toolarium.system.command.builder.ISystemCommandExecuterTypeBuilder;
import com.github.toolarium.system.command.dto.ISystemCommand.SystemCommandExecutionStatusResult;
import com.github.toolarium.system.command.dto.SystemCommand;
import com.github.toolarium.system.command.dto.env.IProcessEnvironment;
import com.github.toolarium.system.command.dto.env.ProcessEnvironment;
import com.github.toolarium.system.command.dto.group.SystemCommandGroup;
import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import com.github.toolarium.system.command.executer.ISystemCommandExecuter;
import com.github.toolarium.system.command.util.SystemCommandFactory;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * The abstract command executer builder
 * 
 * @author patrick
 */
public abstract class AbstractCommandExecuterBuilder implements ISystemCommandExecuterBuilder {
    private ISystemCommandGroupList systemCommandGroupList;
    private SystemCommand currentSystemCommand;
    private IProcessEnvironment parentProcessEnvironment;
    private List<String> parentShell;
    

    /**
     * Constructor for AbstractCommandExecuterBuilder
     * 
     * @param systemCommandGroupList the system command group list
     */
    public AbstractCommandExecuterBuilder(ISystemCommandGroupList systemCommandGroupList) {
        this.systemCommandGroupList = systemCommandGroupList;
        this.currentSystemCommand = null;
        this.parentProcessEnvironment = null;
        this.parentShell = null;

    }

    
    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#onSuccess()
     */
    @Override
    public ISystemCommandExecuterTypeBuilder onSuccess() {
        return addSystemCommand(SystemCommandExecutionStatusResult.SUCCESS);
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#onError()
     */
    @Override
    public ISystemCommandExecuterTypeBuilder onError() {
        return addSystemCommand(SystemCommandExecutionStatusResult.ERROR);
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#onSuccessOrError()
     */
    @Override
    public ISystemCommandExecuterTypeBuilder onSuccessOrError() {
        return addSystemCommand(SystemCommandExecutionStatusResult.SUCCESS_OR_ERROR);
    }

    
    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#pipe()
     */
    @Override
    public ISystemCommandExecuterTypeBuilder pipe() {
        return addSystemCommandGroup();
    }

    
    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#shell(java.lang.String[])
     */
    @Override
    public ISystemCommandExecuterBuilder shell(String... shell) {
        getSystemCommand().setShell(Arrays.asList(shell));
        return this;
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#user(java.lang.String)
     */
    @Override
    public ISystemCommandExecuterBuilder user(String user) {
        getProcessEnvironment().setUser(user);
        return this;
    }
    

    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#workingPath(java.lang.String)
     */
    @Override
    public ISystemCommandExecuterBuilder workingPath(String workingPath) {
        getProcessEnvironment().setWorkingPath(new File(workingPath).getAbsolutePath());
        return this;
    }

    
    /**
     * Set the os
     *
     * @param os the os
     * @return the system command executer builder
    @Override
    public AbstractCommandExecuterBuilder os(String os) {
        if (!getProcessEnvironment().getOS().equals(os)) {
        }
        return this;
    }
     */

    
    /**
     * Set the os version
     *
     * @param os the os version
     * @return the system command executer builder
    @Override
    public ISystemCommandExecuterBuilder osVersion(String osVersion) {
        getProcessEnvironment().setOSversion(osVersion);
        return this;
    }
    */

    
    /**
     * Set the architecture
     *
     * @param architecture the architecture
     * @return the system command executer builder
    @Override
    public ISystemCommandExecuterBuilder architecture(String architecture) {
        getProcessEnvironment().setArchitecture(architecture);
        return this;
    }
    */


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#environmentVariable(java.lang.String, java.lang.String)
     */
    @Override
    public ISystemCommandExecuterBuilder environmentVariable(String key, String value) {
        getProcessEnvironment().getEnvironmentVariables().put(key, value);
        return this;
    }

    
    /**
     * Add a new command 
     *
     * @param command the command to add
     * @return the system command executer builder
     */
    protected ISystemCommandExecuterBuilder command(String command) {
        return command(command, command);
    }


    /**
     * Add a new command 
     *
     * @param command the command to add
     * @param displayCommand the command to add as display
     * @return the system command executer builder
     */
    protected ISystemCommandExecuterBuilder command(String command, String displayCommand) {
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
    protected ISystemCommandExecuterBuilder command(final Map<String, String> keyValueSettings, final String keyPrefix, final boolean encapsulateValue, final boolean encapsulateExpression) {
        return command(keyValueSettings, keyPrefix, encapsulateValue, encapsulateExpression, null);
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
    protected ISystemCommandExecuterBuilder command(final Map<String, String> keyValueSettings, final String keyPrefix, final boolean encapsulateValue, final boolean encapsulateExpression, final Set<String> senstivieSettings) {
        SystemCommandFactory.getInstance().addSystemCommandParameters(getSystemCommand(), keyValueSettings, keyPrefix, encapsulateValue, encapsulateExpression, senstivieSettings);
        return this;
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#lock()
     */
    @Override
    public ISystemCommandExecuterBuilder lock() {
        systemCommandGroupList.lock();
        return this;
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#lock(java.lang.Integer)
     */
    @Override
    public ISystemCommandExecuterBuilder lock(Integer lockTimeoutInSeconds) {
        systemCommandGroupList.lock(lockTimeoutInSeconds);
        return this;
    }


    /**
     * @see com.github.toolarium.system.command.builder.ISystemCommandExecuterBuilder#build()
     */
    @Override
    public ISystemCommandExecuter build() {
        childBuild(systemCommandGroupList);
        return SystemCommandExecuterFactory.getInstance().createSystemCommandExecuter(systemCommandGroupList);
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(systemCommandGroupList);
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
        
        AbstractCommandExecuterBuilder other = (AbstractCommandExecuterBuilder) obj;
        return Objects.equals(systemCommandGroupList, other.systemCommandGroupList);
    }

    
    /**
     * Build the java command
     * @param systemCommandGroupList  the 
     * 
     * @throws IllegalArgumentException In case of invalid parameter combination
     */
    protected abstract void childBuild(ISystemCommandGroupList systemCommandGroupList) throws IllegalArgumentException;
    

    /**
     * Get the current system command
     * 
     * @return the current system command 
     */
    protected SystemCommand getSystemCommand() {
        if (currentSystemCommand == null) {
            if (parentProcessEnvironment != null) {
                currentSystemCommand = new SystemCommand(parentProcessEnvironment);
            } else {
                currentSystemCommand = new SystemCommand();
            }

            if (parentShell != null) {
                currentSystemCommand.setShell(parentShell);
            }

            systemCommandGroupList.add(currentSystemCommand);
        }
        return currentSystemCommand;
    }


    /**
     * Get the current system command
     * 
     * @return the system command 
     */
    protected ProcessEnvironment getProcessEnvironment() {
        return (ProcessEnvironment)getSystemCommand().getProcessEnvironment();
    }

    
    /**
     * Add a new system command
     * 
     * @param systemCommandExecutionStatusResult the system command execution status result
     * @return the system command executer builder
     */
    protected ISystemCommandExecuterTypeBuilder addSystemCommand(SystemCommandExecutionStatusResult systemCommandExecutionStatusResult) {
        getSystemCommand().setSystemCommandExecutionStatusResult(systemCommandExecutionStatusResult);
        return new SystemCommandExecuterTypeBuilder(systemCommandGroupList);
    }

    
    /**
     * Add a new system command
     * 
     * @return the system command executer builder
     */
    protected ISystemCommandExecuterTypeBuilder addSystemCommandGroup() {
        if (currentSystemCommand != null) {
            if (currentSystemCommand.getProcessEnvironment() != null) {
                parentProcessEnvironment = currentSystemCommand.getProcessEnvironment();
            }
            
            if (currentSystemCommand.getShell() != null) {
                parentShell = currentSystemCommand.getShell();
            }
        }
        
        systemCommandGroupList.add(new SystemCommandGroup());
        currentSystemCommand = null;
        return new SystemCommandExecuterTypeBuilder(systemCommandGroupList);
    }
}
