/*
 * FolderCleanupService.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.folder;

import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import com.github.toolarium.system.command.process.util.ScriptUtil;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a folder cleanip servivce
 *  
 * @author patrick
 */
public class FolderCleanupService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(FolderCleanupService.class);
    private long newFolderThreshold;
    private long lockFolderThreshold;
    private Path basePath;
 
    
    /**
     * Constructor for FolderCleanupService
     *
     * @param basePath the base path
     * @param lockFolderThreshold the lock folder threshold
     */
    public FolderCleanupService(Path basePath, long lockFolderThreshold) {
        this.basePath = basePath;
        this.newFolderThreshold  = 10 * 60 * 1000; // 10 minutes
        this.lockFolderThreshold = lockFolderThreshold; // 1 * 60 * 60 * 1000; // one day
    }

    
    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            try {
                List<Path> directoriesToDelete = ScriptUtil.getInstance().selectInvalidProcessDirectories(basePath, newFolderThreshold, lockFolderThreshold);
                if (directoriesToDelete != null && !directoriesToDelete.isEmpty()) {
                    for (Path direcotry : directoriesToDelete) {
                        ProcessStreamUtil.getInstance().deleteDirectory(direcotry);
                    }
                }
            } catch (IOException e) {
                LOG.warn("Error occured: " + e.getMessage(), e);
            }
        } catch (RuntimeException e) {
            LOG.warn("Interupt process monitor thread: " + e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
