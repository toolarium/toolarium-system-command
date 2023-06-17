/*
 * ProcessBuilderUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.util;

import com.github.toolarium.system.command.dto.ISystemCommand;
import com.github.toolarium.system.command.dto.group.ISystemCommandGroup;
import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport;
import com.github.toolarium.system.command.executer.impl.SystemCommandExecuterPlatformSupportWrapper;
import com.github.toolarium.system.command.process.IProcess;
import com.github.toolarium.system.command.process.stream.IProcessInputStream;
import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.ProcessStreamFactory;
import com.github.toolarium.system.command.process.stream.input.ProcessInputStreamSource;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
    
    private static final String DOT = ".";
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
     * Create a process builder list
     * 
     * @param systemCommandGroupList the system command group list
     * @param processInputStream the process input stream
     * @param processOut the process output stream
     * @param processErr the process error stream
     * @param inputSystemCommandExecuterPlatformSupport the system command executer platform support
     * @param scriptPath the script path
     * @return the process builder
     * @throws IllegalArgumentException In case of invalid parameters
     */
    public List<ProcessBuilder> createProcessBuilders(ISystemCommandGroupList systemCommandGroupList, 
                                                      IProcessInputStream processInputStream, 
                                                      IProcessOutputStream processOut, 
                                                      IProcessOutputStream processErr, 
                                                      ISystemCommandExecuterPlatformSupport inputSystemCommandExecuterPlatformSupport, 
                                                      Path scriptPath) {
        if (systemCommandGroupList == null || systemCommandGroupList.size() == 0) {
            return null;
        }
        
        List<ProcessBuilder> list = new ArrayList<>();
        Iterator<ISystemCommandGroup> it = systemCommandGroupList.iterator();
        while (it.hasNext()) {
            ISystemCommandGroup systemCommandGroup = it.next();
            ProcessBuilder processBuilder = createProcessBuilder(systemCommandGroup, inputSystemCommandExecuterPlatformSupport, scriptPath);

            // prepare streams
            if (systemCommandGroupList.size() == 1) {
                prepareProcessInputStream(systemCommandGroup, processBuilder, processInputStream, scriptPath, inputSystemCommandExecuterPlatformSupport);
                prepareProcessOutputStream(systemCommandGroup, processBuilder, processOut);
                prepareProcessErrorStream(systemCommandGroup, processBuilder, processErr);
            } else {
                if (list.size() == 0) {
                    prepareProcessInputStream(systemCommandGroup, processBuilder, processInputStream, scriptPath, inputSystemCommandExecuterPlatformSupport);
                    LOG.debug("Redirect standard output and error to pipe for process #" + systemCommandGroup.getId() + DOT);
                    processBuilder.redirectOutput(Redirect.PIPE);
                    processBuilder.redirectError(Redirect.PIPE);
                } else {
                    LOG.debug("Redirect input to pipe for process #" + systemCommandGroup.getId() + DOT);
                    processBuilder.redirectInput(Redirect.PIPE);
                    
                    if (list.size() < (systemCommandGroupList.size() - 1)) {
                        LOG.debug("Redirect standard output and error to pipe for process #" + systemCommandGroup.getId() + DOT);
                        processBuilder.redirectOutput(Redirect.PIPE);
                        processBuilder.redirectError(Redirect.PIPE);
                    } else {
                        prepareProcessOutputStream(systemCommandGroup, processBuilder, processOut);
                        prepareProcessErrorStream(systemCommandGroup, processBuilder, processErr);
                    }
                }
            }

            LOG.debug("Prepared command (id:" + systemCommandGroup.getId() + ") to run in path [" + processBuilder.directory().getAbsolutePath() + "]: \n" + systemCommandGroup.toString());
            list.add(processBuilder);
        }
        
        return list;
    }

    
    /**
     * Create command line
     * 
     * @param systemCommand the system command
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @param commandList the command list
     * @return the process builder
     * @throws IllegalArgumentException In case of invalid parameters
     */
    List<String> createCommandLine(ISystemCommand systemCommand, ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport, List<String> commandList) {
        List<String> cmdList = new ArrayList<>();
        List<String> shellStart = systemCommandExecuterPlatformSupport.getShellStartCommand(systemCommand);
        if (shellStart != null && !shellStart.isEmpty()) {
            cmdList.addAll(shellStart);
        }
        
        cmdList.addAll(commandList);
        
        List<String> shellEnd = systemCommandExecuterPlatformSupport.getShellEndCommand(systemCommand);
        if (shellEnd != null && !shellEnd.isEmpty()) {
            cmdList.addAll(shellEnd);
        }
        
        //LOG.debug("Shell: " + cmdList);
        return cmdList;
    }
    
    
    /**
     * Create a process builder
     * 
     * @param systemCommandGroup the system command group
     * @param inputSystemCommandExecuterPlatformSupport the system command executer platform support
     * @param scriptPath the script path
     * @return the process builder
     * @throws IllegalArgumentException In case of invalid parameters
     */
    public ProcessBuilder createProcessBuilder(ISystemCommandGroup systemCommandGroup, ISystemCommandExecuterPlatformSupport inputSystemCommandExecuterPlatformSupport, Path scriptPath) {
        validateParameters(systemCommandGroup, inputSystemCommandExecuterPlatformSupport);

        if (systemCommandGroup.runAsScript()) {
            return createScriptProcessBuilder(systemCommandGroup, inputSystemCommandExecuterPlatformSupport, scriptPath);
        } else {
            return createProcessBuilder(systemCommandGroup.iterator().next(), inputSystemCommandExecuterPlatformSupport);
        }
    }
    

    /**
     * Create a process builder
     * 
     * @param systemCommand the system command
     * @param inputSystemCommandExecuterPlatformSupport the system command executer platform support
     * @return the process builder
     * @throws IllegalArgumentException In case of invalid parameters
     */
    public ProcessBuilder createProcessBuilder(ISystemCommand systemCommand, ISystemCommandExecuterPlatformSupport inputSystemCommandExecuterPlatformSupport) {
        validateParameters(systemCommand, inputSystemCommandExecuterPlatformSupport);
        List<String> cmdList = createCommandLine(systemCommand, new SystemCommandExecuterPlatformSupportWrapper(inputSystemCommandExecuterPlatformSupport), systemCommand.getCommandList());
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(cmdList);
        setWorkingPath(systemCommand, builder);
        setEnvironmentVariables(systemCommand, builder);
        return builder;
    }

    
    /**
     * Create a process builder
     * 
     * @param systemCommandGroup the system command group
     * @param inputSystemCommandExecuterPlatformSupport the system command executer platform support
     * @param scriptPath the script path
     * @return the process builder
     * @throws IllegalArgumentException In case of invalid parameters
     */
    public ProcessBuilder createScriptProcessBuilder(ISystemCommandGroup systemCommandGroup, ISystemCommandExecuterPlatformSupport inputSystemCommandExecuterPlatformSupport, Path scriptPath) {
        validateParameters(systemCommandGroup, inputSystemCommandExecuterPlatformSupport);
        ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport = new SystemCommandExecuterPlatformSupportWrapper(inputSystemCommandExecuterPlatformSupport); 
        ISystemCommand primarySystemCommand = null;

        List<String> cmdList = new ArrayList<>();

        try {
            Path file = ScriptUtil.getInstance().prepareTempPathAndScript(scriptPath, systemCommandGroup.getId(), systemCommandExecuterPlatformSupport);
            LOG.debug("Set script [" + file.toString() + "]");
            
            // create a temp script to run multiple commands
            Map<String, String> currentEnvironmentMap = null;
            String currentWorkingPath = null;

            String onSuccessOrErrorEnd = null;
            Iterator<ISystemCommand> it = systemCommandGroup.iterator();
            while (it.hasNext()) {
                ISystemCommand systemCommand = it.next();
                
                if (primarySystemCommand == null) {
                    primarySystemCommand = systemCommand;
                    cmdList.addAll(createCommandLine(primarySystemCommand, systemCommandExecuterPlatformSupport, Arrays.asList(file.toString())));
                    currentEnvironmentMap = primarySystemCommand.getProcessEnvironment().getEnvironmentVariables();
                    currentEnvironmentMap.put(ProcessBuilderUtil.TEMP, file.getParent().toString());
                    currentWorkingPath = primarySystemCommand.getProcessEnvironment().getWorkingPath();
                }

                LOG.debug("Prepare command [" + systemCommand.toString(true) + "]");
                systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getEndOfLine());
                
                // set environment variables
                currentEnvironmentMap = prepareEnvironmentVariables(systemCommandExecuterPlatformSupport, file, currentEnvironmentMap, systemCommand.getProcessEnvironment().getEnvironmentVariables());
                LOG.debug("Set " + ProcessBuilderUtil.TEMP + " [" + file.getParent().toString() + "]");
                currentEnvironmentMap.put(ProcessBuilderUtil.TEMP, file.getParent().toString());
                
                // the working directory
                String newWorkingPath = systemCommand.getProcessEnvironment().getWorkingPath();
                // TODO: relative path new File(base).toURI().relativize(new File(path).toURI()).getPath();
                
                if (newWorkingPath != null && !currentWorkingPath.equals(newWorkingPath)) {
                    LOG.debug("Set in script working path to [" + newWorkingPath + "]. ");
                    systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getEnvironmentChangeDirectoryCommand() + newWorkingPath + systemCommandExecuterPlatformSupport.getEndOfLine());
                    currentWorkingPath = newWorkingPath;
                } else {
                    LOG.debug("Keep script working path to [" + newWorkingPath + "].");
                }

                // the command
                systemCommandExecuterPlatformSupport.writeToFile(file, 
                                                                 /*ScriptUtil.getInstance().prepareCommandList(systemCommandExecuterPlatformSupport.getShellCommand(systemCommand))
                                                                 + SystemCommand.SPACE*/ ""                            
                                                                 + ScriptUtil.getInstance().prepareCommandList(systemCommand.getCommandList()));
                // handle on success or error
                if (systemCommand.getSystemCommandExecutionStatusResult() != null) {
                    if (onSuccessOrErrorEnd != null) {
                        systemCommandExecuterPlatformSupport.writeToFile(file, onSuccessOrErrorEnd);
                        onSuccessOrErrorEnd = null;
                    }
                    
                    switch (systemCommand.getSystemCommandExecutionStatusResult()) {
                        case SUCCESS:
                            systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getCommandOnSuccessStart());
                            onSuccessOrErrorEnd = systemCommandExecuterPlatformSupport.getCommandOnSuccessEnd();
                            break;
                        case ERROR: 
                            systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getCommandOnErrorStart());
                            onSuccessOrErrorEnd = systemCommandExecuterPlatformSupport.getCommandOnErrorEnd();
                            break;
                        case SUCCESS_OR_ERROR: 
                        default:
                            onSuccessOrErrorEnd = null;
                            systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getEndOfLine());
                    }
                }
            }
            
            // close script file
            ScriptUtil.getInstance().closeScriptFile(systemCommandExecuterPlatformSupport, file);
        } catch (IOException e) {
            LOG.warn("Error occured: " + e.getMessage(), e);
        }

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(cmdList);
        setWorkingPath(primarySystemCommand, builder);
        setEnvironmentVariables(primarySystemCommand, builder);
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
            
            if (!key.equals(ProcessBuilderUtil.TEMP)) {
                systemCommandExecuterPlatformSupport.writeToFile(file, envSetting + systemCommandExecuterPlatformSupport.getEndOfLine());
            }
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
     * @param systemCommand the psystem command
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @throws IllegalArgumentException In case of invalid parameters
     */
    private void validateParameters(ISystemCommand systemCommand, ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport) {
        if (systemCommand == null | systemCommand.getProcessEnvironment() == null || systemCommand.getCommandList() == null) {
            throw new IllegalArgumentException("Invalid system command parameters.");
        }
    }


    /**
     * Validate input parameters
     * 
     * @param systemCommandGroup the system command group
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @throws IllegalArgumentException In case of invalid parameters
     */
    private void validateParameters(ISystemCommandGroup systemCommandGroup, ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport) {
        if (systemCommandGroup == null || systemCommandGroup.size() == 0) {
            throw new IllegalArgumentException("Invalid system command group parameters.");
        }
    }


    /**
     * set the working path on the builder
     * 
     * @param systemCommand the system command
     * @param builder the builder
     */
    private void setWorkingPath(ISystemCommand systemCommand, ProcessBuilder builder) {
        final String workingPath = systemCommand.getProcessEnvironment().getWorkingPath();
        LOG.debug("Use working path [" + workingPath + "].");
        builder.directory(new File(workingPath));
    }


    /**
     * set the environment variables
     * 
     * @param systemCommand the system command
     * @param builder the builder
     */
    private void setEnvironmentVariables(ISystemCommand systemCommand, ProcessBuilder builder) {
        //builder.environment().clear();

        // set environment variables
        final Map<String, String> environmentVariables = systemCommand.getProcessEnvironment().getEnvironmentVariables();
        for (Map.Entry<String, String> e : environmentVariables.entrySet()) {
            if (e.getKey() != null && !e.getKey().isBlank()) {
                builder.environment().put(e.getKey(), e.getValue());
            }
        }
    }


    /**
     * Prepare the process streams
     * 
     * @param basePath the base path
     * @param systemCommandGroup the system command group
     * @param processBuilder the process builder
     * @param processInputStream the process input stream
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     */
    private void prepareProcessInputStream(ISystemCommandGroup systemCommandGroup, 
                                           ProcessBuilder processBuilder, 
                                           IProcessInputStream processInputStream, 
                                           Path basePath, 
                                           ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport) {
        ProcessInputStreamSource inputStreamSource;
        if (processInputStream == null) {
            inputStreamSource = ProcessStreamFactory.getInstance().getStandardIn().getProcessInputStreamSource();
        } else {
            inputStreamSource = processInputStream.getProcessInputStreamSource();
        }

        if (inputStreamSource != null) {
            switch (inputStreamSource) {
                case DISCARD:
                    LOG.debug("Discard input stream for process #" + systemCommandGroup.getId() + DOT);
                    processBuilder.redirectInput(Redirect.from(writeInputFile(basePath, systemCommandGroup, inputStreamSource, systemCommandExecuterPlatformSupport)));
                    break;
                case PIPE:
                    LOG.debug("Pipe input stream for process #" + systemCommandGroup.getId() + DOT);
                    processBuilder.redirectInput(Redirect.PIPE);
                    break;
                case FILE:
                    File file = writeInputFile(basePath, systemCommandGroup, inputStreamSource, systemCommandExecuterPlatformSupport);
                    LOG.debug("Read input stream from file [" + file + "] for process #" + systemCommandGroup.getId() + DOT);
                    processBuilder.redirectInput(Redirect.from(file));
                    break;
                case BUFFER:
                    LOG.debug("Read input stream from buffer for process #" + systemCommandGroup.getId() + DOT);
                    processBuilder.redirectInput(Redirect.from(writeInputFile(basePath, systemCommandGroup, inputStreamSource, systemCommandExecuterPlatformSupport)));
                    break;
                case INHERIT:
                default:
                    LOG.debug("Inherit input stream for process #" + systemCommandGroup.getId() + DOT);
                    processBuilder.redirectInput(Redirect.INHERIT);
                    break;
            }
        }
    }

    
    /**
     * Prepare the process output stream
     * 
     * @param systemCommandGroup the system command group
     * @param processBuilder the process builder
     * @param processOut the process output stream
     */
    private void prepareProcessOutputStream(ISystemCommandGroup systemCommandGroup, ProcessBuilder processBuilder, IProcessOutputStream processOut) {
        if (processOut == null) {
            LOG.debug("Discard output stream.");
            processBuilder.redirectOutput(Redirect.DISCARD);
        } else {
            processOut.start(systemCommandGroup);
        }
    }

    
    /**
     * Prepare the process error stream
     * 
     * @param systemCommandGroup the system command group
     * @param processBuilder the process builder
     * @param processErr the process error stream
     */
    private void prepareProcessErrorStream(ISystemCommandGroup systemCommandGroup, ProcessBuilder processBuilder, IProcessOutputStream processErr) {
        if (processErr == null) {
            LOG.debug("Discard error output stream.");
            processBuilder.redirectError(Redirect.DISCARD);
        } else {
            processErr.start(systemCommandGroup);
        }
    }


    /**
     * Write the input file
     * 
     * @param systemCommandGroup the system command group
     * @param basePath the base path
     * @param processInputStreamSource the process input stream source
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @return the file
     */
    private File writeInputFile(Path basePath, ISystemCommandGroup systemCommandGroup, ProcessInputStreamSource processInputStreamSource, ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport) {
        final String buffer = processInputStreamSource.getBuffer();
        File file = processInputStreamSource.getFile();
        if (file == null && buffer == null) {
            return null;
        }

        if (file == null) {
            try {
                file = ScriptUtil.getInstance().createTempFile(basePath, systemCommandGroup.getId() + ".input");
            } catch (IOException e) {
                LOG.warn("Could not create temp file: " + e.getMessage(), e);
                return null;
            }
        }

        LOG.debug("Create input stream from " + file + "].");
        if (buffer != null && !buffer.isEmpty()) {
            try {
                systemCommandExecuterPlatformSupport.writeToFile(file.toPath(), buffer);
            } catch (IOException e) {
                LOG.warn("Could not write temp file content: " + e.getMessage(), e);
            }
        }

        return file;
    }
}
