/*
 * SystemCommandExecuterFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import com.github.toolarium.system.command.builder.ISystemCommandExecuterTypeBuilder;
import com.github.toolarium.system.command.builder.SystemCommandExecuterTypeBuilder;
import com.github.toolarium.system.command.dto.ISystemCommandGroupList;
import com.github.toolarium.system.command.dto.SystemCommandGroupList;
import com.github.toolarium.system.command.executer.ISystemCommandExecuter;
import com.github.toolarium.system.command.executer.LinuxSystemCommandExecuterImpl;
import com.github.toolarium.system.command.executer.UnixSystemCommandExecuterImpl;
import com.github.toolarium.system.command.executer.WindowsSystemCommandExecuterImpl;
import com.github.toolarium.system.command.process.temp.TempFolderCleanupService;
import com.github.toolarium.system.command.process.thread.NameableThreadFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(SystemCommandExecuterFactory.class);
    private static NameableThreadFactory nameableThreadFactory = new NameableThreadFactory("temp");
    private ScheduledExecutorService tempFolderCleanupService;
    private long initialDelay = 0;
    private long period = 5;
    private TimeUnit timeUnit = TimeUnit.SECONDS;


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
        //startTempFolderCleanupService();

        Runtime.getRuntime().addShutdownHook(new Thread(SystemCommandExecuterFactory.class.getName() + ": Shutdown hook") {
            /**
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                stopTempFolderCleanupService();
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
     * Start the temp folder cleanup service
     */
    public void startTempFolderCleanupService() {
        startTempFolderCleanupService(initialDelay, period, timeUnit);
    }
    
    
    /**
     * Start the temp folder cleanup service
     * 
     * @param initialDelay the time to delay first execution
     * @param period the period between successive executions
     * @param timeUnit the time unit of the initialDelay and period parameters
     */
    public void startTempFolderCleanupService(long initialDelay, long period, TimeUnit timeUnit) {
        if (tempFolderCleanupService != null) {
            return;
        }
      
        LOG.info("Start temp folder cleanup service...");
        tempFolderCleanupService = Executors.newScheduledThreadPool(1, nameableThreadFactory);
        tempFolderCleanupService.scheduleAtFixedRate(new TempFolderCleanupService(), initialDelay, period, timeUnit);
    }
           

    /**
     * Stop the temp folder cleanup service
     */
    public void stopTempFolderCleanupService() {
        try {
            if (tempFolderCleanupService != null) {
                LOG.info("Stop temp folder cleanup service...");
                tempFolderCleanupService.shutdown();
                tempFolderCleanupService = null;
            }
        } catch (Exception e) {
            // NOP
        }
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
        ISystemCommandGroupList s = new SystemCommandGroupList();
        s.add(systemCommand);
        return createSystemCommandExecuter(s);
    }

    
    /**
     * Create a system command executer
     *
     * @param systemCommandGroupList the system commandm group list
     * @return the system command executer
     */
    public ISystemCommandExecuter createSystemCommandExecuter(ISystemCommandGroupList systemCommandGroupList) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            
            LOG.debug("Choose " + WindowsSystemCommandExecuterImpl.class.getName() + " as executer.");
            return new WindowsSystemCommandExecuterImpl(systemCommandGroupList);
        } else if (osName.startsWith("linux")) {
            
            LOG.debug("Choose " + LinuxSystemCommandExecuterImpl.class.getName() + " as executer.");
            return new LinuxSystemCommandExecuterImpl(systemCommandGroupList);
        }

        LOG.debug("Choose " + UnixSystemCommandExecuterImpl.class.getName() + " as executer.");
        return new UnixSystemCommandExecuterImpl(systemCommandGroupList);
    }
}
