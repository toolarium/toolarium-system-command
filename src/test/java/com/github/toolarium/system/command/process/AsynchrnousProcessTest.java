/*
 * AsynchrnousProcessTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.toolarium.system.command.AbstractProcessTest;
import com.github.toolarium.system.command.SystemCommandExecuterFactory;
import com.github.toolarium.system.command.dto.SystemCommand;
import com.github.toolarium.system.command.process.stream.IProcessInputStream;
import com.github.toolarium.system.command.process.stream.ProcessStreamFactory;
import com.github.toolarium.system.command.process.stream.output.ProcessBufferOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import com.github.toolarium.system.command.util.OSUtil;
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
    private static final String KEY2 = "key2";
    private static final String KEY1 = "key1";
    
    
    /**
     * Test echo output
     * 
     * @throws InterruptedException in case of thread interrupt
     */
    @Test
    public void echoAsynchrnousTest() throws InterruptedException {
        String command = "echo ok";
        ProcessBufferOutputStream outputStream = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        ProcessBufferOutputStream errorOutputStream = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        IProcessInputStream processInputStream = ProcessStreamFactory.getInstance().getStandardIn();
        SystemCommandExecuterFactory.getInstance().startFolderCleanupService();
        IAsynchronousProcess process = assertAsynchroneProcess(SystemCommandExecuterFactory.builder()
                .system().command(command)
                .build()
                .runAsynchronous(processInputStream, outputStream, errorOutputStream), 
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
        ProcessBufferOutputStream outputStream = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        ProcessBufferOutputStream errorOutputStream = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        IProcessInputStream processInputStream = ProcessStreamFactory.getInstance().getStandardIn();
        IAsynchronousProcess process = assertAsynchroneProcess(SystemCommandExecuterFactory.builder()
                .system().command(command1).onSuccessOrError()
                .system().command(command2)
                .build()
                .runAsynchronous(processInputStream, outputStream,  errorOutputStream), 
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
        SystemCommandExecuterFactory.getInstance().stopFolderCleanupService();
        SystemCommandExecuterFactory.getInstance().startFolderCleanupService(0, 500, TimeUnit.MILLISECONDS);
        
        Map<String, String> systemProperties = new LinkedHashMap<>();
        systemProperties.put("myapp", "the app");
        systemProperties.put("password", "abcd");
        
        Map<String, String> env = new HashMap<>();
        env.putAll(System.getenv());
        env.put(KEY1, "myValue1");
        env.put(KEY2, "myValue2");
        
        String qm = ""; // quotation marks
        String eps1 = ""; // empty parameter space
        String eps2 = ""; // empty parameter space
        if (OSUtil.getInstance().isWindows()) {
            qm = "\"";
            eps1 = SystemCommand.SPACE + SystemCommand.SPACE;
            eps2 = SystemCommand.SPACE;
        }
        String command1 = "echo " + prepareGetEnvValue(KEY1, false) + SystemCommand.SPACE + prepareGetEnvValue(KEY2, false) + SystemCommand.SPACE + prepareGetEnvValue("key3", false);
        String command2 = "echo " + prepareGetEnvValue(KEY1, true) + SystemCommand.SPACE + prepareGetEnvValue(KEY2, true) + SystemCommand.SPACE + prepareGetEnvValue("key3", true);
        
        ProcessBufferOutputStream outputStream = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        ProcessBufferOutputStream errorOutputStream = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        IProcessInputStream processInputStream = ProcessStreamFactory.getInstance().getStandardIn();
        IAsynchronousProcess process = assertAsynchroneProcess(SystemCommandExecuterFactory.builder()
                .system().command(command1).workingPath("build").environmentVariable(KEY1, "myValue1").environmentVariable(KEY2, "myValue2").onSuccess()
                .system().command(command2).workingPath("build").environmentVariable(KEY1, "myValuea").environmentVariable(KEY2, "myValueb").onError()
                .system().command(command2).workingPath("build/classes").environmentVariable(KEY1, "myErrorValue1").environmentVariable("key3", "myErrorValue2").onSuccessOrError()
                .system().command("echo").command(systemProperties, "-D", false, true, new LinkedHashSet<>(Arrays.asList("password")))
                .build()
                .runAsynchronous(processInputStream, outputStream, errorOutputStream), 
                                                               outputStream, errorOutputStream,
                                                               "myValue1 myValue2" + eps1 + "\r\nmyValuea myValueb" + eps2 + "\r\n" + qm + "-Dmyapp=the app" + qm + SystemCommand.SPACE + qm + "-Dpassword=abcd" + qm, // expected standard error!
                                                               "",       // no standard error!
                                                               new File(System.getProperty("user.dir") + "/build").getAbsoluteFile().toString(), // working path
                                                               env,      // no environment
                                                               0,        // return value
                                                               command1, command2, command2, "echo \"-Dmyapp=the app\" \"-Dpassword=...\"");
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
            
            if (!result.equals(expectedOutput)) {
                
                for (int i = 0; i < result.length(); i++) {
                    assertEquals(result.charAt(i), expectedOutput.charAt(i), "Pos:" + i);
                }
                assertEquals(result.length(), expectedOutput.length());
            }
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
