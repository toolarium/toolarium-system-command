/*
 * IProcessInputStream.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream;

import java.io.Closeable;
import java.io.IOException;


/**
 * The process input stream
 * 
 * @author patrick
 */
public interface IProcessInputStream extends Closeable {

    /**
     * Returns an estimate of the number of bytes that can be read (or skipped over) from this input stream without blocking, which may be 0, or 0 when
     * end of stream is detected.  The read might be on the same thread or another thread.  A single read or skip of this many bytes will not block,
     * but may read or skip fewer bytes.
     * 
     * @return an estimate of the number of bytes that can be read (or skipped over) from this input stream without blocking or
     *         {@code 0} when it reaches the end of the input stream.
     * @throws IOException if an I/O error occurs.
     */
    int available() throws IOException;

    
    /**
     * Reads the next byte of data from the input stream. The value byte is returned as an {@code int} in the range {@code 0} to
     * {@code 255}. If no byte is available because the end of the stream has been reached, the value {@code -1} is returned. This method
     * blocks until input data is available, the end of the stream is detected, or an exception is thrown.
     *
     * @return the next byte of data, or {@code -1} if the end of the stream is reached.    
     * @throws IOException If the first byte cannot be read for any reason other than end of file, or if the input stream has been closed,
     *         or if some other I/O error occurs.
     */
    int read() throws IOException;

    
    /**
     * Reads some number of bytes from the input stream and stores them into the buffer array {@code b}. The number of bytes actually read is
     * returned as an integer.  This method blocks until input data is available, end of file is detected, or an exception is thrown.
     * 
     * @param buffer the buffer
     * @return the total number of bytes read into the buffer, or {@code -1} if there is no more data because the end of the stream has been reached.
     * @throws IOException If the first byte cannot be read for any reason other than end of file, or if the input stream has been closed,
     *         or if some other I/O error occurs.
     */
    int read(byte[] buffer)  throws IOException;

    
    /**
     * Reads up to {@code len} bytes of data from the input stream into an array of bytes. An attempt is made to read as many as
     * {@code len} bytes, but a smaller number may be read. The number of bytes actually read is returned as an integer.
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset in array {@code b} at which the data is written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or {@code -1} if there is no more data because the end of the stream has been reached.
     * @throws IOException If the first byte cannot be read for any reason other than end of file, or if the input stream has been closed,
     *         or if some other I/O error occurs.
     */
    int read(byte[] b, int off, int len) throws IOException;
}
