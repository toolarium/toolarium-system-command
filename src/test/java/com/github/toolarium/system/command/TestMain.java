/*
 * TestMain.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;


/**
 * Test class
 * 
 * @author patrick
 */
public final class TestMain {
    
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
        print("Main " + TestMain.class.getName(), false);
        
        if (args != null || args.length > 0) {
            print("  - Parameters", false);
            for (int i = 0; i < args.length; i++) {
                print("    - parameter[" + i + "]: " + args[i], false);
            }
        }
        
        print("Std test", false);
        print("Std Err test", true);
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
}
