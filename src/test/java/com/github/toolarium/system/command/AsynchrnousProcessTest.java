/*
 * AsynchrnousProcessTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.dto.ProcessInputStreamSource;
import com.github.toolarium.system.command.process.stream.impl.ProcessBufferOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Asynchronous process tests
 * 
 * @author patrick
 */
public class AsynchrnousProcessTest extends AbstractProcessTest {
    private static final Logger LOG = LoggerFactory.getLogger(AsynchrnousProcessTest.class);
    
    
    /**
     * Test echo output
     * 
     * @throws InterruptedException in case of thread interrupt
     */
    @Test
    public void echoAsynchrnousTest() throws InterruptedException {
        String command = "echo ok";
        ProcessBufferOutputStream outputStream = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errorOutputStream = new ProcessBufferOutputStream();
        SystemCommandExecuterFactory.getInstance().startTempFolderCleanupService();
        IAsynchronousProcess process = assertAsynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command).build().runAsynchronous(ProcessInputStreamSource.INHERIT, 
                                                                                                                                                   outputStream, 
                                                                                                                                                   errorOutputStream), 
                                                               outputStream, errorOutputStream,
                                                               "ok",     // expected standard out! 
                                                               "",       // no standard error!
                                                               null,     // default working path  
                                                               null,     // no environment 
                                                               0,        // return value
                                                               command); // command
        assertNotNull(process);
    }

    
    /**
     * Test echo output
     * 
     * @throws InterruptedException in case of thread interrupt
     */
    @Test
    public void echoMultipleCommandInSameProcessTest() throws InterruptedException {  
        String command1 = "echo ok1";
        String command2 = "echo ok2";
        ProcessBufferOutputStream outputStream = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errorOutputStream = new ProcessBufferOutputStream();
        IAsynchronousProcess process = assertAsynchroneProcess(SystemCommandExecuterBuilder.create().addToCommand(command1).addSystemCommand().addToCommand(command2).build().runAsynchronous(ProcessInputStreamSource.INHERIT, 
                                                                                                                                                                                              outputStream, 
                                                                                                                                                                                              errorOutputStream), 
                                                               outputStream, errorOutputStream,
                                                               "ok1\r\nok2",  // expected standard out! 
                                                               "",       // no standard error!
                                                               null,     // default working path  
                                                               null,     // no environment 
                                                               0,        // return value
                                                               command1, command2); // command
        assertNotNull(process);
        
        // close resource
        process.close();
    }

    
    /**
     * Test echo output
     * 
     * @throws InterruptedException in case of thread interrupt
     */
    @Test
    public void echoMultipleCommandInSameProcessWithEnvironmenVariablesTest() throws InterruptedException {
        SystemCommandExecuterFactory.getInstance().stopTempFolderCleanupService();
        SystemCommandExecuterFactory.getInstance().startTempFolderCleanupService(0, 500, TimeUnit.MILLISECONDS);
        
        Map<String, String> systemProperties = new LinkedHashMap<>();
        systemProperties.put("myapp", "the app");
        systemProperties.put("password", "abcd");
        
        Map<String, String> env = new HashMap<>();
        env.putAll(System.getenv());
        env.put("key1", "myValue1");
        env.put("key2", "myValue2");
        
        String qm = ""; // quotation marks
        String eps = ""; // empty parameter space
        if (isWindows()) {
            qm = "\"";
            eps = " ";            
        }
        String command1 = "echo " + prepareGetEnvValue("key1") + " " + prepareGetEnvValue("key2") + " " + prepareGetEnvValue("key3");
        String command2 = "echo " + prepareGetEnvValue("key1") + " " + prepareGetEnvValue("key2") + " " + prepareGetEnvValue("key3");
        
        ProcessBufferOutputStream outputStream = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errorOutputStream = new ProcessBufferOutputStream();
        IAsynchronousProcess process = assertAsynchroneProcess(SystemCommandExecuterBuilder.create()
                                                                    .workingPath("build")
                                                                    .environmentVariable("key1", "myValue1").environmentVariable("key2", "myValue2").addToCommand(command1)
                                                                    .addSystemCommand().workingPath("build/classes")
                                                                    .environmentVariable("key1", "myValuea").environmentVariable("key3", "myValueb").addToCommand(command2)
                                                                    .addSystemCommand()
                                                                    .addToCommand("echo")
                                                                    .addToCommand(systemProperties, "-D", true, new LinkedHashSet<>(Arrays.asList("password")))
                                                                    .build().runAsynchronous(ProcessInputStreamSource.INHERIT, outputStream, errorOutputStream), 
                                                               outputStream, errorOutputStream,
                                                               "myValue1 myValue2" + eps + "\r\nmyValuea " + eps + "myValueb\r\n-Dmyapp=" + qm + "the app" + qm + " -Dpassword=" + qm + "abcd" + qm, // expected standard error!
                                                               "",       // no standard error!
                                                               new File(System.getProperty("user.dir") + "/build").getAbsoluteFile().toString(), // working path
                                                               env,      // no environment
                                                               0,        // return value
                                                               command1, command2, "echo -Dmyapp=\"the app\" -Dpassword=...");
        assertNotNull(process);
    }

    
    /**
     * Assert asynchrone process
     * 
     * @param process the process
     * @param outputStream the output stream
     * @param errorOutputStream the error output stream
     * @param output the output
     * @param errorOutput the error output
     * @param workingPath the working path or null for current working directory
     * @param env the environment or current environment variables
     * @param returnValue the return value
     * @param commands the commands
     * @return the process
     * @throws InterruptedException in case of thread interrupt
     */
    private IAsynchronousProcess assertAsynchroneProcess(IAsynchronousProcess process, 
                                                         ProcessBufferOutputStream outputStream, ProcessBufferOutputStream errorOutputStream, 
                                                         String output, String errorOutput, 
                                                         String workingPath, Map<String, String> env, int returnValue, String... commands) 
            throws InterruptedException {
        
        int processEndValue = process.waitFor();

        String expectedOutput = output;
        if (output != null) {
            expectedOutput = ProcessStreamUtil.getInstance().removeCR(output);
            if (!expectedOutput.isEmpty()) {
                expectedOutput += "\n";
            }

            String result = ProcessStreamUtil.getInstance().removeCR(outputStream.toString());
            LOG.debug("Compare result | [" + result + "] == [" + expectedOutput + "].");
            /*
            for (int i = 0; i < result.length(); i++) {
                assertEquals(result.charAt(i), expectedOutput.charAt(i), "Pos:" + i);
            }
            assertEquals(result.length(), expectedOutput.length());
            */
            assertEquals(result, expectedOutput);
        }

        String expectedErrorOutput = errorOutput;
        if (errorOutput != null) {
            expectedErrorOutput = ProcessStreamUtil.getInstance().removeCR(errorOutput);
            
            if (!expectedErrorOutput.isEmpty()) {
                expectedErrorOutput += "\n";
            }
            
            String result = ProcessStreamUtil.getInstance().removeCR(errorOutputStream.toString());
            LOG.debug("Compare error result | [" + result + "] == [" + expectedErrorOutput + "].");
            assertEquals(result, expectedErrorOutput);
        }
        
        assertEquals(processEndValue, returnValue);
        assertProcess(process, workingPath, env, returnValue, commands);
        return process;
    }
}
