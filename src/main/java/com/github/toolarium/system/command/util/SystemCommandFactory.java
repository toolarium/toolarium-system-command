/*
 * SystemCommandFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.util;


/**
 * Defines the system command factory
 *
 * @author patrick
 */
public final class SystemCommandFactory {
    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final SystemCommandFactory INSTANCE = new SystemCommandFactory();
    }


    /**
     * Constructor
     */
    private SystemCommandFactory() {
        // NOP
    }


    /**
     * Get the instance
     *
     * @return the instance
     */
    public static SystemCommandFactory getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Create a sleep command
     *
     * @param numberOfSeconds the number of seconds
     * @return the sleep command
     */
    public String createSleepCommand(int numberOfSeconds) {
        String sleepCmd = "sleep " + numberOfSeconds + " > /dev/null 2>&1";
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            sleepCmd = "ping 127.0.0.1 -n " + numberOfSeconds + " > nul";
        }
        
        return sleepCmd;
    }
}