/*
 * AbstractSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.impl;

import com.github.toolarium.system.command.IAsynchronousProcess;
import com.github.toolarium.system.command.IProcess;
import com.github.toolarium.system.command.IProcessEnvironment;
import com.github.toolarium.system.command.ISystemCommand;
import com.github.toolarium.system.command.ISystemCommandExecuter;
import com.github.toolarium.system.command.dto.AsynchrounousProcess;
import com.github.toolarium.system.command.dto.Process;
import com.github.toolarium.system.command.dto.SystemCommand;
import com.github.toolarium.system.command.impl.util.StreamUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base class for system command execution
 *
 * @author patrick
 */
public abstract class AbstractSystemCommandExecuterImpl implements ISystemCommandExecuter {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSystemCommandExecuterImpl.class);
    private IProcessEnvironment processEnvironment;
    private ISystemCommand systemCommand;
    private String commandId;

    
    /**
     * Constructor for AbstractSystemCommandExecuterImpl
     *
     * @param processEnvironment the process environment
     * @param systemCommand the system command
     */
    protected AbstractSystemCommandExecuterImpl(IProcessEnvironment processEnvironment, ISystemCommand systemCommand) {
        this.processEnvironment = processEnvironment;
        this.systemCommand = systemCommand;
        this.commandId = null;
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuter#runSynchronous()
     */
    @Override
    public IProcess runSynchronous() {
        return runSynchronous(0);
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuter#runSynchronous(int)
     */
    @Override
    public IProcess runSynchronous(int numberOfSecondsToWait) {
        IAsynchronousProcess asynchronousProcess = runAsynchronous();
        String commandIdStr = "(id:" + commandId + ")";

        StringBuilder message = new StringBuilder("Command " + commandIdStr + SystemCommand.SPACE + systemCommand.getCommandList() + SystemCommand.SPACE);

        // to capture output from the shell
        InputStream outputMessage = null;
        InputStream errorMessage = null;

        int exitValue = -1;
        try {
            if (numberOfSecondsToWait <= 0) {
                exitValue = asynchronousProcess.waitFor();
                
                if (LOG.isDebugEnabled()) {
                    message.append("ended.");
                    LOG.debug(message.toString());
                }
            } else {
                if (asynchronousProcess.waitFor(numberOfSecondsToWait, TimeUnit.SECONDS)) {
                    exitValue = asynchronousProcess.getExitValue();
                    
                    if (LOG.isDebugEnabled()) {
                        message.append("ended in time.");
                        LOG.debug(message.toString());
                    }
                } else {
                    asynchronousProcess.tryDestroy();
                    if (asynchronousProcess.isAlive()) {
                        asynchronousProcess.destroy();
                    }
                    
                    exitValue = asynchronousProcess.getExitValue();
                    message.append("stopped execution (timeout " + numberOfSecondsToWait + ")!");
                    LOG.info(message.toString());
                }
            }
        } catch (InterruptedException ex) {
            message.append("ended with error: ");
            message.append(ex.getMessage());
            if (LOG.isDebugEnabled()) {
                LOG.debug(message.toString(), ex);
            }
            
            LOG.warn(message.toString());
        } finally {
            // to capture output from the shell
            try {
                outputMessage = StreamUtil.getInstance().convertStreamToNewInputStream(asynchronousProcess.getOutputStream());
            } catch (IOException e) {
                LOG.debug("Could not read standard out of process #" + asynchronousProcess.getPid() + ": " + e.getMessage());
                LOG.warn("Could not read standard out of process #" + asynchronousProcess.getPid() + ": " + e.getMessage());
            }
            try {
                errorMessage = StreamUtil.getInstance().convertStreamToNewInputStream(asynchronousProcess.getErrorStream());
            } catch (IOException e) {
                LOG.debug("Could not read standard error of process #" + asynchronousProcess.getPid() + ": " + e.getMessage());
                LOG.warn("Could not read standard error of process #" + asynchronousProcess.getPid() + ": " + e.getMessage());
            }

            StreamUtil.getInstance().close(asynchronousProcess.getInputStream());
            StreamUtil.getInstance().close(asynchronousProcess.getOutputStream());
            StreamUtil.getInstance().close(asynchronousProcess.getErrorStream());
        }

        return new Process(processEnvironment, asynchronousProcess.getSystemCommand(), asynchronousProcess.getPid(), asynchronousProcess.getStartTime(), asynchronousProcess.getTotalCpuDuration(),
                           exitValue, null, outputMessage, errorMessage);
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuter#runAsynchronous()
     */
    @Override
    public IAsynchronousProcess runAsynchronous() {
        validateProcessEnvironment(processEnvironment);
        validateSystemCommand(systemCommand);

        // create process builder
        PlatformDependentCommand platformDependentCommand = preparePlatformDependentCommandList(processEnvironment, systemCommand);
        commandId = platformDependentCommand.getCommandId();
        ProcessBuilder processBuilder = createProcessBuilder(platformDependentCommand);
        // TODO: processBuilder.redirectErrorStream(true);
        
        java.lang.Process process = null;
        try {
            String pathInfo = " in current path.";

            if (processEnvironment.getWorkingPath() != null) {
                // System.getProperty("user.home")
                processBuilder.directory(new File(processEnvironment.getWorkingPath()));
                pathInfo = " in path [" + processEnvironment.getWorkingPath() + "].";
            }

            String commandIdStr = "(id:" + commandId + ")";
            LOG.debug("Start command " + commandIdStr + ": [" + platformDependentCommand.toString() + "]" + pathInfo);
            process = processBuilder.start();
            LOG.debug("Process successful started " + commandIdStr + ", pid: " + process.pid());
        } catch (Exception e) {
            LOG.warn("Error occured while executing command " + platformDependentCommand.toString() + ": " + e.getMessage(), e);
        }

        return new AsynchrounousProcess(processEnvironment, systemCommand, process);
    }

        
    /**
     * Validate the command list
     * 
     * @param systemCommand the system command
     * @throws IllegalArgumentException In case of a invalid command list
     */
    protected void validateSystemCommand(final ISystemCommand systemCommand) {
        if (systemCommand == null || systemCommand.getCommandList() == null || systemCommand.getCommandList().isEmpty()) {
            throw new IllegalArgumentException("Invalid command!");
        }
    }


    /**
     * Validate the process environment
     *
     * @param processEnvironment the process environment
     */
    protected void validateProcessEnvironment(IProcessEnvironment processEnvironment) {
    }

    
    /**
     * Create a process builder
     * 
     * @param platformDependentCommand platform dependent command
     * @return the process builder
     */
    protected ProcessBuilder createProcessBuilder(PlatformDependentCommand platformDependentCommand) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(processEnvironment.getWorkingPath()));
        builder.command(platformDependentCommand.getCommandList());
        builder.environment().clear();
        builder.environment().putAll(processEnvironment.getEnvironmentVariables());
        return builder;
    }

    
    /**
     * Prepare platform dependent command list.
     *
     * @param processEnvironment the process environment
     * @param systemCommand the system command
     * @return the platform dependent command list
     */
    protected PlatformDependentCommand preparePlatformDependentCommandList(IProcessEnvironment processEnvironment, ISystemCommand systemCommand) {
        final List<String> shellCommandList = getShellCommand(processEnvironment, systemCommand);
        List<String> commandList = new ArrayList<String>();
        commandList.addAll(shellCommandList);
        commandList.addAll(systemCommand.getCommandList());
        
        StringBuilder shellCommand = new StringBuilder();
        for (int i = 0; i < shellCommandList.size(); i++) {
            if (i > 0) {
                shellCommand.append(SystemCommand.SPACE);
            }
            shellCommand.append(shellCommandList.get(i));
        }
        
        return new PlatformDependentCommand(commandList, shellCommand.toString() + SystemCommand.SPACE + systemCommand.toString(true));
    }
    
    
    /**
     * Get the shell command
     * 
     * @param processEnvironment the process environment
     * @param systemCommand the system command
     * @return the shell command
     */
    protected abstract List<String> getShellCommand(IProcessEnvironment processEnvironment, ISystemCommand systemCommand);
    
    
    
    /**
     * Platform dependent command
     * 
     * @author patrick
     */
    public class PlatformDependentCommand {
        private String commandId;
        private List<String> commandList;
        private String displayCommand;
        
        
        /**
         * Constructor for PlatformDependentCommand
         *
         * @param commandList the command list
         * @param displayCommand the display command
         */
        PlatformDependentCommand(List<String> commandList, String displayCommand) {
            this.commandId = Integer.toHexString(ThreadLocalRandom.current().nextInt()).toUpperCase();
            this.commandList = commandList;
            this.displayCommand = displayCommand;
        }
        
        
        /**
         * Get the command list
         *
         * @return the command list
         */
        public List<String> getCommandList() {
            return commandList;
        }
        
        
        /**
         * The command id
         * 
         * @return the command id
         */
        public String getCommandId() {
            return commandId;
        }
        
        
        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return displayCommand;
        }
    }
}
