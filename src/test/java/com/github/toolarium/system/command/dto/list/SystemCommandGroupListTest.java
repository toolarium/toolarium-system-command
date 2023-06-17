/*
 * SystemCommandGroupListTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.dto.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;


/**
 * Test {@link SystemCommandGroupList}.
 *  
 * @author patrick
 */
public class SystemCommandGroupListTest {

    /**
     * Test lock and reset
     *
     * @throws InterruptedException In case of an interrupt
     */
    @Test
    public void testLock() throws InterruptedException {
        SystemCommandGroupList systemCommandGroupList = new SystemCommandGroupList();
        assertNull(systemCommandGroupList.getLockTimeout());
        
        systemCommandGroupList.lock(5);
        Instant timeout = Instant.ofEpochMilli(Instant.now().toEpochMilli() + (1000 * 5));
        
        assertNotNull(systemCommandGroupList.getLockTimeout());
        assertTrue(Instant.now().isBefore(timeout));
        assertTrue(Instant.now().isBefore(systemCommandGroupList.getLockTimeout()));
        
        Long seconds1 = timeout.getEpochSecond();
        assertEquals(seconds1, systemCommandGroupList.getLockTimeout().getEpochSecond());

        // sleep
        Thread.sleep(1000);
        
        // reset lock timeout
        systemCommandGroupList.resetLock();

        assertNotNull(systemCommandGroupList.getLockTimeout());
        assertTrue(Instant.now().isBefore(systemCommandGroupList.getLockTimeout()));
        assertEquals(seconds1 + 1, systemCommandGroupList.getLockTimeout().getEpochSecond());
    }
}
