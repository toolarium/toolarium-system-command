/*
 * TeeOutputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.impl;

import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * OUtput stream which splits the output named after the unix 'tee' command. It allows a stream to be branched off
 * so there are now two streams.
 * 
 * @author patrick
 */
public class TeeOutputStream extends FilterOutputStream {
    private IProcessOutputStream branch;

    
    /**
     * Constructs a TeeOutputStream.
     *
     * @param out the main OutputStream
     * @param branch the second OutputStream
     */
    public TeeOutputStream(final OutputStream out, final IProcessOutputStream branch) {
        super(out);
        this.branch = branch;
    }

    
    /**
     * Constructs a TeeOutputStream.
     *
     * @param out the main OutputStream
     * @param branch the second OutputStream
     */
    public TeeOutputStream(final OutputStream out, final OutputStream branch) {
        super(out);
        
        if (branch != null) {
            this.branch = new ProcessOutputStream(branch);
        }
    }

    
    /**
     * Closes both output streams.
     * 
     * @see java.io.FilterOutputStream#close()
     */
    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            if (getBranchOutputStream() != null) {
                getBranchOutputStream().close();
            }
        }
    }

    
    /**
     * @see java.io.FilterOutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        try {
            super.flush();
        } finally {
            if (getBranchOutputStream() != null) {
                getBranchOutputStream().flush();
            }
        }
    }

    
    /**
     * @see java.io.FilterOutputStream#write(byte[])
     */
    @Override
    public synchronized void write(final byte[] b) throws IOException {
        super.write(b);
        
        if (getBranchOutputStream() != null) {
            this.getBranchOutputStream().write(b);
        }
    }

    
    /**
     * @see java.io.FilterOutputStream#write(byte[], int, int)
     */
    @Override
    public synchronized void write(final byte[] b, final int off, final int len) throws IOException {
        super.write(b, off, len);
        
        if (getBranchOutputStream() != null) {
            this.getBranchOutputStream().write(b, off, len);
        }
    }

    
    /**
     * @see java.io.FilterOutputStream#write(int)
     */
    @Override
    public synchronized void write(final int b) throws IOException {
        super.write(b);
        
        if (getBranchOutputStream() != null) {
            this.getBranchOutputStream().write(b);
        }
    }
    
    
    /**
     * Get the branch output stream
     *
     * @return the branch output stream
     */
    protected IProcessOutputStream getBranchOutputStream() {
        return branch;
    }
}
