/*
 * IProcessInputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream;

import com.github.toolarium.system.command.process.stream.input.ProcessInputStreamSource;

/**
 * The process input stream
 * 
 * @author patrick
 */
public interface IProcessInputStream {

    /**
     * The process input stream source
     *
     * @return zhe process input stream source
     */
    ProcessInputStreamSource getProcessInputStreamSource();
}
