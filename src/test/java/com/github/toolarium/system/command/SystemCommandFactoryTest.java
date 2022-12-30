/*
 * SystemCommandFactoryTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.github.toolarium.system.command.impl.util.StreamUtil;
import java.io.IOException;
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
        Process process = SystemCommandExecuterFactory.getInstance().createSystemCommandExecuter().executeCommand(null, 10, "dir");
        boolean result = process.isAlive();
        assertFalse(result);
        result = SystemCommandExecuterFactory.getInstance().createSystemCommandExecuter().executeCommand(null, 10, "ls -ltra").isAlive();
        assertFalse(result);
    }


    /**
     * Test echo output
     *
     * @throws IOException in case of error
     */
    @Test
    public void echoOkTest() throws IOException {
        String expectedResult = "ok\n";
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            expectedResult = "ok \n";

        }
        assertEquals(StreamUtil.getInstance().convertStreamToStr(SystemCommandExecuterFactory.getInstance()
                        .createSystemCommandExecuter().executeCommand(null, "echo ok").getInputStream()).replace('\r', ' '), expectedResult);
    }

    
    /**
     * Test echo output
     *
     * @throws IOException in case of error
     */
    //@Test
    public void multipleCommandsTest() throws IOException {
        String expectedResult = "ok + ok + ok\n";
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            expectedResult = "ok + ok + ok\n";

        }
        
        assertEquals(StreamUtil.getInstance().convertStreamToStr(SystemCommandExecuterFactory.getInstance()
                        .createSystemCommandExecuter().executeCommand(null, "echo ok", " + ok", " + ok").getInputStream()).replace('\r', ' '), expectedResult);
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
        
        Process process = SystemCommandExecuterFactory.getInstance().createSystemCommandExecuter().executeCommand(null, SystemCommandFactory.getInstance().createSleepCommand(seconds));
        process.waitFor();
        LOG.debug("Exit value: " + process.exitValue());

        process = SystemCommandExecuterFactory.getInstance().createSystemCommandExecuter().executeCommand(null, seconds + 1, SystemCommandFactory.getInstance().createSleepCommand(seconds));
        LOG.debug("Exit value: " + process.exitValue());

        process = SystemCommandExecuterFactory.getInstance().createSystemCommandExecuter().executeCommand(null, (2 * seconds) + 1, SystemCommandFactory.getInstance().createSleepCommand(2 * seconds));
        LOG.debug("Exit value: " + process.exitValue());
    }
}
