/*
 * SystemCommandExecuterFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import com.github.toolarium.system.command.builder.ISystemCommandExecuterTypeBuilder;
import com.github.toolarium.system.command.builder.system.SystemCommandExecuterTypeBuilder;
import com.github.toolarium.system.command.dto.ISystemCommand;
import com.github.toolarium.system.command.dto.group.ISystemCommandGroup;
import com.github.toolarium.system.command.dto.list.ISystemCommandGroupList;
import com.github.toolarium.system.command.dto.list.SystemCommandGroupList;
import com.github.toolarium.system.command.executer.ISystemCommandExecuter;
import com.github.toolarium.system.command.executer.impl.LinuxSystemCommandExecuterImpl;
import com.github.toolarium.system.command.executer.impl.UnixSystemCommandExecuterImpl;
import com.github.toolarium.system.command.executer.impl.WindowsSystemCommandExecuterImpl;
import com.github.toolarium.system.command.process.folder.FolderCleanupService;
import com.github.toolarium.system.command.process.thread.NameableThreadFactory;
import com.github.toolarium.system.command.util.OSUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The system command excuter factory
 *
 * @author patrick
 */
public final class SystemCommandExecuterFactory {
    /** Get the default initial delay of the folder cleanup service */ 
    public static final long INITIAL_DELAY  = 0;
    
    /** Get the default period of the folder cleanup service */ 
    public static final long PERIOD = 5;
    
    /** Get the default timeunit of the folder cleanup service */ 
    public static final TimeUnit TIMEUNIT = TimeUnit.SECONDS;

