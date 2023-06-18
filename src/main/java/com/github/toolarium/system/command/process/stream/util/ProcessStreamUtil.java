/*
 * ProcessStreamUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.stream.util;

import com.github.toolarium.system.command.process.stream.IProcessOutputStream;
import com.github.toolarium.system.command.process.stream.IProcessStreamExceptionHandler;
import com.github.toolarium.system.command.process.stream.output.ProcessOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The process stream util class
 *
 * @author patrick
 */
public final class ProcessStreamUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessStreamUtil.class);
    
    
    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final ProcessStreamUtil INSTANCE = new ProcessStreamUtil();
    }


    /**
     * Constructor
     */
    private ProcessStreamUtil() {
        // NOP
    }


    /**
     * Get the instance
     *
     * @return the instance
     */
    public static ProcessStreamUtil getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Create a unique id
     *
     * @return the id
     */
    public String getId() {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
               + String.format("%02x", ThreadLocalRandom.current().nextInt(16)).toUpperCase();
    }
    
    
    /**
     * Pipe the available bytes from the source stream to the target. In case a prefix is defined it will be inserted after a newline.
     *
     * @param source the input stream
     * @param target the output stream
     * @param linePrefix the prefix to add after every new line or null
     * @param failureHandler the failure handler
     * @return the number of piped data; -1 in case of an error
     */
    public int pipeAvailableBytes(InputStream source, OutputStream target, final String linePrefix, IProcessStreamExceptionHandler failureHandler) {
        byte[] prefix = null;
        if (linePrefix != null && !linePrefix.isEmpty()) {
            prefix = linePrefix.getBytes();
        }
        
        return pipeAvailableBytes(source, new ProcessOutputStream(target, prefix, failureHandler));
    }

    
    /**
     * Pipe the available bytes from the source stream to the target. In case a prefix is defined it will be inserted after a newline.
     *
     * @param source the input stream
     * @param target the output stream
     * @param prefix the prefix to add after every new line or null
     * @param failureHandler the failure handler
     * @return the number of piped data; -1 in case of an error
     */
    public int pipeAvailableBytes(InputStream source, OutputStream target, final byte[] prefix, IProcessStreamExceptionHandler failureHandler) {
        return pipeAvailableBytes(source, new ProcessOutputStream(target, String.valueOf(prefix), failureHandler));
    }

    
    /**
     * Pipe the available bytes from the source stream to the target. In case a prefix is defined it will be inserted after a newline.
     *
     * @param source the input stream
     * @param target the output stream
     * @return the number of piped data; -1 in case of an error
     */
    public int pipeAvailableBytes(InputStream source, IProcessOutputStream target) {
        try {
            if (source == null) {
                return -1;
            }
            
            byte[] buffer = new byte[Math.max(10, source.available())];
            int length = source.read(buffer);
            
            if (target != null) {
                target.write(insertPrefix(buffer, length, target.getLinePrefix()));
                target.flush();
                return length;
            }
        } catch (IOException e) {
            if (target.getProcessStreamExceptionHandler() != null) {
                target.getProcessStreamExceptionHandler().handle(e);
            }
            
            LOG.debug("ERROR: " + e.getMessage(), e);                
            return -1;
        }
        
        return 0;
    }

    
    /**
     * Insert a prefix by a newline
     *
     * @param data the data
     * @param length the length of the data
     * @param prefix the preifx to insert
     * @return the prepared data
     */
    public byte[] insertPrefix(byte[] data, int length, byte[] prefix) {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        int start = 0;
        
        if (prefix != null && prefix.length > 0) {
            for (int i = 1; i <= length; i++) {
                if ('\n' == (char) data[i - 1]) {
                    target.write(data, start, i - start - 1);
                    target.write('\n');
                    try {
                        target.write(prefix);
                    } catch (IOException e) {
                        // NOP
                    }
                    start = i;
                }
            }
            
        }
        
        if ((length - start) > 0) {
            target.write(data, start, length - start);
        }
        
        return target.toByteArray();
    }
    
    
    /**
     * Remove CR
     *
     * @param input the input
     * @return the result
     */
    public String removeCR(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replace("\r", "");
    }

    
    /**
     * Convert the InputStream to String we use the Reader.read(char[] buffer) method. We iterate until the Reader return -1 which means
     * there's no more data to read.
     *
     * @param is the input stream
     * @return the result
     * @throws IOException in case of error
     */
    public ByteArrayOutputStream convertStreamTo(InputStream is) throws IOException {
        if (is == null) {
            return new ByteArrayOutputStream();
        }

        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        is.transferTo(dest);
        return dest;
    }


    /**
     * Convert the InputStream to String we use the Reader.read(char[] buffer) method. We iterate until the Reader return -1 which means
     * there's no more data to read.
     *
     * @param is the input stream
     * @return the result
     * @throws IOException in case of error
     */
    public String convertStreamToStr(InputStream is) throws IOException {
        if (is == null) {
            return "";
        }

        ByteArrayOutputStream dest = convertStreamTo(is);
        is.transferTo(dest);
        return dest.toString();
    }

    
    /**
     * Convert the InputStream to String we use the Reader.read(char[] buffer) method. We iterate until the Reader return -1 which means
     * there's no more data to read.
     *
     * @param is the input stream
     * @param charsetName the charset name
     * @return the result
     * @throws IOException in case of error
     */
    public String convertStreamToStr(InputStream is, String charsetName) throws IOException {
        if (is == null) {
            return "";
        }

        ByteArrayOutputStream dest = convertStreamTo(is);
        is.transferTo(dest);
        return dest.toString(charsetName);
    }

    
    /**
     * Convert the InputStream to String we use the Reader.read(char[] buffer) method. We iterate until the Reader return -1 which means
     * there's no more data to read.
     *
     * @param is the input stream
     * @return the result
     * @throws IOException in case of error
     */
    public InputStream convertStreamToNewInputStream(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }

        ByteArrayOutputStream dest = convertStreamTo(is);
        is.transferTo(dest);
        return new ByteArrayInputStream(dest.toByteArray());
    }

    
    /**
     * Close silent a stream
     *
     * @param closeable close
     */
    public void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // NOP
            }
        }
    }
    
    
    /**
     * Delete a directory
     * 
     * @param pathToBeDeleted the directory
     */
    public void deleteDirectory(Path pathToBeDeleted) {
        if (pathToBeDeleted == null || !pathToBeDeleted.toFile().exists()) {
            return;
        }
        
        try {
            LOG.debug("Delete path [" + pathToBeDeleted + "] ...");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
            LOG.info("Directory [" + pathToBeDeleted + "] can't be deleted: " + e.getMessage(), e);
        }
    }
}