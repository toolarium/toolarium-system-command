/*
 * AbstractSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.impl;

import com.github.toolarium.system.command.ISystemCommand;
import com.github.toolarium.system.command.ISystemCommandExecuter;
import com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport;
import com.github.toolarium.system.command.dto.PlatformDependentSystemCommand;
import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.ISynchronousProcess;
import com.github.toolarium.system.command.process.dto.AsynchronousProcess;
import com.github.toolarium.system.command.process.dto.ProcessInputStreamSource;
import com.github.toolarium.system.command.process.dto.SynchronousProcess;
import com.github.toolarium.system.command.process.liveness.IProcessLiveness;
import com.github.toolarium.system.command.process.liveness.impl.ProcessLiveness;
import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.impl.ProcessBufferOutputStream;
import com.github.toolarium.system.command.process.stream.impl.ProcessOutputStream;
import com.github.toolarium.system.command.process.thread.NameableThreadFactory;
import com.github.toolarium.system.command.process.util.ProcessBuilderUtil;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base class for system command execution
 *
 * @author patrick
 */
public abstract class AbstractSystemCommandExecuterImpl implements ISystemCommandExecuter, ISystemCommandExecuterPlatformSupport {
    /** The default poll timeout */    
    public static final int DEFAULT_POLL_TIMEOUT = 5;
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSystemCommandExecuterImpl.class);
    private static NameableThreadFactory nameableThreadFactory = new NameableThreadFactory("liveness");
    
    private PlatformDependentSystemCommand platformDependentSystemCommand;

    
    /**
     * Constructor for AbstractSystemCommandExecuterImpl
     *
     * @param systemCommandList the system command list
     */
    protected AbstractSystemCommandExecuterImpl(List<? extends ISystemCommand> systemCommandList) {
        this.platformDependentSystemCommand = preparePlatformDependentCommandList(systemCommandList); 
    }

    
    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuter#runSynchronous()
     */
    @Override
    public ISynchronousProcess runSynchronous() {
        return runSynchronous(0);
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuter#runSynchronous(int)
     */
    @Override
    public ISynchronousProcess runSynchronous(int numberOfSecondsToWait) {
        return runSynchronous(null, numberOfSecondsToWait);
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuter#runSynchronous(com.github.toolarium.system.command.process.dto.ProcessInputStreamSource, int)
     */
    @Override
    public ISynchronousProcess runSynchronous(ProcessInputStreamSource processInputStreamSource, int numberOfSecondsToWait) {
        return runSynchronous(processInputStreamSource, numberOfSecondsToWait, DEFAULT_POLL_TIMEOUT);
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuter#runSynchronous(com.github.toolarium.system.command.process.dto.ProcessInputStreamSource, int, long)
     */
    @Override
    public ISynchronousProcess runSynchronous(ProcessInputStreamSource processInputStreamSource, int numberOfSecondsToWait, long pollTimeout) {
        
        // to capture output from the shell
        ProcessBufferOutputStream outputstream = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errorOutputstream = new ProcessBufferOutputStream();        
        IAsynchronousProcess asynchronousProcess = runAsynchronous(processInputStreamSource, outputstream, errorOutputstream, pollTimeout);
        StringBuilder processInfo = new StringBuilder(" (id:" + prepareCommandId() + ", pid:" + asynchronousProcess.getPid());
        
        int exitValue = -1;
        try {
            if (numberOfSecondsToWait <= 0) {
                exitValue = asynchronousProcess.waitFor();
                LOG.info(new StringBuilder("Process ").append("ended").append(processInfo).append(", exit:").append(exitValue).append(", duration:").append(prepareDuration(asynchronousProcess)).append(")").toString());
            } else {
                if (asynchronousProcess.waitFor(numberOfSecondsToWait, TimeUnit.SECONDS)) {
                    exitValue = asynchronousProcess.getExitValue();
                    LOG.info(new StringBuilder("Process ").append("ended in time").append(processInfo).append(", exit:").append(exitValue).append(", duration:").append(prepareDuration(asynchronousProcess)).append(")").toString());
                } else {
                    StringBuilder message = new StringBuilder("Process ");
                    asynchronousProcess.tryDestroy();
                    if (asynchronousProcess.isAlive()) {
                        asynchronousProcess.destroy();
                        message.append("forced aborted");
                    } else {
                        message.append("aborted");
                    }

                    exitValue = asynchronousProcess.getExitValue();
                    LOG.info(message.append(", timeout:" + numberOfSecondsToWait).append(processInfo).append(", exit:").append(exitValue).append(", duration:").append(prepareDuration(asynchronousProcess)).append(")!").toString());
                }
            }
        } catch (InterruptedException ex) {
            StringBuilder message = new StringBuilder("Process ").append("ended with error").append(processInfo).append(", duration:").append(prepareDuration(asynchronousProcess)).append("): ").append(ex.getMessage());
            if (LOG.isDebugEnabled()) {
                LOG.debug(message.toString(), ex);
            }
            
            LOG.warn(message.toString());
        }

        return new SynchronousProcess(platformDependentSystemCommand, 
                                      asynchronousProcess.getPid(), 
                                      asynchronousProcess.getStartTime(), asynchronousProcess.getTotalCpuDuration(),
                                      exitValue, 
                                      outputstream.toString(), errorOutputstream.toString());
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuter#runAsynchronous()
     */
    @Override
    public IAsynchronousProcess runAsynchronous() {
        return runAsynchronous(null, new ProcessOutputStream(System.out), new ProcessOutputStream(System.err));
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuter#runAsynchronous(com.github.toolarium.system.command.process.dto.ProcessInputStreamSource, 
     *      com.github.toolarium.system.command.process.stream.IProcessOutputStream, com.github.toolarium.system.command.process.stream.IProcessOutputStream)
     */
    @Override
    public IAsynchronousProcess runAsynchronous(ProcessInputStreamSource processInputStreamSource, IProcessOutputStream processOut, IProcessOutputStream processErr) {
        return runAsynchronous(processInputStreamSource, processOut, processErr, DEFAULT_POLL_TIMEOUT);
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuter#runAsynchronous(com.github.toolarium.system.command.process.dto.ProcessInputStreamSource, 
     *      com.github.toolarium.system.command.process.stream.IProcessOutputStream, com.github.toolarium.system.command.process.stream.IProcessOutputStream, long)
     */
    @Override
    public IAsynchronousProcess runAsynchronous(ProcessInputStreamSource processInputStreamSource, IProcessOutputStream processOut, IProcessOutputStream processErr, long pollTimeout) {
        platformDependentSystemCommand.validate();

        // create process builder
        ProcessBuilder processBuilder = ProcessBuilderUtil.getInstance().createProcessBuilder(platformDependentSystemCommand, this);
        
        // prepare streams
        setPrcoessInputStreamSource(processBuilder, processInputStreamSource, processOut, processErr);
        
        IProcessLiveness processLiveness = null;
        try {
            LOG.debug("Start command (id:" + prepareCommandId() + ") in path [" + processBuilder.directory().getAbsolutePath() + "]: \n" + platformDependentSystemCommand.toString());
            
            // start process
            java.lang.Process process = processBuilder.start();

            // start liveness thread
            processLiveness = new ProcessLiveness(process, processOut, processErr, pollTimeout);
            Executors.newSingleThreadExecutor(nameableThreadFactory).execute(processLiveness);
            
            boolean scriptExecution = platformDependentSystemCommand.getSystemCommandList().size() > 1; 
            if (scriptExecution) {
                // create pid file
                File pidFile = new File(platformDependentSystemCommand.getTempPath() + "/" + platformDependentSystemCommand.getCommandId() + ".pid");
                pidFile.createNewFile();
                writeToFile(pidFile.toPath(), "" + process.pid());
            }
            
            LOG.info("Process successful started (id:" + prepareCommandId() + ", pid:" + process.pid() + ", script:" + scriptExecution + ")");
        } catch (Exception e) {
            LOG.warn("Error occured while executing command " + platformDependentSystemCommand.toString() + ": " + e.getMessage(), e);
        }
        
        return new AsynchronousProcess(platformDependentSystemCommand, processLiveness);
    }


    /**
     * @see com.github.toolarium.system.command.ISystemCommandExecuterPlatformSupport#writeToFile(java.nio.file.Path, java.lang.String)
     */
    @Override
    public void writeToFile(Path file, String content) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Write to script file [" + file + "]:\n" + content.replace("\r", ""));
        }
        
        Files.writeString(file, content, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    
    /**
     * Prepare platform dependent command list.
     *
     * @param systemCommandList the system command list
     * @return the platform dependent command list
     * @throws IllegalArgumentException In case of an invalid system command list
     */
    protected PlatformDependentSystemCommand preparePlatformDependentCommandList(final List<? extends ISystemCommand> systemCommandList) {
        if (systemCommandList == null || systemCommandList.isEmpty()) {
            throw new IllegalArgumentException("Invalid system command list!");
        }

        StringBuilder displayCommand = new StringBuilder();
        for (ISystemCommand systemCommand : systemCommandList) {
            if (displayCommand.length() > 0) {
                displayCommand.append(getEndOfLine());
            }
            
            //displayCommand.append(ScriptUtil.getInstance().prepareCommandList(getShellCommand(systemCommand)));
            //displayCommand.append(SystemCommand.SPACE);
            displayCommand.append(systemCommand.toString(true));
        }

        return new PlatformDependentSystemCommand(systemCommandList, displayCommand.toString());
    }

    
    /**
     * Set the process input stream source
     * 
     * @param processInputStreamSource the process input stream source
     * @param processOut the process output stream
     * @param processErr the process error stream
     * @param processBuilder the process builder
     */
    protected void setPrcoessInputStreamSource(ProcessBuilder processBuilder, ProcessInputStreamSource processInputStreamSource, IProcessOutputStream processOut, IProcessOutputStream processErr) {
        ProcessInputStreamSource inputStreamSource = processInputStreamSource;
        if (inputStreamSource != null) {
            switch (inputStreamSource) {
                case DISCARD:
                    LOG.debug("Discard input stream.");
                    processBuilder.redirectInput(Redirect.DISCARD);
                    break;
                case PIPE:
                    LOG.debug("Pipe input stream.");
                    processBuilder.redirectInput(Redirect.PIPE);
                    break;
                case INHERIT:
                default:
                    LOG.debug("Inherit input stream.");
                    processBuilder.redirectInput(Redirect.INHERIT);
                    break;
            }
        }
        
        if (processOut == null) {
            LOG.debug("Discard output stream.");
            processBuilder.redirectOutput(Redirect.DISCARD);
        }
        
        if (processErr == null) {
            LOG.debug("Discard error output stream.");
            processBuilder.redirectError(Redirect.DISCARD);
        }
    }


    /**
     * Prepare the command id
     * 
     * @return the command id
     */
    protected String prepareCommandId() {
        final String id = platformDependentSystemCommand.getCommandId();
        if (id != null && !id.isBlank()) {
            return id;
        }
        
        return "n/a";
    }


    /**
     * Prepare duration
     * 
     * @param process the process 
     * @return the duration
     */
    protected String prepareDuration(IAsynchronousProcess process) {
        return ProcessBuilderUtil.getInstance().prepareDuration(process);
    }
}

