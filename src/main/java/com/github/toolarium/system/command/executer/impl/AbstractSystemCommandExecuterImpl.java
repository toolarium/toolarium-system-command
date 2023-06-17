/*
 * AbstractSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.executer.impl;

import com.github.toolarium.system.command.SystemCommandExecuterFactory;
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
import com.github.toolarium.system.command.process.stream.output.ProcessBufferOutputStream;
import com.github.toolarium.system.command.process.stream.output.ProcessOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import com.github.toolarium.system.command.process.thread.NameableThreadFactory;
import com.github.toolarium.system.command.process.util.ProcessBuilderUtil;
import com.github.toolarium.system.command.process.util.ScriptUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;
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
        return runSynchronous(null, numberOfSecondsToWait);
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
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuter#runAsynchronous(com.github.toolarium.system.command.process.stream.IProcessOutputStream)
     */
    @Override
    public IAsynchronousProcess runAsynchronous(IProcessOutputStream processOutAndErr) {
        return runAsynchronous(null, processOutAndErr, processOutAndErr, DEFAULT_POLL_TIMEOUT);
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

        // create script path
        IProcessLiveness processLiveness = null;
        Path lockFile = null;
        
        try {
            Path scriptPath = null;
            if (systemCommandGroupList.runAsScript()
                || (processInputStream != null && (processInputStream.getProcessInputStreamSource().getFile() != null || processInputStream.getProcessInputStreamSource().getBuffer() != null))) {
                lockFile = ScriptUtil.getInstance().createLockFile(SystemCommandExecuterFactory.getInstance().getScriptFolderBasePath(), systemCommandGroupList.getId());
                scriptPath = Paths.get(SystemCommandExecuterFactory.getInstance().getScriptFolderBasePath() + "/" + systemCommandGroupList.getId());
            }

            // create process builder list
            List<ProcessBuilder> processBuilderList = ProcessBuilderUtil.getInstance().createProcessBuilders(systemCommandGroupList, processInputStream, processOut, processErr, this, scriptPath);
            if (processBuilderList.size() == 0) {
                throw new IllegalStateException("Invalid empty process builder list!");
            }
   
            // start process
            List<java.lang.Process> processList;
            if (processBuilderList.size() == 1) {
                processList = Arrays.asList(processBuilderList.get(0).start());
            } else {
                processList = ProcessBuilder.startPipeline(processBuilderList);
            }

            // restart lock from now
            systemCommandGroupList.lock(null);

            // start liveness thread
            processLiveness = new ProcessLiveness(systemCommandGroupList.getId(), processList, processOut, processErr, scriptPath, systemCommandGroupList.getLockTimeout(), pollTimeout);
            Executors.newSingleThreadExecutor(nameableThreadFactory).execute(processLiveness);

            int processCount = 0;
            Iterator<ISystemCommandGroup> it = systemCommandGroupList.iterator();
            while (it.hasNext()) {
                ISystemCommandGroup systemCommandGroup = it.next();
                // create pid file
                if (systemCommandGroup.runAsScript() && processList.size() > processCount) {
                    ScriptUtil.getInstance().createPidFile(scriptPath, systemCommandGroup.getId(), processList.get(processCount++).pid());
                }
            }

            if (scriptPath != null) {
                LOG.info("Process successful started (id:" + systemCommandGroupList.getId() + ", pid:" + processLiveness.getProcessId() + ", script:" + scriptPath + ")");
            } else {
                LOG.info("Process successful started (id:" + systemCommandGroupList.getId() + ", pid:" + processLiveness.getProcessId() + ")");
            }
        } catch (Exception e) {
            LOG.warn("Error occured while start executing command " + systemCommandGroupList.toString() + ": " + e.getMessage(), e);
            RuntimeException ex = new RuntimeException(e.getMessage());
            ex.setStackTrace(e.getStackTrace());
            throw ex;
        } finally {
            if (lockFile != null && systemCommandGroupList.isLocked()) {
                lockFile.toFile().delete();
            }
        }
        
        return new AsynchronousProcess(systemCommandGroupList, processLiveness);
    }

    
    /**
     * @see com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport#writeToFile(java.nio.file.Path, java.lang.String)
     */
    @Override
    public void writeToFile(Path file, String content) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Write to file [" + file + "]:\n" + ProcessStreamUtil.getInstance().removeCR(content));
        }

        Files.writeString(file, content, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
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

