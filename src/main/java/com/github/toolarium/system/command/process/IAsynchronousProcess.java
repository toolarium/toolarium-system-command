/*
 * IAsynchronousProcess.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process;

import java.io.OutputStream;
import java.util.concurrent.TimeUnit;


/**
 * Defines an asynchronous process
 * 
 * @author patrick
 */
public interface IAsynchronousProcess extends IProcess {
    
    /**
     * Wait, if necessary, until the process is terminated. This method returns immediately if the 
     * process has already terminated. If the process has not yet terminated, the calling thread 
     * will be blocked until the process exits.
     *
     * @return the exit value, by convention, the value {@code 0} indicates normal termination
     * @throws InterruptedException if the current thread is
     *         {@linkplain Thread#interrupt() interrupted} by another
     *         thread while it is waiting, then the wait is ended and
     *         an {@link InterruptedException} is thrown.
     */
    int waitFor() throws InterruptedException;

    
    /**
     * Wait, if necessary, until the process is terminated. This method returns immediately if the 
     * process has already terminated or the specified waiting time elapses.
     *
     * <p>The default implementation of this methods polls the {@code exitValue}
     * to check if the process has terminated. Concrete implementations of this
     * class are strongly encouraged to override this method with a more
     * efficient implementation.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the {@code timeout} argument
     * @return true if the process has exited; othweise false if the waiting time elapsed before the process has exited
     * @throws InterruptedException if the current thread is interrupted while waiting.
     */
    boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException;

    
    /**
     * Check whether the process is alive.
     *
     * @return true if it is alive; othweise false
     */
    boolean isAlive();


    /**
     * Try to kill the process.
     */
    void tryDestroy();

    
    /**
     * Destroy the process immediate. If the process is not alive, no action is taken.
     */
    void destroy();

    
    /**
     * Returns the output stream connected to the process input stream. 
     * If the standard input of the process has been redirected, the stream is not available.
     *
     * @return the input stream
     */
    OutputStream getInputStream();
    
       
    /**
     * Get the process handle.
     *
     * @return the process handle
     */
    ProcessHandle getProcessHandle();
    
    
    /**
     * Close and cleanup the process. If the process is not finalised the close wait until the process is finished.
     */
    void close();
}
