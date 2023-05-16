/*
 * ProcessInputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.impl;

import com.github.toolarium.system.command.process.stream.IProcessInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Implements the {@link IProcessInputStream}.
 *  
 * @author patrick
 */
public class ProcessInputStream extends AbstractProcessStream implements IProcessInputStream {
    private InputStream is;

    
    /**
     * Constructor for ProcessInputStream
     */
    public ProcessInputStream() {
        this.is = System.in;
    }

    
    /**
     * Constructor for ProcessInputStream
     *
     * @param is the input stream
     */
    public ProcessInputStream(InputStream is) {
        this.is = is;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessInputStream#available()
     */
    @Override
    public int available() throws IOException {
        return is.available();
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessInputStream#read()
     */
    @Override
    public int read() throws IOException {
        return is.read();
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessInputStream#read(byte[])
     */
    @Override
    public int read(byte[] buffer) throws IOException {
        return is.read(buffer);
    }


    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessInputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }


    /**
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        is.close();
    }
}
