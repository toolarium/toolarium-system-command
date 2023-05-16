/*
 * AbstractProcessTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.system.command.process.IProcess;
import com.github.toolarium.system.command.process.util.ProcessBuilderUtil;
import com.github.toolarium.system.command.process.util.ScriptUtil;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract process test
 *  
 * @author patrick
 */
public class AbstractProcessTest {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractProcessTest.class);
    
    
    /**
     * Before test
     * 
     * @param testInfo the test info
     */
    @BeforeEach() 
    public void startLog(TestInfo testInfo) {
        final String header = "- START: " + testInfo.getDisplayName() + " "; 
        LOG.info(header + ScriptUtil.getInstance().prepareString("-", (80 - header.length())));
    }

    
    /**
     * Before test
     * 
     * @param testInfo the test info
     */
    @AfterEach() 
    public void endLog(TestInfo testInfo) {
        final String header = "- END: " + testInfo.getDisplayName() + " "; 
        LOG.info(header + ScriptUtil.getInstance().prepareString("-", (80 - header.length())) + "\n");
    }

    
    /**
     * Get environment key
     *
     * @param key the key
     * @return the prepared expression
     */
    protected String prepareGetEnvValue(String key) {
        if (isWindows()) {
            return "%" + key + "%";
        }
        
        return "$" + key;
    }

    
    /**
     * Check if it is a windows os
     *
     * @return true if it is windows
     */
    protected boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    
    /**
     * Get all environment variables
     *
     * @return the command to get all environment variables
     */
    protected String selectAllEnvironmentVariables() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            return "set";
        }
        
        return "env";
    }

    
    /**
     * Assert synchronous process
     * 
     * @param process the process
     * @param workingPath the working path or null for current working directory
     * @param env the environment or current environment variables
     * @param returnValue the return value
     * @param commands the commands
     * @return the process
     */
    protected IProcess assertProcess(IProcess process, String workingPath, Map<String, String> env, int returnValue, String... commands) {
        
        ISystemCommand systemCommand = process.getSystemCommandList().get(0);
        assertEquals(systemCommand.getProcessEnvironment().getUser(), System.getProperty("user.name").trim());
        
        if (workingPath == null) {
            assertEquals(systemCommand.getProcessEnvironment().getWorkingPath(), System.getProperty("user.dir"));
        } else {
            assertEquals(systemCommand.getProcessEnvironment().getWorkingPath(), workingPath);
        }
        
        if (workingPath == null) {
            Map<String, String> e = new LinkedHashMap<>(System.getenv());
            if (systemCommand.getProcessEnvironment().getEnvironmentVariables().containsKey(ProcessBuilderUtil.TEMP)) {
                e.put(ProcessBuilderUtil.TEMP, systemCommand.getProcessEnvironment().getEnvironmentVariables().get(ProcessBuilderUtil.TEMP));
            }
                
            assertEquals(systemCommand.getProcessEnvironment().getEnvironmentVariables(), e);
        } else {
            Map<String, String> e = new LinkedHashMap<>(env);
            e.put(ProcessBuilderUtil.TEMP, systemCommand.getProcessEnvironment().getEnvironmentVariables().get(ProcessBuilderUtil.TEMP));
            assertEquals(systemCommand.getProcessEnvironment().getEnvironmentVariables(), e);
        }
            
        assertEquals(systemCommand.getProcessEnvironment().getOS(), System.getProperty("os.name").trim().toLowerCase());
        assertEquals(systemCommand.getProcessEnvironment().getOSVersion(), System.getProperty("os.version").trim());
        assertEquals(systemCommand.getProcessEnvironment().getArchitecture(), System.getProperty("os.arch").trim());
        assertNull(systemCommand.getShell());
        
        String execCommands = "";
        String displayCommands = "";
        for (ISystemCommand sc : process.getSystemCommandList()) {
            if (!execCommands.isEmpty()) {
                execCommands += ", ";
            }
            execCommands += sc.toString();
            
            if (!displayCommands.isEmpty()) {
                displayCommands += ", ";
            }
            displayCommands += sc.toString(true);
        }
        
        if (commands != null) {
            assertEquals("[" + execCommands + "]",  "" + Arrays.asList(commands));
            assertEquals("[" + displayCommands + "]",  "" + Arrays.asList(commands));
        }
        
        assertTrue(process.getPid() > 0);        
        assertTrue(Instant.now().isAfter(process.getStartTime()));
        // assertTrue(process.getTotalCpuDuration().getNano() > 0);
        assertEquals(process.getExitValue(), returnValue, "Invalid exit value -> " + execCommands);
        return process;
    }
}
