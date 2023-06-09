/*
 * ProcessStreamExceptionHandler.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.handler;

import com.github.toolarium.system.command.process.stream.IProcessStreamExceptionHandler;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


/**
 * Implements a stream support for the {@link IProcessStreamExceptionHandler}.
 * 
 * @author patrick
 */
public class ProcessStreamExceptionHandler implements IProcessStreamExceptionHandler, Closeable {
    private PrintStream printStream;

    
    /**
     * Constructor for ProcessStreamExceptionHandler
     *
     * @param outputStream the output stream to write
     */
    public ProcessStreamExceptionHandler(OutputStream outputStream) {
        this.printStream = new PrintStream(outputStream);
    }
    
    
    /**
     * @see com.github.toolarium.system.command.process.stream.IProcessStreamExceptionHandler#handle(java.io.IOException)
     */
    @Override
    public void handle(IOException ex) {
        if (printStream != null) {
            printStream.println(ex.getMessage() + ": ");
            ex.printStackTrace(printStream); // CHECKSTYLE IGNORE THIS LINE
            printStream.flush();
        }
    }

    
    /**
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        if (printStream != null) {
            printStream.flush();
            printStream.close();
            printStream = null;
        }
    }
}
