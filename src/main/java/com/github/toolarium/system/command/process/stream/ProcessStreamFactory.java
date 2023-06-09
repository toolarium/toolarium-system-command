/*
 * ProcessStreamFactory.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream;

import com.github.toolarium.system.command.process.stream.handler.ProcessStreamExceptionHandler;
import com.github.toolarium.system.command.process.stream.handler.Slf4jProcessStreamExceptionHandler;
import com.github.toolarium.system.command.process.stream.input.ProcessBufferInputStream;
import com.github.toolarium.system.command.process.stream.input.ProcessDiscardInputStream;
import com.github.toolarium.system.command.process.stream.input.ProcessFileInputStream;
import com.github.toolarium.system.command.process.stream.input.ProcessStandardInInputStream;
import com.github.toolarium.system.command.process.stream.output.ProcessBufferOutputStream;
import com.github.toolarium.system.command.process.stream.output.ProcessOutputStream;
import com.github.toolarium.system.command.process.stream.output.Slf4jProcessOutputStream;
import java.io.File;
import java.io.OutputStream;


/**
 * The process stream factory
 * 
 * @author patrick
 */
public final class ProcessStreamFactory {

    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final ProcessStreamFactory INSTANCE = new ProcessStreamFactory();
    }

    
    /**
     * Constructor
     */
    private ProcessStreamFactory() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static ProcessStreamFactory getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Get the standard out as process output stream
     *
     * @return the process output stream
     */
    public IProcessOutputStream getStandardOut() {
        return new ProcessOutputStream(System.out);
    }

    
    /**
     * Get the standard out as process output stream
     *
     * @param linePrefix the line prefix
     * @return the process output stream
     */
    public IProcessOutputStream getStandardOut(String linePrefix) {
        return getProcessOutputStream(System.out, linePrefix, null);
    }

    
    /**
     * Get the standard out as process output stream
     *
     * @param linePrefix the line prefix
     * @param processStreamExceptionHandler the process stream exception handler or null
     * @return the process output stream
     */
    public IProcessOutputStream getStandardOut(String linePrefix, IProcessStreamExceptionHandler processStreamExceptionHandler) {
        return getProcessOutputStream(System.out, linePrefix, processStreamExceptionHandler);
    }

    
    /**
     * Get the standard out as process error stream
     *
     * @return the process output stream
     */
    public IProcessOutputStream getStandardErr() {
        return new ProcessOutputStream(System.err);
    }

    
    /**
     * Get the standard out as process error stream
     *
     * @param linePrefix the line prefix
     * @return the process output stream
     */
    public IProcessOutputStream getStandardErr(String linePrefix) {
        return getProcessOutputStream(System.err, linePrefix, null);
    }

    
    /**
     * Get the standard out as process error stream
     *
     * @param linePrefix the line prefix
     * @param processStreamExceptionHandler the process stream exception handler or null
     * @return the process output stream
     */
    public IProcessOutputStream getStandardErr(String linePrefix, IProcessStreamExceptionHandler processStreamExceptionHandler) {
        return getProcessOutputStream(System.err, linePrefix, processStreamExceptionHandler);
    }

    
    /**
     * Get a process output stream which is wrapped by a {@link OutputStream}.
     *
     * @param os the output stream to pass as process output stream
     * @return the process output stream
     */
    public IProcessOutputStream getProcessOutputStream(OutputStream os) {
        return new ProcessOutputStream(os);
    }

    
    /**
     * Get a process output stream which is wrapped by a {@link OutputStream}.
     *
     * @param os the output stream to pass as process output stream
     * @param linePrefix the line prefix
     * @return the process output stream
     */
    public IProcessOutputStream getProcessOutputStream(OutputStream os, String linePrefix) {
        return new ProcessOutputStream(os, linePrefix, null);
    }

    
    /**
     * Get a process output stream which is wrapped by a {@link OutputStream}.
     *
     * @param os the output stream to pass as process output stream
     * @param linePrefix the line prefix
     * @param processStreamExceptionHandler the process stream exception handler or null
     * @return the process output stream
     */
    public IProcessOutputStream getProcessOutputStream(OutputStream os, String linePrefix, IProcessStreamExceptionHandler processStreamExceptionHandler) {
        return new ProcessOutputStream(os, linePrefix, processStreamExceptionHandler);
    }


    /**
     * Get a process output stream buffer
     *
     * @return a process output stream buffer
     */
    public ProcessBufferOutputStream getProcessBufferOutputStream() {
        return new ProcessBufferOutputStream();
    }

    
    /**
     * Get a process output stream buffer
     *
     * @param linePrefix the line prefix
     * @return a process output stream buffer
     */
    public ProcessBufferOutputStream getProcessBufferOutputStream(String linePrefix) {
        return new ProcessBufferOutputStream(linePrefix);
    }


    /**
     * Get a process output stream buffer
     *
     * @param linePrefix the line prefix
     * @param processStreamExceptionHandler the process stream exception handler or null
     * @return a process output stream buffer
     */
    public ProcessBufferOutputStream getProcessBufferOutputStream(String linePrefix, IProcessStreamExceptionHandler processStreamExceptionHandler) {
        return new ProcessBufferOutputStream(linePrefix, processStreamExceptionHandler);
    }

    
    /**
     * Get a process output stream buffer
     *
     * @return a process output stream buffer
     */
    public IProcessOutputStream getSlf4jProcessOutputStream() {
        return new Slf4jProcessOutputStream();
    }


    /**
     * Get the standard in as process input stream
     *
     * @return the process input stream
     */
    public IProcessInputStream getStandardIn() {
        return new ProcessStandardInInputStream();
    }

    
    /**
     * Get the standard in as process input stream
     *
     * @return the process input stream
     */
    public IProcessInputStream getEmptyStandardIn() {
        return new ProcessDiscardInputStream();
    }

    
    /**
     * Get the standard in read from the buffer as process input stream
     *
     * @param buffer the buffer
     * @return the process input stream
     */
    public IProcessInputStream getStandardInFromBuffer(String buffer) {
        return new ProcessBufferInputStream(buffer);
    }

    
    /**
     * Get the standard in as process input stream
     *
     * @param file the file as input
     * @return the process input stream
     */
    public IProcessInputStream getStandardInFromFile(File file) {
        return new ProcessFileInputStream(file);
    }


    /**
     * Get the standard in as process input stream
     *
     * @return the process input stream
     */
    public IProcessStreamExceptionHandler getStandardOutProcessStreamExceptionHandler() {
        return new ProcessStreamExceptionHandler(System.out);
    }


    /**
     * Get the standard in as process input stream
     *
     * @return the process input stream
     */
    public IProcessStreamExceptionHandler getStandardErrProcessStreamExceptionHandler() {
        return new ProcessStreamExceptionHandler(System.err);
    }


    /**
     * Get the standard in as process input stream
     *
     * @return the process input stream
     */
    public IProcessStreamExceptionHandler getSlf4jProcessStreamExceptionHandler() {
        return new Slf4jProcessStreamExceptionHandler();
    }
}