    private static final String TOOLARIUM_SYSTEM_COMMAND_TEMP_SUBFOLDER = "toolarium-system-command";
    private static final Logger LOG = LoggerFactory.getLogger(SystemCommandExecuterFactory.class);
    private static NameableThreadFactory nameableThreadFactory = new NameableThreadFactory("folder");
    private ScheduledExecutorService folderCleanupService;
    private volatile Boolean folderCleanupServiceIsRunning;
    private long initialDelay = INITIAL_DELAY;
    private long period = PERIOD;
    private TimeUnit timeUnit = TIMEUNIT;
    private Path basePath;
    private long lockFolderThreshold = 1 * 60 * 60 * 1000; // one day


    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final SystemCommandExecuterFactory INSTANCE = new SystemCommandExecuterFactory();
    }


    /**
     * Constructor
     */
    private SystemCommandExecuterFactory() {
        folderCleanupServiceIsRunning = null;
        //startFolderCleanupService();

        setScriptFolderBasePath(null);
        Runtime.getRuntime().addShutdownHook(new Thread(SystemCommandExecuterFactory.class.getName() + ": Shutdown hook") {
            /**
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                stopFolderCleanupService();
            }
        });
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static SystemCommandExecuterFactory getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Create a system command executer type builder
     *
     * @return the system command executer type builder
     */
    public static ISystemCommandExecuterTypeBuilder builder() {
        return new SystemCommandExecuterTypeBuilder(new SystemCommandGroupList());
    }


    /**
     * Create a system command executer
     *
     * @param systemCommand the system command
     * @return the system command executer
     */
    public ISystemCommandExecuter createSystemCommandExecuter(ISystemCommand... systemCommand) {
        SystemCommandGroupList s = new SystemCommandGroupList();
        s.add(systemCommand);
        return createSystemCommandExecuter(s);
    }

    
    /**
     * Create a system command executer
     *
     * @param systemCommandGroup the system command group
     * @return the system command executer
     */
    /*
    public ISystemCommandExecuter createSystemCommandExecuter(ISystemCommandGroup systemCommandGroup) {
        SystemCommandGroupList s = new SystemCommandGroupList();
        s.add(systemCommandGroup);
        return createSystemCommandExecuter(s);
    }
*/
    
    /**
     * Create a system command executer
     *
     * @param systemCommandGroupList the system commandm group list
     * @return the system command executer
     */
    public ISystemCommandExecuter createSystemCommandExecuter(ISystemCommandGroupList systemCommandGroupList) {
        if (systemCommandGroupList.runAsScript() && folderCleanupServiceIsRunning == null) {
            LOG.debug("System command group list use runs as script: " + systemCommandGroupList.getId() + ".");
            startFolderCleanupService();
        }
        
        if (OSUtil.getInstance().isWindows()) {
            
            LOG.debug("Choose " + WindowsSystemCommandExecuterImpl.class.getName() + " as executer.");
            return new WindowsSystemCommandExecuterImpl(systemCommandGroupList);
        } else if (OSUtil.getInstance().isLinux()) {
            
            LOG.debug("Choose " + LinuxSystemCommandExecuterImpl.class.getName() + " as executer.");
            return new LinuxSystemCommandExecuterImpl(systemCommandGroupList);
        }

        LOG.debug("Choose " + UnixSystemCommandExecuterImpl.class.getName() + " as executer.");
        return new UnixSystemCommandExecuterImpl(systemCommandGroupList);
    }

    
    /**
     * Start the folder cleanup service
     */
    public void startFolderCleanupService() {
        if (folderCleanupServiceIsRunning != null && folderCleanupServiceIsRunning) {
            return;
        }

        synchronized (this) {
            folderCleanupServiceIsRunning = Boolean.TRUE;
            LOG.info("Start folder cleanup service...");
            folderCleanupService = Executors.newScheduledThreadPool(1, nameableThreadFactory);
            folderCleanupService.scheduleAtFixedRate(new FolderCleanupService(basePath, lockFolderThreshold), initialDelay, period, timeUnit);
        }
    }
    
    
    /**
     * Start the folder cleanup service
     * 
     * @param initialDelay the time to delay first execution
     * @param period the period between successive executions
     * @param timeUnit the time unit of the initialDelay and period parameters
     */
    public void startFolderCleanupService(long initialDelay, long period, TimeUnit timeUnit) {
        if (folderCleanupServiceIsRunning != null && folderCleanupServiceIsRunning) {
            return;
        }
        
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;
        startFolderCleanupService();
    }

    
    /**
     * Start the folder cleanup service
     * 
     * @param initialDelay the time to delay first execution
     * @param period the period between successive executions
     * @param timeUnit the time unit of the initialDelay and period parameters
     * @param lockFolderThreshold the lock folder threshold
     */
    public void startFolderCleanupService(long initialDelay, long period, TimeUnit timeUnit, long lockFolderThreshold) {
        if (folderCleanupServiceIsRunning != null && folderCleanupServiceIsRunning) {
            return;
        }
        
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;
        this.lockFolderThreshold = lockFolderThreshold;
        startFolderCleanupService();
    }


    /**
     * Stop the folder cleanup service
     */
    public void stopFolderCleanupService() {
        if (folderCleanupServiceIsRunning == null || !folderCleanupServiceIsRunning) {
            return;
        }

        try {
            synchronized (this) {
                if (folderCleanupServiceIsRunning != null && folderCleanupServiceIsRunning) {
                    LOG.info("Stop folder cleanup service...");
                    folderCleanupService.shutdown();
                    folderCleanupService = null;
                    folderCleanupServiceIsRunning = Boolean.FALSE;
                }
            }
        } catch (Exception e) {
            // NOP
        }
    }

    
    /**
     * Get the script folder base path
     *
     * @return the script folder base path
     */
    public Path getScriptFolderBasePath() {
        return basePath;
    }


    /**
     * Set the script folder base path
     *
     * @param inputBasePath the script folder base path
     */
    public void setScriptFolderBasePath(Path inputBasePath) {
        if (inputBasePath != null && inputBasePath.equals(basePath)) {
            return;
        }

        if (inputBasePath != null) {
            this.basePath = inputBasePath;
        } else {
            this.basePath = Path.of(System.getProperty("java.io.tmpdir").trim() + "/" + TOOLARIUM_SYSTEM_COMMAND_TEMP_SUBFOLDER);
            if (!this.basePath.toFile().exists()) {
                try {
                    LOG.warn("Create path [" + this.basePath + "].");
                    Files.createDirectories(getScriptFolderBasePath());
                } catch (IOException e) {
                    LOG.warn("Could not create path [" + this.basePath + "]: " + e.getMessage(), e);
                }
            }
        }

        if (folderCleanupServiceIsRunning != null && folderCleanupServiceIsRunning) {
            stopFolderCleanupService();
            startFolderCleanupService();
        }
    }
}
