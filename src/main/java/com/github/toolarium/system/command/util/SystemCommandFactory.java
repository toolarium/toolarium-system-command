/*
 * SystemCommandFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.util;

import com.github.toolarium.system.command.dto.SystemCommand;
import java.util.Map;
import java.util.Set;

/**
 * Defines the system command factory
 *
 * @author patrick
 */
public final class SystemCommandFactory {
    private static final String ENSAPSULATE_CHAR = "\"";


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

    
    /**
     * Add system command parameters
     *
     * @param systemCommand the system command
     * @param map the parameter list
     * @param encapsulateValue true to encapsulate values
     * @param encapsulateExpression true to encapsulate the full expression
     */
    public void addSystemCommandParameters(SystemCommand systemCommand, final Map<String, String> map, final boolean encapsulateValue, final boolean encapsulateExpression) {
        addSystemCommandParameters(systemCommand, map, null, encapsulateValue, encapsulateExpression, null);
    }

    
    /**
     * Add system command parameters
     *
     * @param systemCommand the system command
     * @param map the parameter list
     * @param keyPrefix the key prefix or null
     * @param encapsulateValue true to encapsulate values
     * @param encapsulateExpression true to encapsulate the full expression
     */
    public void addSystemCommandParameters(SystemCommand systemCommand, final Map<String, String> map, final String keyPrefix, final boolean encapsulateValue, final boolean encapsulateExpression) {
        addSystemCommandParameters(systemCommand, map, keyPrefix, encapsulateValue, encapsulateExpression, null);
    }
    
    
    /**
     * Add system command parameters
     *
     * @param systemCommand the system command
     * @param map the parameter list
     * @param keyPrefix the key prefix or null
     * @param encapsulateValue true to encapsulate values
     * @param encapsulateExpression true to encapsulate the full expression
     * @param senstivieSettings the sensitive settings
     */
    public void addSystemCommandParameters(SystemCommand systemCommand, final Map<String, String> map, final String keyPrefix, final boolean encapsulateValue, final boolean encapsulateExpression, final Set<String> senstivieSettings) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            StringBuilder cmdBuilder = new StringBuilder();
            StringBuilder displayBuilder = null;
            if (encapsulateExpression) {
                cmdBuilder.append(ENSAPSULATE_CHAR);
            }
            
            if (keyPrefix != null && !keyPrefix.isBlank()) {
                cmdBuilder.append(keyPrefix);
            }
            
            cmdBuilder.append(e.getKey());
            
            if (e.getValue() != null && !e.getValue().isBlank()) {
                cmdBuilder.append("=");

                displayBuilder = new StringBuilder(cmdBuilder);
                if (encapsulateValue) {
                    cmdBuilder.append(ENSAPSULATE_CHAR);
                }
                cmdBuilder.append(e.getValue());
                if (encapsulateValue) {
                    cmdBuilder.append(ENSAPSULATE_CHAR);
                }
                
                if (senstivieSettings != null && senstivieSettings.contains(e.getKey())) {
                    displayBuilder.append("...");
                } else {
                    displayBuilder = new StringBuilder(cmdBuilder);
                }
            } else {
                displayBuilder = new StringBuilder(cmdBuilder);
            }
            
            if (encapsulateExpression) {
                cmdBuilder.append(ENSAPSULATE_CHAR);
                displayBuilder.append(ENSAPSULATE_CHAR);
            }
            
            if (!cmdBuilder.toString().isEmpty() && !displayBuilder.toString().isEmpty()) {
                systemCommand.add(cmdBuilder.toString(), displayBuilder.toString());                
            }
        }
    }
}