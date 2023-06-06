/*
 * JarSystemCommandTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.toolarium.system.command.AbstractProcessTest;
import com.github.toolarium.system.command.SystemCommandExecuterFactory;
import com.github.toolarium.system.command.TestMain;
import com.github.toolarium.system.command.Version;
import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.stream.IProcessInputStream;
import com.github.toolarium.system.command.process.stream.ProcessStreamFactory;
import com.github.toolarium.system.command.process.stream.output.ProcessBufferOutputStream;
import org.junit.jupiter.api.Test;


/**
 * Test the jar usage
 *  
 * @author patrick
 */
public class JarSystemCommandTest extends AbstractProcessTest {
    private static final String TEST_JARFILE = "build/libs/toolarium-system-command-" + Version.VERSION + "-test.jar";

    
    /**
     * Shows the usage of inherit java environment
     * 
     * @throws InterruptedException in case of a thread interruption
     */
    @Test
    public void usageJarInhertitEnvironment() throws InterruptedException {
        final String param1 = "-param1";
        final String param2 = "-newValue=this is a parameter with spaces";
        final String envValue = "1";
        final String sysValue = "new value";
        final int exitValue = 1;
        
        IProcessInputStream processInputStream = ProcessStreamFactory.getInstance().getStandardIn();
        ProcessBufferOutputStream output = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .jar(TEST_JARFILE)                                                     // test jar file
                .inheritJre()                                                      // inherit jre
                .environmentVariable(TestMain.ENV_KEY, envValue)                   // set an additional environment variable
                .javaMemory("256M", "1024M")                                       // set memory
                .systemProperty(TestMain.SYSTEM_PROPERTY_KEY, sysValue)            // set system properties
                .systemProperty(TestMain.SYSTEM_PROPERTY_EXIT_VALUE, "" + exitValue)
                .systemProperty(TestMain.SYSTEM_PROPERTY_PRINT_VERBOSE, TRUE)
                .parameter(param1).parameter(param2)                               // set program parameter
            .build()
            .runAsynchronous(processInputStream, output, errOutput); // write output and error to defined buffer
        myAsyncProcess.waitFor();                                                  // wait until process ends
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        assertEquals(exitValue, myAsyncProcess.getExitValue());
        //assertNotNull(myAsyncProcess.getTotalCpuDuration());
        assertTestMainOut(output, null, null, envValue, sysValue, param1, param2);
        assertTestMainErr(errOutput);
    }

    
    /**
     * Shows the usage
     * 
     * @throws InterruptedException in case of a thread interruption
     */
    @Test
    public void usageJarDefaultJavaAndSetClassPath() throws InterruptedException {
        final String param1 = "-param1";
        final String param2 = "-newValue=this is a parameter with spaces";
        final String envValue = "1";
        final String sysValue = "new value";
        final int exitValue = 1;
        
        IProcessInputStream processInputStream = ProcessStreamFactory.getInstance().getStandardIn();
        ProcessBufferOutputStream output = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
             .jar(TEST_JARFILE)                                                   // test jar file
                .environmentVariable(TestMain.ENV_KEY, envValue)                  // set an additional environment variable
                .systemProperty(TestMain.SYSTEM_PROPERTY_KEY, sysValue)           // set system properties
                .systemProperty(TestMain.SYSTEM_PROPERTY_EXIT_VALUE, "" + exitValue)
                .systemProperty(TestMain.SYSTEM_PROPERTY_PRINT_VERBOSE, TRUE)
                .parameter(param1).parameter(param2)
            .build()
            .runAsynchronous(processInputStream, output, errOutput);
        myAsyncProcess.waitFor();                                                  // wait until process ends
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        assertEquals(exitValue, myAsyncProcess.getExitValue());
        //assertNotNull(myAsyncProcess.getTotalCpuDuration());
        assertTestMainOut(output, null, null, envValue, sysValue, param1, param2);
        assertTestMainErr(errOutput);
    }
}
