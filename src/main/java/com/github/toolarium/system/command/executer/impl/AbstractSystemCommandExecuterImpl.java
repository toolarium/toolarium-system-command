/*
 * AbstractSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.executer.impl;

import com.github.toolarium.system.command.dto.group.ISystemCommandGroup;
import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import com.github.toolarium.system.command.executer.ISystemCommandExecuter;
import com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport;
import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.ISynchronousProcess;
import com.github.toolarium.system.command.process.impl.AsynchronousProcess;
import com.github.toolarium.system.command.process.impl.SynchronousProcess;
import com.github.toolarium.system.command.process.liveness.IProcessLiveness;
import com.github.toolarium.system.command.process.liveness.impl.ProcessLiveness;
import com.github.toolarium.system.command.process.stream.IProcessInputStream;
import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.ProcessStreamFactory;
import com.github.toolarium.system.command.process.stream.input.ProcessInputStreamSource;
import com.github.toolarium.system.command.process.stream.output.ProcessBufferOutputStream;
import com.github.toolarium.system.command.process.stream.output.ProcessOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import com.github.toolarium.system.command.process.thread.NameableThreadFactory;
import com.github.toolarium.system.command.process.util.ProcessBuilderUtil;
import com.github.toolarium.system.command.process.util.ScriptUtil;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
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
    
    private ISystemCommandGroupList systemCommandGroupList;

    
    /**
     * Constructor for AbstractSystemCommandExecuterImpl
     *
     * @param systemCommandGroupList the system command group list
     * @throws IllegalArgumentException In case of an invalid system command list
     */
    protected AbstractSystemCommandExecuterImpl(ISystemCommandGroupList systemCommandGroupList) {
        if (systemCommandGroupList == null || systemCommandGroupList.size() == 0) {
            throw new IllegalArgumentException("Invalid system command list!");
        }

        this.systemCommandGroupList = systemCommandGroupList;
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuter#runSynchronous()
     */
    @Override
    public ISynchronousProcess runSynchronous() {
        return runSynchronous(0);
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuter#runSynchronous(int)
     */
    @Override
    public ISynchronousProcess runSynchronous(int numberOfSecondsToWait) {
        return runSynchronous(ProcessStreamFactory.getInstance().getStandardIn(), numberOfSecondsToWait);
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuter#runSynchronous(com.github.toolarium.system.command.process.stream.IProcessInputStream, int)
     */
    @Override
    public ISynchronousProcess runSynchronous(IProcessInputStream processInputStream, int numberOfSecondsToWait) {
        // to capture output from the shell
        ProcessBufferOutputStream outputstream = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        ProcessBufferOutputStream errorOutputstream = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();        
        IAsynchronousProcess asynchronousProcess = runAsynchronous(processInputStream, outputstream, errorOutputstream, DEFAULT_POLL_TIMEOUT);
        StringBuilder processInfo = new StringBuilder(" (id:" + systemCommandGroupList.getId() + ", pid:" + asynchronousProcess.getPid());
        
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

        return new SynchronousProcess(systemCommandGroupList, 
                                      asynchronousProcess.getPid(), 
                                      asynchronousProcess.getStartTime(), asynchronousProcess.getTotalCpuDuration(),
                                      exitValue, 
                                      outputstream.toString(), errorOutputstream.toString());
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuter#runAsynchronous()
     */
    @Override
    public IAsynchronousProcess runAsynchronous() {
        return runAsynchronous(null, new ProcessOutputStream(System.out), new ProcessOutputStream(System.err));
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuter#runAsynchronous(com.github.toolarium.system.command.process.stream.IProcessOutputStream, com.github.toolarium.system.command.process.stream.IProcessOutputStream)
     */
    @Override
    public IAsynchronousProcess runAsynchronous(IProcessOutputStream processOut, IProcessOutputStream processErr) {
        return runAsynchronous(null, processOut, processErr, DEFAULT_POLL_TIMEOUT);
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuter#runAsynchronous(com.github.toolarium.system.command.process.stream.IProcessInputStream, 
     *      com.github.toolarium.system.command.process.stream.IProcessOutputStream, com.github.toolarium.system.command.process.stream.IProcessOutputStream)
     */
    @Override
    public IAsynchronousProcess runAsynchronous(IProcessInputStream processInputStream, IProcessOutputStream processOut, IProcessOutputStream processErr) {
        return runAsynchronous(processInputStream, processOut, processErr, DEFAULT_POLL_TIMEOUT);
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuter#runAsynchronous(com.github.toolarium.system.command.process.stream.IProcessInputStream, 
     *      com.github.toolarium.system.command.process.stream.IProcessOutputStream, com.github.toolarium.system.command.process.stream.IProcessOutputStream, long)
     */
    @Override
    public IAsynchronousProcess runAsynchronous(IProcessInputStream processInputStream, IProcessOutputStream processOut, IProcessOutputStream processErr, long pollTimeout) {
        
        // create process builder list
        IProcessLiveness processLiveness = null;
        List<ProcessBuilder> processBuilderList = ProcessBuilderUtil.getInstance().createProcessBuilders(systemCommandGroupList, this);
        if (processBuilderList.size() == 0) {
            throw new IllegalStateException("");
        }
        
        try {
            ISystemCommandGroup systemCommandGroup = systemCommandGroupList.iterator().next();
            LOG.debug("Start command (id:" + systemCommandGroupList.getId() + ") in path [" + processBuilderList.get(0).directory().getAbsolutePath() + "]: \n" + systemCommandGroup.toString());
            
            // prepare streams
            setProcessInputStreamSource(systemCommandGroup, processBuilderList.get(0), processInputStream, processOut, processErr);
            
            // start process
            List<java.lang.Process> processList;
            if (processBuilderList.size() == 1) {
                processList = Arrays.asList(processBuilderList.get(0).start());
            } else {
                processList = ProcessBuilder.startPipeline(processBuilderList); 
            }
   
            // start liveness thread
            processLiveness = new ProcessLiveness(processList, processOut, processErr, pollTimeout);
            Executors.newSingleThreadExecutor(nameableThreadFactory).execute(processLiveness);
            
            // TODO:
            /*
            boolean scriptExecution = systemCommandGroup.size() > 1; 
            if (scriptExecution) {
                // create pid file
                File pidFile = new File(platformDependentSystemCommand.getTempPath() + "/" + systemCommandGroup.getCommandId() + ".pid"); // systemCommandGroup.getId()
                pidFile.createNewFile();
                writeToFile(pidFile.toPath(), "" + process.pid());
            }
            */
            
            LOG.info("Process successful started (id:" + systemCommandGroupList.getId() + ", pid:" + processLiveness.getProcessId() /* TODO: + ", script:" + scriptExecution*/ + ")");
        } catch (Exception e) {
            LOG.warn("Error occured while executing command " + systemCommandGroupList.toString() + ": " + e.getMessage(), e);
        }
        
        return new AsynchronousProcess(systemCommandGroupList, processLiveness);
    }


    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#writeToFile(java.nio.file.Path, java.lang.String)
     */
    @Override
    public void writeToFile(Path file, String content) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Write to script file [" + file + "]:\n" + ProcessStreamUtil.getInstance().removeCR(content));
        }
        
        Files.writeString(file, content, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    
    /**
     * Write the input file
     *
     * @param systemCommandGroup the system command group
     * @param processInputStreamSource the process input stream source
     * @return the file
     */
    protected File writeInputFile(ISystemCommandGroup systemCommandGroup, ProcessInputStreamSource processInputStreamSource) {
        final String buffer = processInputStreamSource.getBuffer();
        File file = processInputStreamSource.getFile();
        if (file == null && buffer == null) {
            return null;
        }

        if (file == null) {
            try {
                file = ScriptUtil.getInstance().createTempFile(systemCommandGroup, "processinput.dat");
            } catch (IOException e) {
                LOG.warn("Could not create temp file: " + e.getMessage(), e);
                return null;
            } 
        }
        
        LOG.debug("Create input stream from " + file + "].");
        if (buffer != null && !buffer.isEmpty()) {
            try {
                writeToFile(file.toPath(), buffer);
            } catch (IOException e) {
                LOG.warn("Could not write temp file content: " + e.getMessage(), e);
            }
        }
        
        return file;
    }

    
    /**
     * Set the process input stream source
     * 
     * @param systemCommandGroup the system command group
     * @param processBuilder the process builder
     * @param processInputStream the process input stream
     * @param processOut the process output stream
     * @param processErr the process error stream
     */
    protected void setProcessInputStreamSource(ISystemCommandGroup systemCommandGroup, ProcessBuilder processBuilder, IProcessInputStream processInputStream, IProcessOutputStream processOut, IProcessOutputStream processErr) {
        ProcessInputStreamSource inputStreamSource = processInputStream.getProcessInputStreamSource();
        if (inputStreamSource != null) {
            switch (inputStreamSource) {
                case DISCARD:
                    LOG.debug("Discard input stream.");
                    processBuilder.redirectInput(Redirect.from(writeInputFile(systemCommandGroup, inputStreamSource)));
                    break;
                case PIPE:
                    LOG.debug("Pipe input stream.");
                    processBuilder.redirectInput(Redirect.PIPE);
                    break;
                case FILE:
                    LOG.debug("File input stream.");
                    processBuilder.redirectInput(Redirect.from(writeInputFile(systemCommandGroup, inputStreamSource)));
                    break;
                case BUFFER:
                    LOG.debug("Read input stream from buffer.");
                    processBuilder.redirectInput(Redirect.from(writeInputFile(systemCommandGroup, inputStreamSource)));
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
     * Prepare duration
     * 
     * @param process the process 
     * @return the duration
     */
    protected String prepareDuration(IAsynchronousProcess process) {
        return ProcessBuilderUtil.getInstance().prepareDuration(process);
    }
}

