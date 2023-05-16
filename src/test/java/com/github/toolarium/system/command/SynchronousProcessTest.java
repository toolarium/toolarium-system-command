/*
 * SynchronousProcessTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.system.command.process.ISynchronousProcess;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import com.github.toolarium.system.command.util.SystemCommandFactory;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Synchronous process tests
 * 
 * @author patrick
 */
public class SynchronousProcessTest extends AbstractProcessTest {
    private static final Logger LOG = LoggerFactory.getLogger(SynchronousProcessTest.class);

    
    /**
     * Test list directory
     */
    @Test
    public void listDirectoryTest() {
        ISynchronousProcess process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand("dir").build().runSynchronous(10), 
                                                              null,     // ignore standard out
                                                              "",       // no standard error! 
                                                              null,     // default working path  
                                                              null,     // no environment
                                                              0,        // return value
                                                              "dir");   // command
        LOG.debug("=>" + process.getOutput());
        assertTrue(process.getOutput().length() > 20);
        assertNotNull(process);
    }

    
    /**
     * Test echo output
     */
    @Test
    public void echoSynchrnousTest() {
        String command = "echo ok";
        ISynchronousProcess process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runSynchronous(), 
                                                             "ok",     // expected standard out
                                                             "",       // no standard error! 
                                                             null,     // default working path  
                                                             null,     // no environment 
                                                             0,        // return value
                                                             command); // command
        assertNotNull(process);
    }

    
    /**
     * Test echo output
     */
    @Test
    public void echoSynchrnousErrorStreamTest() {
        int returnValue = 127;
        if (isWindows()) {
            returnValue = 1;
        }
        
        String command = "ech ok";        
        ISynchronousProcess process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runSynchronous(), 
                                                             "",       // no standard out! 
                                                             null,     // ignore standard error!
                                                             null,     // default working path  
                                                             null,     // no environment 
                                                             returnValue,        // return value
                                                             command); // command
        assertNotNull(process);
        assertTrue(process.getErrorOutput().length() > 10);
    }

    
    /**
     * Test sleep
     */
    @Test
    public void sleepTest() {
        int seconds = 2;
        String command = SystemCommandFactory.getInstance().createSleepCommand(seconds);
        ISynchronousProcess process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runSynchronous(),
                                                             "",       // no standard out! 
                                                             "",       // no standard error!
                                                             null,     // default working path  
                                                             null,     // no environment 
                                                             0,        // return value
                                                             command); // command
        process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runSynchronous(),
                                         "",       // no standard out! 
                                         "",       // no standard error!
                                         null,     // default working path  
                                         null,     // no environment 
                                         0,        // return value
                                         command); // command
      
        //process = SystemCommandExecuterFactory.getInstance().createSystemCommandExecuter().executeCommand(null, seconds + 1, SystemCommandFactory.getInstance().createSleepCommand(seconds));
        LOG.debug("Exit value: " + process.getExitValue());

        command = SystemCommandFactory.getInstance().createSleepCommand(2 * seconds);
        process = assertSynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runSynchronous(),
                                         "",       // no standard out! 
                                         "",       // no standard error!
                                         null,     // default working path  
                                         null,     // no environment 
                                         0,        // return value
                                         command); // command
    }

    
    /**
     * Assert synchronous process
     * 
     * @param process the process
     * @param output the output
     * @param errorOutput the error output
     * @param workingPath the working path or null for current working directory
     * @param env the environment or current environment variables
     * @param returnValue the return value
     * @param commands the commands
     * @return the process
     */
    private ISynchronousProcess assertSynchroneProcess(ISynchronousProcess process, String output, String errorOutput, String workingPath, Map<String, String> env, int returnValue, String... commands) {
        String expectedOutput = output;
        if (output != null) {
            expectedOutput = ProcessStreamUtil.getInstance().removeCR(output);
            if (!expectedOutput.isEmpty()) {
                expectedOutput += "\n";
            }

            String result = ProcessStreamUtil.getInstance().removeCR(process.getOutput());
            LOG.debug("Compare result | [" + result + "] == [" + expectedOutput + "].");
            assertEquals(result, expectedOutput);
        }

        String expectedErrorOutput = errorOutput;
        if (errorOutput != null) {
            expectedErrorOutput = ProcessStreamUtil.getInstance().removeCR(errorOutput);
            
            if (!expectedErrorOutput.isEmpty()) {
                expectedErrorOutput += "\n";
            }
            String result = ProcessStreamUtil.getInstance().removeCR(process.getErrorOutput());
            LOG.debug("Compare error result | [" + result + "] == [" + expectedErrorOutput + "].");
            assertEquals(result, expectedErrorOutput);
        }

        assertProcess(process, workingPath, env, returnValue, commands);
        return process;
    }
}
