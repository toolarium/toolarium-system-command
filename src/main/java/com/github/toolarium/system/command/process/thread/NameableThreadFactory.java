/*
 * NameableThreadFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.thread;

import java.util.concurrent.ThreadFactory;


/**
 * Implements a thread factory
 *  
 * @author patrick
 */
public class NameableThreadFactory implements ThreadFactory {
    private int threadsNum;
    private final String namePattern;

    
    /**
     * Constructor for NameableThreadFactory
     *
     * @param baseName the base name
     */
    public NameableThreadFactory(String baseName) {
        namePattern = baseName + "-%d";
    }

    
    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(Runnable runnable) {
        threadsNum++;
        return new Thread(runnable, String.format(namePattern, threadsNum));
    }    
}