/*
 * SystemCommandTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.system.command.AbstractProcessTest;
import com.github.toolarium.system.command.SystemCommandExecuterFactory;
import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.IProcess;
import com.github.toolarium.system.command.process.ISynchronousProcess;
import com.github.toolarium.system.command.process.stream.IProcessInputStream;
import com.github.toolarium.system.command.process.stream.ProcessStreamFactory;
import com.github.toolarium.system.command.process.stream.output.ProcessBufferOutputStream;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the system command 
 *  
 * @author patrick
 */
public class SystemCommandTest extends AbstractProcessTest {
    private static final Logger LOG = LoggerFactory.getLogger(SystemCommandTest.class);

    
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
        IProcessInputStream processInputStream = ProcessStreamFactory.getInstance().getStandardIn();
        ProcessBufferOutputStream output = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .system().command("dir")
            .build()
            .runAsynchronous(processInputStream, output, errOutput);
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
        IProcessInputStream processInputStream = ProcessStreamFactory.getInstance().getStandardIn();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .system().command("dir")
            .build()
            .runAsynchronous(processInputStream, null, null);
        myAsyncProcess.waitFor();
        assertNotNull(myAsyncProcess);
        assertNotNull(myAsyncProcess.getExitValue());
        //assertNotNull(myAsyncProcess.getTotalCpuDuration());
    }
}
