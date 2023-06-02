/*
 * SystemCommandFactoryTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.system.command.dto.SystemCommandGroup;
import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.IProcess;
import com.github.toolarium.system.command.process.ISynchronousProcess;
import com.github.toolarium.system.command.process.dto.ProcessInputStreamSource;
import com.github.toolarium.system.command.process.stream.impl.ProcessBufferOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import com.github.toolarium.system.command.process.util.ScriptUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the system command 
 *  
 * @author patrick
 */
public class SystemCommandFactoryTest extends AbstractProcessTest {
    private static final Logger LOG = LoggerFactory.getLogger(SystemCommandFactoryTest.class);
    private static final String NL = "\n";

    /**
     * Shows the usage
     */
    @Test
    public void usageSync() {
        ISynchronousProcess mySyncProcess = SystemCommandExecuterFactory.builder()
              .system().command("dir")
              .build()
              .runSynchronous();
        assertNotNull(mySyncProcess);
        assertNotNull(mySyncProcess.getExitValue());
        //assertNotNull(mySyncProcess.getTotalCpuDuration());
        assertNotNull(mySyncProcess.getOutput());
        assertNotNull(mySyncProcess.getErrorOutput());
        
        
        
        IProcess process2 = SystemCommandExecuterFactory.builder().system().command("dir").build().runSynchronous(10 /* numberOfSecondsToWait */);
        assertNotNull(process2);
    }


    /**
     * Shows the usage
     * 
     * @throws InterruptedException in case of a thread interruption
     */
    @Test
    public void usageAsync() throws InterruptedException {
        ProcessBufferOutputStream output = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .system().command("dir")
            .build()
            .runAsynchronous(ProcessInputStreamSource.INHERIT, output, errOutput);
        assertNotNull(myAsyncProcess.getInputStream()); // the input stream where we can bypass
        myAsyncProcess.waitFor();
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        //assertNotNull(myAsyncProcess.getTotalCpuDuration());
        LOG.debug(output.toString());
        assertFalse(output.toString().isBlank());
        assertNotNull(output.toString());
        assertTrue(errOutput.toString().isBlank());
        
    }


    /**
     * Shows the usage
     * 
     * @throws InterruptedException in case of a thread interruption
     */
    @Test
    public void usageAsyncSilent() throws InterruptedException {
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .system().command("dir")
            .build()
            .runAsynchronous(ProcessInputStreamSource.INHERIT, null, null);
        myAsyncProcess.waitFor();
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        //assertNotNull(myAsyncProcess.getTotalCpuDuration());
    }
    
    
    /**
     * Shows the usage
     * 
     * @throws InterruptedException in case of a thread interruption
     */
    @Test
    public void usageJava() throws InterruptedException {
        final String param1 = "-param1";
        final String param2 = "-newValue=this is a parameter with spaces";
        final String envValue = "1";
        final String sysValue = "new value";
        final int exitValue = 1;
        
        ProcessBufferOutputStream output = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .java("com.github.toolarium.system.command.TestMain")
                .environmentVariable("CLASSPATH", "build/classes/java/test")
                .environmentVariable(TestMain.ENV_KEY, envValue)
                //.javaMemory("256M", "1024M")
                .systemProperty(TestMain.SYSTEM_PROPERTY_KEY, sysValue)
                .systemProperty(TestMain.SYSTEM_PROPERTY_EXIT_VALUE, "" + exitValue)
                .systemProperty(TestMain.SYSTEM_PROPERTY_PRINT_VERBOSE, "true")
                .parameter(param1).parameter(param2)
            .build()
            .runAsynchronous(ProcessInputStreamSource.INHERIT, output, errOutput);
        myAsyncProcess.waitFor();
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        assertEquals(exitValue, myAsyncProcess.getExitValue());
        assertEquals(TestMain.MAIN_HEADRER + TestMain.class.getName() + NL 
                     + TestMain.PARAMETERS_TITLE + NL 
                     + TestMain.PARAMETERS_PREFIX + "0" + TestMain.PARAMETERS_APPENDIX + param1 + NL
                     + TestMain.PARAMETERS_PREFIX + "1" + TestMain.PARAMETERS_APPENDIX + param2 + NL
                     + TestMain.STD_TEST + envValue + "/" + sysValue + NL, ProcessStreamUtil.getInstance().removeCR(output.toString()));
        assertEquals(TestMain.STD_ERR_TEST + NL, ProcessStreamUtil.getInstance().removeCR(errOutput.toString()));
        //assertNotNull(myAsyncProcess.getTotalCpuDuration());
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
                .environmentVariable("CLASSPATH", "build/classes/java/test")
                .systemProperty(TestMain.SYSTEM_PROPERTY_READINPUT, "true")
            .build()
            .runAsynchronous(ProcessInputStreamSource.DISCARD, output, errOutput);
        myAsyncProcess.waitFor();
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        assertEquals("", ProcessStreamUtil.getInstance().removeCR(output.toString()));
        assertEquals("", ProcessStreamUtil.getInstance().removeCR(errOutput.toString()));
        
        // buffer
        output = new ProcessBufferOutputStream();
        ProcessInputStreamSource buffer = ProcessInputStreamSource.BUFFER;
        buffer.setBuffer("test buffer");
        myAsyncProcess = SystemCommandExecuterFactory.builder()
                .java("com.github.toolarium.system.command.TestMain")
                    .environmentVariable("CLASSPATH", "build/classes/java/test")
                    .systemProperty(TestMain.SYSTEM_PROPERTY_READINPUT, "true")
                .build()
                .runAsynchronous(buffer, output, errOutput);
        myAsyncProcess.waitFor();
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        assertEquals("test buffer\n", ProcessStreamUtil.getInstance().removeCR(output.toString()));
        assertEquals("", ProcessStreamUtil.getInstance().removeCR(errOutput.toString()));

        // file
        output = new ProcessBufferOutputStream();
        ProcessInputStreamSource file = ProcessInputStreamSource.FILE;
        File inputFile = ScriptUtil.getInstance().createTempFile(new SystemCommandGroup(), "testinput.txt");
        Files.writeString(inputFile.toPath(), "input content", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        file.setFile(inputFile);
        myAsyncProcess = SystemCommandExecuterFactory.builder()
                .java("com.github.toolarium.system.command.TestMain")
                    .environmentVariable("CLASSPATH", "build/classes/java/test")
                    .systemProperty(TestMain.SYSTEM_PROPERTY_READINPUT, "true")
                .build()
                .runAsynchronous(file, output, errOutput);
        myAsyncProcess.waitFor();
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
    public void usageJava2() throws InterruptedException {
        ProcessBufferOutputStream output = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .java("-version")
            .build()
            .runAsynchronous(ProcessInputStreamSource.INHERIT, output, errOutput);
        myAsyncProcess.waitFor();
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        //assertNotNull(myAsyncProcess.getTotalCpuDuration());
    }
}
