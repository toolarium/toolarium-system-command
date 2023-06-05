/*
 * SystemCommandFactoryTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.IProcess;
import com.github.toolarium.system.command.process.ISynchronousProcess;
import com.github.toolarium.system.command.process.stream.ProcessInputStreamSource;
import com.github.toolarium.system.command.process.stream.impl.ProcessBufferOutputStream;
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
}
