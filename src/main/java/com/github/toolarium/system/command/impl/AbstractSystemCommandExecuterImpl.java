/*
 * AbstractSystemCommandExecuterImpl.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.impl;

import com.github.toolarium.system.command.SystemCommandExecuter;
import com.github.toolarium.system.command.impl.util.StreamUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base class for system command execution
 *
 * @author patrick
 */
public abstract class AbstractSystemCommandExecuterImpl implements SystemCommandExecuter {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSystemCommandExecuterImpl.class);


    /**
     * @see com.github.toolarium.system.command.SystemCommandExecuter#executeCommand(java.lang.String, java.lang.String[])
     */
    @Override
    public Process executeCommand(String path, String... commandList) {
        if (commandList == null || commandList.length == 0) {
            throw new IllegalArgumentException("Invalid command!");
        }

        List<String> commandParameterList = Arrays.asList(commandList);

        // create process builder
        ProcessBuilder processBuilder = createProcessBuilder(commandParameterList);
        processBuilder.redirectErrorStream(true);

        Process process = null;
        try {
            String pathInfo = " in current path.";

            if (path != null) {
                // System.getProperty("user.home")
                processBuilder.directory(new File(path));
                pathInfo = " in path [" + path + "].";
            }

            LOG.debug("Execute command: " + commandParameterList + "" + pathInfo);
            process = processBuilder.start();
        } catch (Exception e) {
            LOG.warn("Error occured while executing command " + commandParameterList + ": " + e.getMessage(), e);
        }

        return process;
    }


    /**
     * @see com.github.toolarium.system.command.SystemCommandExecuter#executeCommand(java.lang.String, int, java.lang.String[])
     */
    @Override
    public Process executeCommand(String path, int numberOfSecondsToWait, String... commandList) {
        Process process = null;
        InputStream processOutput = null;
        InputStream processErrorOutput = null;
        if (commandList == null || commandList.length == 0) {
            throw new IllegalArgumentException("Invalid command!");
        }
        
        List<String> commandParameterList = Arrays.asList(commandList);

        try {
            process = executeCommand(path, commandList);

            // to capture output from the shell
            processOutput = process.getInputStream();
            processErrorOutput = process.getErrorStream();
            boolean hasEnded = false;
            boolean hasEndedInTime = true;

            // wait for the shell to finish and get the return code
            if (numberOfSecondsToWait <= 0) {
                process.waitFor();
                hasEnded = true;
            } else {
                hasEndedInTime = process.waitFor(numberOfSecondsToWait, TimeUnit.SECONDS);
                hasEnded = true;
            }

            if (hasEnded) {
                String message = "Command " + commandParameterList + "";
                if (hasEndedInTime) {
                    message += " -> returns " + process.exitValue();
                } else { 
                    message += " stopped execution (timeout " + numberOfSecondsToWait + ")!";
                }
                
                boolean messageLogged = false;
                String outputMessage = convertStreamToStr(processOutput);
                String errorMessage = convertStreamToStr(processErrorOutput);

                if (outputMessage != null && !outputMessage.isEmpty())                {
                    LOG.debug(message);
                    messageLogged = true;
                }

                if (errorMessage != null && !errorMessage.isEmpty()) {
                    if (!messageLogged) {
                        LOG.debug(message + ", error:\n" + errorMessage);
                    } else {
                        LOG.debug("\nError:\n" + errorMessage);
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn("Error occured while executing command " + commandParameterList + ": " + e.getMessage(), e);
        } finally {
            if (processOutput != null) {
                try {
                    processOutput.close();
                } catch (Exception e) {
                    // NOP
                }
            }
            if (processErrorOutput != null) {
                try {
                    processErrorOutput.close();
                } catch (Exception e) {
                    // NOP
                }
            }
        }

        return process;
    }


    /**
     * Prepare platform dependent command list.
     *
     * @param osName the os name
     * @param commandList the command list to execute
     * @return the platform dependent command list
     */
    protected abstract List<String> preparePlatformDependentCommandList(String osName, List<String> commandList);

    
    /**
     * Create a process builder
     *
     * @param commandParameterList the command parameter list to execute
     * @return the process builder
     */
    protected ProcessBuilder createProcessBuilder(List<String> commandParameterList) {
        String osName = System.getProperty("os.name").toLowerCase();
        List<String> commandList = preparePlatformDependentCommandList(osName, commandParameterList);
        LOG.debug("Execute command: " + commandList);
        return new ProcessBuilder(commandList);
    }

    
    /**
     * Convert the InputStream to String we use the Reader.read(char[] buffer) method. We iterate until the Reader return -1 which means
     * there's no more data to read. We use the StringWriter class to produce the string.
     *
     * @param is the input stream
     * @return the result
     * @throws IOException In case of an I/O error
     */
    protected String convertStreamToStr(InputStream is) throws IOException {
        if (is == null) {
            return "";
        }
        
        return StreamUtil.getInstance().convertStreamToStr(is);
    }
}
