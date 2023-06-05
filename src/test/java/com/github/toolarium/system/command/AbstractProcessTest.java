/*
 * AbstractProcessTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.system.command.dto.ISystemCommand;
import com.github.toolarium.system.command.dto.ISystemCommand.SystemCommandExecutionStatusResult;
import com.github.toolarium.system.command.dto.group.ISystemCommandGroup;
import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import com.github.toolarium.system.command.process.IProcess;
import com.github.toolarium.system.command.process.stream.output.ProcessBufferOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import com.github.toolarium.system.command.process.util.ProcessBuilderUtil;
import com.github.toolarium.system.command.process.util.ScriptUtil;
import com.github.toolarium.system.command.util.OSUtil;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
    /** new line */
    protected static final String NL = "\n";
    /** true */
    protected static final String TRUE = "true";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractProcessTest.class);
    
    
    /**
     * Before test
     * 
     * @param testInfo the test info
     */
    @BeforeEach() 
    public void startLog(TestInfo testInfo) {
        final String header = "--- [START] " + testInfo.getDisplayName() + " "; 
        LOG.info(header + ScriptUtil.getInstance().prepareString("-", (120 - header.length())));
    }

    
    /**
     * Before test
     * 
     * @param testInfo the test info
     */
    @AfterEach() 
    public void endLog(TestInfo testInfo) {
        final String header = "--- [END]   " + testInfo.getDisplayName() + " "; 
        LOG.info(header + ScriptUtil.getInstance().prepareString("-", (120 - header.length())) + "\n");
    }

    
    /**
     * Get environment key
     *
     * @param key the key
     * @param hasConfition true it it has condition
     * @return the prepared expression
     */
    protected String prepareGetEnvValue(String key, boolean hasConfition) {
        if (OSUtil.getInstance().isWindows()) {
            if (hasConfition) {
                return "!" + key + "!";
            } else {
                return "%" + key + "%";
            }
        }
        
        return "$" + key;
    }

    
    /**
     * Get all environment variables
     *
     * @return the command to get all environment variables
     */
    protected String selectAllEnvironmentVariables() {
        if (OSUtil.getInstance().isWindows()) {
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
        
        ISystemCommandGroupList systemCommandGroupList = process.getSystemCommandGroupList();
        assertNotNull(systemCommandGroupList);
        assertEquals(1, systemCommandGroupList.size());
        ISystemCommandGroup systemCommandGroup = systemCommandGroupList.iterator().next();
        assertNotNull(systemCommandGroup);
      
        int commandCounter = 0;
        List<String> commandList = new LinkedList<>(Arrays.asList(commands));
        Iterator<ISystemCommandGroup> l = systemCommandGroupList.iterator();
        while (l.hasNext()) {
            ISystemCommandGroup g = l.next();
            Iterator<ISystemCommand> s = g.iterator();
            while (s.hasNext()) {
                ISystemCommand c = s.next();
                if (c.getSystemCommandExecutionStatusResult() != null && !SystemCommandExecutionStatusResult.SUCCESS_OR_ERROR.equals(c.getSystemCommandExecutionStatusResult())) {
                    if (SystemCommandExecutionStatusResult.SUCCESS.equals(c.getSystemCommandExecutionStatusResult())) {
                        String ss = commandList.remove(commandCounter);
                        LOG.debug("Remove " + ss);
                    } else {
                        commandCounter++;
                    }
                } else {
                    commandCounter++;
                }
            }
        }
        
        //assertEquals(commands.length, systemCommandGroup.size(), "" + systemCommandGroup);
        ISystemCommand systemCommand = systemCommandGroup.iterator().next();
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
        
        String execCommands = systemCommandGroupList.toString(true).replaceAll("\n", ", ");
        String displayCommands = systemCommandGroupList.toString().replaceAll("\n", ", ");
            
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


    /**
     * Assert test main output
     *
     * @param output the output stream to verify
     * @param inputUser the user
     * @param inputWorkingDir the working directory
     * @param envValue the env value
     * @param sysValue the sys value
     * @param param1 the parameter 1
     * @param param2 the parameter 2
     */
    protected void assertTestMainOut(ProcessBufferOutputStream output, String inputUser, String inputWorkingDir, String envValue, String sysValue, String param1, String param2) {

        String user = inputUser;
        String workingDir = inputWorkingDir;

        if (user == null) {
            user = System.getProperty("user.name");
        }

        if (workingDir == null) {
            workingDir = System.getProperty("user.dir");
        }
        
        assertEquals(TestMain.MAIN_HEADRER + TestMain.class.getName() + NL 
                       + TestMain.PARAMETERS_TITLE + NL 
                       + TestMain.PARAMETERS_PREFIX + "0" + TestMain.PARAMETERS_APPENDIX + param1 + NL
                       + TestMain.PARAMETERS_PREFIX + "1" + TestMain.PARAMETERS_APPENDIX + param2 + NL
                       + TestMain.STD_TEST + envValue + "/" + sysValue + " - [" + user + "]: " + workingDir + NL, 
                     ProcessStreamUtil.getInstance().removeCR(output.toString()));
    }

    
    /**
     * Assert test main error output 
     *
     * @param errOutput the error output stream to verify
     */
    protected void assertTestMainErr(ProcessBufferOutputStream errOutput) {
        assertEquals(TestMain.STD_ERR_TEST + NL, ProcessStreamUtil.getInstance().removeCR(errOutput.toString()));
    }
}
