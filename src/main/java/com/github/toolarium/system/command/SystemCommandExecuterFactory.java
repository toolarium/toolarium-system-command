/*
 * SystemCommandExecuterFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import com.github.toolarium.system.command.impl.LinuxSystemCommandExecuterImpl;
import com.github.toolarium.system.command.impl.UnixSystemCommandExecuterImpl;
import com.github.toolarium.system.command.impl.WindowsSystemCommandExecuterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The system command excuter factory
 *
 * @author patrick
 */
public final class SystemCommandExecuterFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SystemCommandExecuterFactory.class);


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
        // NOP
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
     * Create a system command executer
     *
     * @param processEnvironment the process environment
     * @param systemCommand the system command
     * @return the system command executer
     */
    public ISystemCommandExecuter createSystemCommandExecuter(IProcessEnvironment processEnvironment, ISystemCommand systemCommand) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            
            LOG.debug("Choose " + WindowsSystemCommandExecuterImpl.class.getName() + " as executer.");
            return new WindowsSystemCommandExecuterImpl(processEnvironment, systemCommand);
        } else if (osName.startsWith("linux")) {
            
            LOG.debug("Choose " + LinuxSystemCommandExecuterImpl.class.getName() + " as executer.");
            return new LinuxSystemCommandExecuterImpl(processEnvironment, systemCommand);
        }

        LOG.debug("Choose " + UnixSystemCommandExecuterImpl.class.getName() + " as executer.");
        return new UnixSystemCommandExecuterImpl(processEnvironment, systemCommand);
    }
}
