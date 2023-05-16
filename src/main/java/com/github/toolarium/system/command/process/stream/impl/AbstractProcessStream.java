/*
 * AbstractProcessStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.impl;

import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;


/**
 * Abstract process stream
 *  
 * @author patrick
 */
public class AbstractProcessStream {
    private final String id;
    
    
    /**
     * Constructor for AbstractProcessStream
     */
    public AbstractProcessStream() {
        this.id = ProcessStreamUtil.getInstance().getId();
    }

    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return id;
    }
}
