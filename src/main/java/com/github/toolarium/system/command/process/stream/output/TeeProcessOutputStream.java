/*
 * TeeProcessOutputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.output;

import com.github.toolarium.system.command.dto.group.ISystemCommandGroup;
import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.IProcessStreamExceptionHandler;
import java.io.IOException;


/**
 * Implements a tee {@link IProcessOutputStream} which splits the output named after the unix 'tee' command.
 * 
 * @author patrick
 */
public class TeeProcessOutputStream implements IProcessOutputStream {
    private IProcessOutputStream stream1;
    private IProcessOutputStream stream2;
    
    
    /**
     * Constructor for ProcessOutputStream
     * 
     * @param stream1 the first stream
     * @param stream2 the second stream
     */
    public TeeProcessOutputStream(IProcessOutputStream stream1, IProcessOutputStream stream2) {
        this.stream1 = stream1;
        this.stream2 = stream2;
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        if (stream1 != null) {
            stream1.write(b);
        }
        
        if (stream2 != null) {
            stream2.write(b);
        }
    }


    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) throws IOException {
        if (stream1 != null) {
            stream1.write(b);
        }
                
        if (stream2 != null) {
            stream2.write(b);
        }
    }


    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (stream1 != null) {
            stream1.write(b);
        }
        
        if (stream2 != null) {
            stream2.write(b);
        }
    }


    /**
     * @see java.io.Flushable#flush()
     */
    @Override
    public void flush() throws IOException {
        if (stream1 != null) {
            stream1.flush();
        }
        
        if (stream2 != null) {
            stream2.flush();
        }        
    }


    /**
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        if (stream1 != null) {
            stream1.close();
        }

        if (stream2 != null) {
            stream2.close();
        }
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#getLinePrefix()
     */
    @Override
    public byte[] getLinePrefix() {
        return stream1.getLinePrefix();
    }

    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#getProcessStreamExceptionHandler()
     */
    @Override
    public IProcessStreamExceptionHandler getProcessStreamExceptionHandler() {
        return stream1.getProcessStreamExceptionHandler();
    }
    

    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessOutputStream#start(com.github.toolarium.system.command.dto.group.ISystemCommandGroup)
     */
    @Override
    public void start(ISystemCommandGroup systemCommandGroup) {
        if (stream1 != null) {
            stream1.start(systemCommandGroup);
        }
        
        if (stream2 != null) {
            stream2.start(systemCommandGroup);
        }
    }

    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (stream1 != null) {
            builder.append(stream1.toString());
        }
        
        if (stream2 != null) {
            builder.append(stream2.toString());
        }
        
        return builder.toString();
    }
}
