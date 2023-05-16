/*
 * ProcessBuilderUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.util;

import com.github.toolarium.system.command.ISystemCommand;
import com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport;
import com.github.toolarium.system.command.dto.PlatformDependentSystemCommand;
import com.github.toolarium.system.command.process.IProcess;
import com.github.toolarium.system.command.process.util.dto.SystemCommandExecuterPlatformSupportWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Process builder util.
 *  
 * @author patrick
 */
public final class ProcessBuilderUtil {
    
    /** TEMP Environment variable */
    public static final String TEMP = "TOOLARIUM_TEMP";

    private static final Logger LOG = LoggerFactory.getLogger(ProcessBuilderUtil.class);
    
    
    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final ProcessBuilderUtil INSTANCE = new ProcessBuilderUtil();
    }

    
    /**
     * Constructor
     */
    private ProcessBuilderUtil() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static ProcessBuilderUtil getInstance() {
        return HOLDER.INSTANCE;
    }
    
    
    /**
     * Check if a process is running
     * 
     * @param pid the pid
     * @return true if the process is still running
     */
    public boolean isProcessRunning(Long pid) {
        if (pid == null) {
            return false;
        }
        
        Optional<ProcessHandle> processHandle = ProcessHandle.of(pid);
        boolean isrunning = processHandle.isPresent() && processHandle.get().isAlive();
        return isrunning;
    }

    
    /**
     * Create a process builder
     * 
     * @param platformDependentSystemCommand the platform dependent system command
     * @param inputSystemCommandExecuterPlatformSupport the system command executer platform support
     * @return the process builder
     * @throws IllegalArgumentException In case of invalid parameters
     */
    public ProcessBuilder createProcessBuilder(PlatformDependentSystemCommand platformDependentSystemCommand, ISystemCommandExecuterPlatformSupport inputSystemCommandExecuterPlatformSupport) {
        validateParameters(platformDependentSystemCommand, inputSystemCommandExecuterPlatformSupport);
        ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport = new SystemCommandExecuterPlatformSupportWrapper(inputSystemCommandExecuterPlatformSupport); 

        List<String> cmdList = new ArrayList<>();
        cmdList.addAll(systemCommandExecuterPlatformSupport.getShellCommand(platformDependentSystemCommand.getSystemCommandList().get(0))); // in all cases we use the first command

        final Map<String, String> environmentVariables = platformDependentSystemCommand.getSystemCommandList().get(0).getProcessEnvironment().getEnvironmentVariables();
        String workingPath = platformDependentSystemCommand.getSystemCommandList().get(0).getProcessEnvironment().getWorkingPath();
        LOG.debug("Use script working path to [" + workingPath + "].");
        
        if (platformDependentSystemCommand.getSystemCommandList().size() <= 1) {
            cmdList.addAll(platformDependentSystemCommand.getSystemCommandList().get(0).getCommandList());
            //cmdList.addAll(systemCommandExecuterPlatformSupport.getEndShellCommand(platformDependentSystemCommand.getSystemCommandList().get(0))); // in all cases we use the first command
        } else {
            try {
                Path file = ScriptUtil.getInstance().prepareTempPathAndScript(platformDependentSystemCommand, systemCommandExecuterPlatformSupport);
                LOG.debug("Set script [" + file.toString() + "]");
                cmdList.add(file.toString());
                
                // create a temp script to run multiple commands
                Map<String, String> currentEnvironmentMap = environmentVariables;
                currentEnvironmentMap.put("TOOLARIUM_TEMP", file.getParent().toString());
                String currentWorkingPath = workingPath;
                for (ISystemCommand systemCommand : platformDependentSystemCommand.getSystemCommandList()) {
                    LOG.debug("Prepare command [" + systemCommand.toString(true) + "]");
                    systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getEndOfLine());
                    
                    // set environment variables
                    currentEnvironmentMap = prepareEnvironmentVariables(systemCommandExecuterPlatformSupport, file, currentEnvironmentMap, systemCommand.getProcessEnvironment().getEnvironmentVariables());
                    currentEnvironmentMap.put(ProcessBuilderUtil.TEMP, file.getParent().toString());
                    
                    // the working directory
                    String newWorkingPath = systemCommand.getProcessEnvironment().getWorkingPath();
                    // TODO: relative path new File(base).toURI().relativize(new File(path).toURI()).getPath();
                    
                    if (newWorkingPath != null && !currentWorkingPath.equals(newWorkingPath)) {
                        LOG.debug("Set in script working path to [" + newWorkingPath + "].");
                        systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getEnvironmentChangeDirectoryCommand() + newWorkingPath + systemCommandExecuterPlatformSupport.getEndOfLine());
                        currentWorkingPath = newWorkingPath;
                    } else {
                        LOG.debug("Keep script working path to [" + newWorkingPath + "].");
                    }

                    // the command
                    systemCommandExecuterPlatformSupport.writeToFile(file, 
                                                                     /*ScriptUtil.getInstance().prepareCommandList(systemCommandExecuterPlatformSupport.getShellCommand(systemCommand))
                                                                     + SystemCommand.SPACE*/ ""                            
                                                                     + ScriptUtil.getInstance().prepareCommandList(systemCommand.getCommandList())
                                                                     + systemCommandExecuterPlatformSupport.getEndOfLine());
                }
                
                // close script file
                ScriptUtil.getInstance().closeScriptFile(systemCommandExecuterPlatformSupport, file);
            } catch (IOException e1) {
                LOG.warn("Error occured: " + e1.getMessage(), e1);
            }
        }

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(cmdList);
        builder.directory(new File(workingPath));
        //builder.environment().clear();

        // set environment variables
        for (Map.Entry<String, String> e : environmentVariables.entrySet()) {
            if (e.getKey() != null && !e.getKey().isBlank()) {
                builder.environment().put(e.getKey(), e.getValue());
            }
        }
        return builder;
    }

    
    /**
     * Prepare the environment variable 
     *
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @param file the current temporary file
     * @param currentEnv the current environment
     * @param newEnv the new environment
     * @return the new environment variables
     * @throws IOException In case of write issues
     */
    public Map<String, String> prepareEnvironmentVariables(ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport, Path file, Map<String, String> currentEnv, Map<String, String> newEnv) throws IOException {
        
        // unset environment variables
        for (String key : ProcessBuilderUtil.getInstance().unsetEnvironment(currentEnv, newEnv)) {
            String envSetting = systemCommandExecuterPlatformSupport.getEnvironmentUnsetCommand() 
                                + key 
                                + systemCommandExecuterPlatformSupport.getEnvironmentAssignCommand() 
                                + systemCommandExecuterPlatformSupport.getEnvironmentAssignCommandEnd();
            LOG.debug("Unset environment variable [" + key + "].");
            systemCommandExecuterPlatformSupport.writeToFile(file, envSetting + systemCommandExecuterPlatformSupport.getEndOfLine());
        }

        // set new or changed environment variables
        Set<String> newKeys = ProcessBuilderUtil.getInstance().unsetEnvironment(newEnv, currentEnv);
        for (Map.Entry<String, String> e : newEnv.entrySet()) {
            if (newKeys.equals(e.getKey()) || !e.getValue().equals(currentEnv.get(e.getKey()))) {
                final String envSetting = systemCommandExecuterPlatformSupport.getEnvironmentSetCommand() 
                                          + e.getKey() 
                                          +  systemCommandExecuterPlatformSupport.getEnvironmentAssignCommand() 
                                          + e.getValue() 
                                          + systemCommandExecuterPlatformSupport.getEnvironmentAssignCommandEnd();
                LOG.debug("Set environment variable [" + e.getKey() + "].");
                systemCommandExecuterPlatformSupport.writeToFile(file, envSetting + systemCommandExecuterPlatformSupport.getEndOfLine());
            }
        }
        
        return newEnv;
    }

    
    /**
     * Write the environment variable 
     *
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @param file the current temporary file
     * @param env the environment variable
     * @return the new environment
     * @throws IOException In case of write issues
     */
    public Map<String, String> prepareEnvironmentVariables(ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport, Path file, Map<String, String> env) throws IOException {
        if (env != null && !env.isEmpty()) {
            for (Map.Entry<String, String> e : env.entrySet()) {
                if (e.getKey() != null && !e.getKey().isBlank()) {
                    final String envSetting = systemCommandExecuterPlatformSupport.getEnvironmentSetCommand() 
                                              + e.getKey() 
                                              +  systemCommandExecuterPlatformSupport.getEnvironmentAssignCommand() 
                                              + e.getValue() 
                                              + systemCommandExecuterPlatformSupport.getEnvironmentAssignCommandEnd();
                    systemCommandExecuterPlatformSupport.writeToFile(file, envSetting + systemCommandExecuterPlatformSupport.getEndOfLine());
                }
            }
        }
        
        return env;
    }

    
    /**
     * Compare two maps by key
     *
     * @param currentEnv the current environment
     * @param newEnv the new environment
     * @return the keys to unset
     */
    public Set<String> unsetEnvironment(Map<String, String> currentEnv, Map<String, String> newEnv) {
        Set<String> keysToUnset = new LinkedHashSet<>();
        if (currentEnv != null && !currentEnv.isEmpty()) {
            for (String key : currentEnv.keySet()) {
                if (!newEnv.containsKey(key)) {
                    keysToUnset.add(key);
                }
            }
        }
            
        return keysToUnset;
    }

    
    /**
     * Prepare duration
     * 
     * @param process the process 
     * @return the duration
     */
    public String prepareDuration(IProcess process) {
        if (process.getTotalCpuDuration() == null) {
            return "n/a";
        }
        
        return process.getTotalCpuDuration().toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }
    
    
    /**
     * Validate input parameters
     * 
     * @param platformDependentSystemCommand the platform dependent system command
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @throws IllegalArgumentException In case of invalid parameters
     */
    private void validateParameters(PlatformDependentSystemCommand platformDependentSystemCommand, ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport) {
        if (platformDependentSystemCommand == null || systemCommandExecuterPlatformSupport == null) {
            throw new IllegalArgumentException("Invalid parameters.");
        }

        platformDependentSystemCommand.validate();
    }
}
