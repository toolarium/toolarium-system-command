/*
 * FolderCleanupService.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.folder;

import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import com.github.toolarium.system.command.process.util.ProcessBuilderUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a folder cleanip servivce
 *  
 * @author patrick
 */
public class FolderCleanupService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(FolderCleanupService.class);
    private Path basePath;
 
    
    /**
     * Constructor for FolderCleanupService
     *
     * @param basePath the base path
     */
    public FolderCleanupService(Path basePath) {
        this.basePath = basePath;
    }

    
    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            try {
                Files.find(basePath, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile()).forEach(file -> {
                    if (file.toFile().getName().endsWith(".pid")) {
                        Long pid = readPidFile(file);
                        if (!ProcessBuilderUtil.getInstance().isProcessRunning(pid)) {
                            final String name = file.toFile().getName();
                            String commandId = null;
                            int idx = name.lastIndexOf('.');
                            if (idx > 0) {
                                commandId = name.substring(0, idx);
                            }
                            
                            LOG.info("Process ended (id:" + commandId + ", pid:" + pid + ")");

                            ProcessStreamUtil.getInstance().deleteDirectory(file.getParent());
                        }
                    }
                });
            } catch (IOException e) {
                LOG.warn("Error occured: " + e.getMessage(), e);
            }
    
            
        } catch (RuntimeException e) {
            LOG.info("Interupt process monitor thread...");
            Thread.currentThread().interrupt();
        }
    }


    /**
     * Read a pid file
     * 
     * @param file the file
     * @return the read and parsed pid
     */
    public static Long readPidFile(Path file) {
        try {
            return Long.valueOf(Files.readString(file).trim());
        } catch (NumberFormatException e) {
            LOG.warn("The content of the file [" + file + "] can't be proper parsed: " + e.getMessage(), e);
        } catch (IOException e) {
            LOG.warn("Can't access the file  [" + file + "]: " + e.getMessage(), e);
        }
        
        return null;
    }

}
