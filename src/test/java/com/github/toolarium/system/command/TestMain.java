/*
 * TestMain.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Test class
 * 
 * @author patrick
 */
public final class TestMain {
    /** Main header */
    public static final String MAIN_HEADRER = "Main ";

    /** Parameters title */
    public static final String PARAMETERS_TITLE = "  - Parameters";

    /** Parameters prefix */
    public static final String PARAMETERS_PREFIX = "    - parameter[";
    
    /** Parameters appendix */
    public static final String PARAMETERS_APPENDIX = "]: ";

    /** STR OUT TEST message */
    public static final String STD_TEST = "Std test: ";
    
    /** STR ERROR TEST message */
    public static final String STD_ERR_TEST = "Std Err test";

    /** Environment KEY */
    public static final String ENV_KEY = "BASE";

    /** System property key*/
    public static final String SYSTEM_PROPERTY_KEY = "key";

    /** System property key*/
    public static final String SYSTEM_PROPERTY_PRINT_VERBOSE = "verbose";

    /** System property key*/
    public static final String SYSTEM_PROPERTY_EXIT_VALUE = "exit";

    /** System property read input */
    public static final String SYSTEM_PROPERTY_READINPUT = "readInput";


    /**
     * Constructor for TestMain
     */
    private TestMain() {
    }

    
    /**
     * Main class
     *
     * @param args program parameters
     */
    public static void main(String[] args) {
        if (isEnabled(SYSTEM_PROPERTY_PRINT_VERBOSE)) {
            printVerbose(args);
        }
        
        // echo input
        String readInput = System.getProperty(SYSTEM_PROPERTY_READINPUT);
        if (readInput != null && !readInput.isBlank()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String line = reader.readLine();
                if (line != null) {
                    print(line, false);
                }
            } catch (IOException e) {
                // NOP
            }
        }
            

        // exit value
        String exitValue = System.getProperty(SYSTEM_PROPERTY_EXIT_VALUE);
        if (exitValue != null && !exitValue.isBlank()) {
            System.exit(Integer.valueOf(exitValue)); // CHECKSTYLE IGNORE THIS LINE
        }
    }

    
    /**
     * Print verbose
     *
     * @param args the arguments
     */
    private static void printVerbose(String[] args) {
        print(MAIN_HEADRER + TestMain.class.getName(), false);
        
        if (args != null && args.length > 0) {
            print(PARAMETERS_TITLE, false);
            for (int i = 0; i < args.length; i++) {
                print(PARAMETERS_PREFIX + i + PARAMETERS_APPENDIX + args[i], false);
            }
        }
        
        print(STD_TEST + System.getenv().get(ENV_KEY) + "/" + System.getProperty(SYSTEM_PROPERTY_KEY) + " - [" + System.getProperty("user.name") + "]: " + System.getProperty("user.dir"), false);
        print(STD_ERR_TEST, true);
    }

    
    /**
     * Print a value to the console
     *
     * @param value the value
     * @param isError true if it should be printed on the std error
     */
    private static void print(String value, boolean isError) {
        if (isError) {
            System.err.println(value); // CHECKSTYLE IGNORE THIS LINE
        } else {
            System.out.println(value); // CHECKSTYLE IGNORE THIS LINE
        }
    }
    
    
    /**
     * Check if it is enables
     *
     * @param key the key
     * @return true if it is enabled
     */
    private static boolean isEnabled(String key) {
        String value = System.getProperty(key);
        return (value != null && Boolean.valueOf(value).booleanValue());
    }
}
