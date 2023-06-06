/*
 * ScriptUtilTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;


/**
 * Test the {@link ProcessBuilderUtil}.
 *  
 * @author patrick
 */
public class ScriptUtilTest {

    /**
     * Test the prepare string
     */
    @Test public void testPrepareString() {
        assertEquals("....", ScriptUtil.getInstance().prepareString(".", 4));
        assertEquals("####", ScriptUtil.getInstance().prepareString("#", 4));
        assertEquals(":::::", ScriptUtil.getInstance().prepareString("::", 5));
    }
    
    
    /**
     * Test read write pid files
     */
    @Test
    public void testReadWritePidFile() {
        Long pid = 98765L;
        Path pidFileFolder = Paths.get("build/pidtest");
        Path pidFile1 = ScriptUtil.getInstance().createPidFile(pidFileFolder, "name1", pid++);
        assertNotNull(pidFile1);
        Path pidFile2 = ScriptUtil.getInstance().createPidFile(pidFileFolder, "name2", pid);
        assertNotNull(pidFile2);

        assertTrue(ScriptUtil.getInstance().hasNoRunningProcesses(pidFileFolder));

        Path pidFile3 = ScriptUtil.getInstance().createPidFile(pidFileFolder, "name3", ProcessHandle.current().pid());
        assertNotNull(pidFile3);

        assertFalse(ScriptUtil.getInstance().hasNoRunningProcesses(pidFileFolder));
    }

    
    /**
     * Test prepare id from name
     */
    @Test
    public void testprepareIdFromName() {
        assertEquals("pidtest", ScriptUtil.getInstance().prepareIdFromName(Paths.get("build/pidtest").toString()));
    }
    
    
    /**
     * Test thresold
     */
    @Test
    public void thresholdTest() {
        assertTrue(ScriptUtil.getInstance().hasReachedThresholdValue(System.currentTimeMillis() - 1000, 50));
        assertTrue(ScriptUtil.getInstance().hasReachedThresholdValue(System.currentTimeMillis() - 1000, 1000));
        assertFalse(ScriptUtil.getInstance().hasReachedThresholdValue(System.currentTimeMillis() - 1000, 1001));
    }


    /**
     * Test the select invalid process directories
     *
     * @throws IOException In case of an I/O error
     * @throws InterruptedException In case of a thread interrupt
     */
    @Test
    public void testSelectInvalidProcessDirectories() throws IOException, InterruptedException {
        Long pid = 98765L;
        Path pidFileFolder = Paths.get("build/pidtest");
        ProcessStreamUtil.getInstance().deleteDirectory(pidFileFolder);
        
        Path pidFile1 = ScriptUtil.getInstance().createPidFile(Paths.get(pidFileFolder.toString() + "/t1"), "name1", pid++);
        assertNotNull(pidFile1);
        Path pidFile2 = ScriptUtil.getInstance().createPidFile(Paths.get(pidFileFolder.toString() + "/t2"), "name2", pid++);
        assertNotNull(pidFile2);
        Path pidFile3 = ScriptUtil.getInstance().createPidFile(Paths.get(pidFileFolder.toString() + "/t3"), "name3", ProcessHandle.current().pid());
        assertNotNull(pidFile3);
        Path pidFile4 = ScriptUtil.getInstance().createPidFile(Paths.get(pidFileFolder.toString() + "/t4"), "name4", pid);
        assertNotNull(pidFile4);
        Path lockFile4 = ScriptUtil.getInstance().createLockFile(pidFileFolder, "t4");
        assertNotNull(lockFile4);
        Path pidFile5 = ScriptUtil.getInstance().createPidFile(Paths.get(pidFileFolder.toString() + "/t5"), "name5", ProcessHandle.current().pid());
        assertNotNull(pidFile5);
        Path lockFile5 = ScriptUtil.getInstance().createLockFile(pidFileFolder, "t5");
        assertNotNull(lockFile5);
        
        Thread.sleep(110);

        List<Path> list = ScriptUtil.getInstance().selectInvalidProcessDirectories(pidFileFolder, 100, 500);
        assertEquals(Arrays.asList(pidFile1.getParent(), pidFile2.getParent()), list);

        Thread.sleep(500);

        list = ScriptUtil.getInstance().selectInvalidProcessDirectories(pidFileFolder, 100, 500);
        assertEquals(Arrays.asList(pidFile1.getParent(), pidFile2.getParent(), pidFile4.getParent()), list);
    }
}


