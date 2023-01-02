/*
 * SystemCommandFactoryTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.system.command.impl.util.StreamUtil;
import java.io.IOException;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the system command 
 *  
 * @author patrick
 */
public class SystemCommandFactoryTest {
    private static final Logger LOG = LoggerFactory.getLogger(SystemCommandFactoryTest.class);
    
    
    /**
     * Test list directory
     * 
     * @throws IOException in case of error
     */
    @Test
    public void listDirectoryTest() throws IOException {
        assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand("dir").build().runSynchronous(10), "dir", "", "");
    }


    /**
     * Test echo output
     *
     * @throws IOException in case of error
     */
    @Test
    public void echoSynchrnousTest() throws IOException {
        String command = "echo ok";
        IProcess process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runSynchronous(), command, "ok", "");
        assertNotNull(process);
    }

    
    /**
     * Test echo output
     *
     * @throws IOException in case of error
     */
    @Test
    public void echoSynchrnousErrorStreamTest() throws IOException {
        String command = "echo ok>&2";
        IProcess process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runSynchronous(), command, "", "ok");
        assertNotNull(process);
    }


    /**
     * Test echo output
     *
     * @throws IOException in case of error
     * @throws InterruptedException  In case of interruption
     */
    @Test
    public void echoAsynchrnousTest() throws IOException, InterruptedException {
        String command = "echo ok";
        IAsynchronousProcess process = assertAsynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runAsynchronous(), command, "ok", "");
        assertNotNull(process);
    }


    /**
     * Test sleep
     *
     * @throws IOException in case of error
     * @throws InterruptedException In case of interrupt
     */
    @Test
    public void sleepTest() throws IOException, InterruptedException {
        int seconds = 2;
        String command = SystemCommandFactory.getInstance().createSleepCommand(seconds);
        IProcess process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runSynchronous(), command, "", "");
        LOG.debug("Exit value: " + process.getExitValue());

        process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runSynchronous(), command, "", "");
        //process = SystemCommandExecuterFactory.getInstance().createSystemCommandExecuter().executeCommand(null, seconds + 1, SystemCommandFactory.getInstance().createSleepCommand(seconds));
        LOG.debug("Exit value: " + process.getExitValue());

        command = SystemCommandFactory.getInstance().createSleepCommand(2 * seconds);
        process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runSynchronous(), command, "", "");
        LOG.debug("Exit value: " + process.getExitValue());
    }

    
    /**
     * Assert asynchrone process
     * 
     * @param process the process
     * @param command the command
     * @param output the output
     * @param errorOutput the error output
     * @return the process
     * @throws IOException in case of an I/O exception
     * @throws InterruptedException In case of interuption 
     */
    private IAsynchronousProcess assertAsynchroneProcess(IAsynchronousProcess process, String command, String output, String errorOutput) throws IOException, InterruptedException {
        int result = process.waitFor();
        Thread.sleep(1000);
        assertEquals(result, 0);
        assertSynchroneProcess(process, command, output, errorOutput);
        return process;
    }

    
    /**
     * Assert synchrone process
     * 
     * @param process the process
     * @param command the command
     * @param output the output
     * @param errorOutput the error output
     * @return the process
     * @throws IOException in case of an I/O exception
     */
    private IProcess assertSynchroneProcess(IProcess process, String command, String output, String errorOutput) throws IOException {
        String expectedOutput = output;
        String osName = System.getProperty("os.name").toLowerCase();
        if (!output.isEmpty()) {
            expectedOutput = output + "\n";
            if (osName.startsWith("windows")) {
                expectedOutput = output + " \n";
            }
        }

        if (output != null && !output.isEmpty()) {
            assertEquals(StreamUtil.getInstance().convertStreamToStr(process.getOutputStream()).replace('\r', ' '), expectedOutput);
        }

        String expectedErrorOutput = errorOutput;
        if (!errorOutput.isEmpty()) {
            expectedErrorOutput = errorOutput + "\n";
            if (osName.startsWith("windows") && !errorOutput.isEmpty()) {
                expectedErrorOutput = errorOutput + " \n";
            }
        }

        if (errorOutput != null && !errorOutput.isEmpty()) {
            assertEquals(StreamUtil.getInstance().convertStreamToStr(process.getErrorStream()).replace('\r', ' '), expectedErrorOutput);
        }
        
        assertEquals(process.getProcessEnvironment().getUser(), System.getProperty("user.name").trim());
        assertEquals(process.getProcessEnvironment().getWorkingPath(), System.getProperty("user.dir"));
        assertEquals(process.getProcessEnvironment().getEnvironmentVariables(), System.getenv());
        assertEquals(process.getProcessEnvironment().getOS(), System.getProperty("os.name").trim().toLowerCase());
        assertEquals(process.getProcessEnvironment().getOSVersion(), System.getProperty("os.version").trim());
        assertEquals(process.getProcessEnvironment().getArchitecture(), System.getProperty("os.arch").trim());
        assertNull(process.getSystemCommand().getShell());
        assertEquals("" + process.getSystemCommand().getCommandList(), "[" + command + "]");
        assertEquals(process.getSystemCommand().toString(false), command);
        assertEquals(process.getSystemCommand().toString(true), command);
        assertTrue(process.getPid() > 0);
        
        assertTrue(Instant.now().isAfter(process.getStartTime()));
        // assertTrue(process.getTotalCpuDuration().getNano() > 0);
        assertEquals(process.getExitValue(), 0);
        return process;
    }
}
