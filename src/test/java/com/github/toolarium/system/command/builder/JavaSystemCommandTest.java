/*
 * JavaSystemCommandTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.toolarium.system.command.AbstractProcessTest;
import com.github.toolarium.system.command.SystemCommandExecuterFactory;
import com.github.toolarium.system.command.TestMain;
import com.github.toolarium.system.command.dto.group.SystemCommandGroup;
import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.stream.ProcessInputStreamSource;
import com.github.toolarium.system.command.process.stream.output.ProcessBufferOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import com.github.toolarium.system.command.process.util.ScriptUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.Test;


/**
 * Java system command test
 *  
 * @author patrick
 */
public class JavaSystemCommandTest extends AbstractProcessTest {
    private static final String TEST_BUFFER = "test buffer";

    
    /**
     * Shows the usage of inherit java environment
     * 
     * @throws InterruptedException in case of a thread interruption
     */
    @Test
    public void usageJavaInhertitEnvironment() throws InterruptedException {
        final String param1 = "-param1";
        final String param2 = "-newValue=this is a parameter with spaces";
        final String envValue = "1";
        final String sysValue = "new value";
        final int exitValue = 1;
        
        ProcessBufferOutputStream output = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .java("com.github.toolarium.system.command.TestMain")
                .inheritJre()                                                      // inherit jre
                .inheritClassPath()                                                // inherit classpath
                .environmentVariable(TestMain.ENV_KEY, envValue)                   // set an additional environment variable
                .javaMemory("256M", "1024M")                                       // set memory
                .systemProperty(TestMain.SYSTEM_PROPERTY_KEY, sysValue)            // set system properties
                .systemProperty(TestMain.SYSTEM_PROPERTY_EXIT_VALUE, "" + exitValue)
                .systemProperty(TestMain.SYSTEM_PROPERTY_PRINT_VERBOSE, TRUE)
                .parameter(param1).parameter(param2)                               // set program parameter
            .build()
            .runAsynchronous(output, errOutput);                                   // write output and error to defined buffer
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
    public void usageJavaDefaultJavaAndSetClassPath() throws InterruptedException {
        final String param1 = "-param1";
        final String param2 = "-newValue=this is a parameter with spaces";
        final String envValue = "1";
        final String sysValue = "new value";
        final int exitValue = 1;
        
        ProcessBufferOutputStream output = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .java("com.github.toolarium.system.command.TestMain")                 // use default java
                .environmentVariable("CLASSPATH", "build/classes/java/test")      // set classpath by environment variable
                .environmentVariable(TestMain.ENV_KEY, envValue)                  // set an additional environment variable
                .systemProperty(TestMain.SYSTEM_PROPERTY_KEY, sysValue)           // set system properties
                .systemProperty(TestMain.SYSTEM_PROPERTY_EXIT_VALUE, "" + exitValue)
                .systemProperty(TestMain.SYSTEM_PROPERTY_PRINT_VERBOSE, TRUE)
                .parameter(param1).parameter(param2)
            .build()
            .runAsynchronous(output, errOutput);
        myAsyncProcess.waitFor();                                                 // wait until process ends
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
     * @throws IOException In case of I/O errors
     */
    @Test
    public void usageInputStream() throws InterruptedException, IOException {
        // discard
        ProcessBufferOutputStream output = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .java("com.github.toolarium.system.command.TestMain")
                .inheritJre()
                .inheritClassPath()
                .systemProperty(TestMain.SYSTEM_PROPERTY_READINPUT, TRUE)
            .build()
            .runAsynchronous(ProcessInputStreamSource.DISCARD, output, errOutput); // disable input stream
        myAsyncProcess.waitFor();                                                  // wait until process ends
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        assertEquals("", ProcessStreamUtil.getInstance().removeCR(output.toString()));
        assertEquals("", ProcessStreamUtil.getInstance().removeCR(errOutput.toString()));
        
        // buffer
        output = new ProcessBufferOutputStream();
        ProcessInputStreamSource buffer = ProcessInputStreamSource.BUFFER;         // create auffer inputstream
        buffer.setBuffer(TEST_BUFFER);
        myAsyncProcess = SystemCommandExecuterFactory.builder()
             .java("com.github.toolarium.system.command.TestMain")
                .inheritJre()                                                      // inherit jre
                .inheritClassPath()                                                // inherit classpath
                .systemProperty(TestMain.SYSTEM_PROPERTY_READINPUT, TRUE)          // system property force TestMain to read from inputstream 
                .build()
                .runAsynchronous(buffer, output, errOutput);
        myAsyncProcess.waitFor();                                                  // wait until process ends
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        assertEquals(TEST_BUFFER + NL, ProcessStreamUtil.getInstance().removeCR(output.toString()));
        assertEquals("", ProcessStreamUtil.getInstance().removeCR(errOutput.toString()));

        // file
        output = new ProcessBufferOutputStream();
        ProcessInputStreamSource file = ProcessInputStreamSource.FILE;             // create file as inputstream
        File inputFile = ScriptUtil.getInstance().createTempFile(new SystemCommandGroup(), "testinput.txt");
        Files.writeString(inputFile.toPath(), "input content", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        file.setFile(inputFile);
        myAsyncProcess = SystemCommandExecuterFactory.builder()
             .java("com.github.toolarium.system.command.TestMain")
                .inheritJre()                                                      // inherit jre
                .inheritClassPath()                                                // inherit classpath
                .systemProperty(TestMain.SYSTEM_PROPERTY_READINPUT, TRUE)        // system property force TestMain to read from inputstream 
                .build()
                .runAsynchronous(file, output, errOutput);
        myAsyncProcess.waitFor();                                                  // wait until process ends
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        assertEquals("input content\n", ProcessStreamUtil.getInstance().removeCR(output.toString()));
        assertEquals("", ProcessStreamUtil.getInstance().removeCR(errOutput.toString()));
    }

    
    /**
     * Shows the usage
     * 
     * @throws InterruptedException in case of a thread interruption
     */
    @Test
    public void usageChangeJavaUser() throws InterruptedException {
        final String param1 = "-param1";
        final String param2 = "-newValue=this is a parameter with spaces";
        final String envValue = "1";
        final String sysValue = "new value";
        final int exitValue = 1;
        
        String javaUser = "myUser";
        ProcessBufferOutputStream output = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .java("com.github.toolarium.system.command.TestMain ")
                .inheritJre()                                                      // inherit jre
                .inheritClassPath()                                                // inherit classpath
                .javaUser(javaUser)                                                // set java user
                .environmentVariable(TestMain.ENV_KEY, envValue)                   // set an additional environment variable
                .javaMemory("256M", "1024M")                                       // set memory
                .systemProperty(TestMain.SYSTEM_PROPERTY_KEY, sysValue)            // set system properties
                .systemProperty(TestMain.SYSTEM_PROPERTY_EXIT_VALUE, "" + exitValue)
                .systemProperty(TestMain.SYSTEM_PROPERTY_PRINT_VERBOSE, TRUE)
                .parameter(param1).parameter(param2)                               // set program parameter
            .build()
            .runAsynchronous(ProcessInputStreamSource.INHERIT, output, errOutput); // write output and error to defined buffer
        myAsyncProcess.waitFor();                                                  // wait until process ends
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        assertEquals(exitValue, myAsyncProcess.getExitValue());
        //assertNotNull(myAsyncProcess.getTotalCpuDuration());
        assertTestMainOut(output, javaUser, null, envValue, sysValue, param1, param2);
        assertTestMainErr(errOutput);
    }


    /**
     * Shows the usage
     * 
     * @throws InterruptedException in case of a thread interruption
     */
    @Test
    public void usageJavaVersion() throws InterruptedException {
        ProcessBufferOutputStream output = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .java("-version")
            .build()
            .runAsynchronous(ProcessInputStreamSource.INHERIT, output, errOutput);
        myAsyncProcess.waitFor();
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
    }
}
