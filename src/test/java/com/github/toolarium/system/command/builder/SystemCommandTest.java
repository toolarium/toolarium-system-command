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
import com.github.toolarium.system.command.util.OSUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the system command 
 *  
 * @author patrick
 */
public class SystemCommandTest extends AbstractProcessTest {
    private static final String DIR = "dir";
    private static final Logger LOG = LoggerFactory.getLogger(SystemCommandTest.class);

    
    /**
     * Shows the usage
     */
    @Test
    public void usageSync() {
        ISynchronousProcess mySyncProcess = SystemCommandExecuterFactory.builder()
              .system().command(DIR)
              .build()
              .runSynchronous();
        assertNotNull(mySyncProcess);
        assertNotNull(mySyncProcess.getExitValue());
        //assertNotNull(mySyncProcess.getTotalCpuDuration());
        assertNotNull(mySyncProcess.getOutput());
        assertNotNull(mySyncProcess.getErrorOutput());
        
        
        
        IProcess process2 = SystemCommandExecuterFactory.builder().system().command(DIR).build().runSynchronous(10 /* numberOfSecondsToWait */);
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
            .system().command(DIR)
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
            .system().command(DIR)
            .build()
            .runAsynchronous(processInputStream, null, null);
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
    public void usageAsyncPipe() throws InterruptedException {
        String listDirectory = "ls -a";
        String grep = "grep \"\\,\\.\"";
        if (OSUtil.getInstance().isWindows()) {
            listDirectory = DIR;
            grep = "findstr /C:\"..\"";
        }
        
        IProcessInputStream processInputStream = ProcessStreamFactory.getInstance().getStandardIn();
        ProcessBufferOutputStream output = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
        IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
            .system().command(listDirectory).pipe()
            .system().command(grep)
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
     * Manual process builder test
     *
     * @throws IOException In case of an I/O error
     * @throws InterruptedException In case of an intterupt
     */
    @Test
    public void manualAsyncPipeTest() throws IOException, InterruptedException {
        String[] shell = new String[] {"sh", "-c" };
        String listDirectory = "ls -a";
        String grep = "grep \"\\,\\.\"";
        if (OSUtil.getInstance().isWindows()) {
            shell = new String[] {"cmd.exe", "/c"};
            listDirectory = DIR;
            grep = "findstr /C:\"..\"";
        }

        ProcessBuilder listProcessBuilder = new ProcessBuilder();
        listProcessBuilder.command(shell[0], shell[1], listDirectory);        
        listProcessBuilder.redirectInput(Redirect.INHERIT);
        listProcessBuilder.redirectOutput(Redirect.PIPE);
        listProcessBuilder.redirectError(Redirect.PIPE);
        
        ProcessBuilder grepProcessBuilder = new ProcessBuilder();
        grepProcessBuilder.command(shell[0], shell[1], grep);        
        grepProcessBuilder.redirectInput(Redirect.PIPE);

        List<java.lang.Process> processList = ProcessBuilder.startPipeline(Arrays.asList(new ProcessBuilder[] {listProcessBuilder, grepProcessBuilder}));
        BufferedReader reader = new BufferedReader(new InputStreamReader(processList.get(processList.size() - 1).getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            LOG.info("->" + line);
        }
        
        int exitCode = processList.get(processList.size() - 1).waitFor();
        LOG.info("Exited with error code : " + exitCode);
    }

}
