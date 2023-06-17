/*
 * ScriptUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.util;

import com.github.toolarium.system.command.dto.SystemCommand;
import com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport;
import com.github.toolarium.system.command.util.OSUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Script util
 * 
 * @author patrick
 */
public final class ScriptUtil {
    /** The process lock filename */
    public static final String PROCESS_LOCK_FILENAME = ".lock";

    private static final Logger LOG = LoggerFactory.getLogger(ScriptUtil.class);
    
    
    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final ScriptUtil INSTANCE = new ScriptUtil();
    }

    
    /**
     * Constructor
     */
    private ScriptUtil() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static ScriptUtil getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Create a pid file
     * 
     * @param scriptPath the script path
     * @param filename the filename
     * @param pid the pid
     * @return the pidfile path
     */
    public Path createPidFile(Path scriptPath, String filename, Long pid) {
        if (pid == null) {
            return null;
        }
        
        try {
            Path pidFilePath = createTempFile(scriptPath, filename + ".pid").toPath();
            Files.writeString(pidFilePath, Long.toString(pid), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            return pidFilePath;
        } catch (IOException e) {
            LOG.debug("Could not create pid file: " + e.getMessage(), e);
            LOG.warn("Could not create pid file: " + e.getMessage());
            return null;
        }
    }


    /**
     * Read a pid file
     * 
     * @param file the file
     * @return the read and parsed pid
     */
    public Long readPidFile(Path file) {
        try {
            String content = Files.readString(file).trim();
            if (content == null || content.isBlank()) {
                return null;
            }
            return Long.valueOf(content);
        } catch (NumberFormatException e) {
            LOG.warn("The content of the file [" + file + "] can't be proper parsed: " + e.getMessage());
        } catch (Exception e) {
            LOG.warn("Can't access the file  [" + file + "]: " + e.getMessage());
        }
        
        return null;
    }

    
    /**
     * Create lock file
     *
     * @param basePath the base path
     * @param id the id
     * @return the lock file
     * @throws IOException In case of an I/O error
     */
    public Path createLockFile(Path basePath, String id) throws IOException {
        Path scriptPath = Paths.get(basePath + "/" + id);
        scriptPath.toFile().mkdirs();
        Path lockFile = Paths.get(scriptPath + "/" + ScriptUtil.PROCESS_LOCK_FILENAME);
        lockFile.toFile().createNewFile();
        return lockFile;
    }
    
    
    /**
     * Verify if the directory has no running processes
     * 
     * @param directory the directory
     * @return true if it has no running processes
     */
    public boolean hasNoRunningProcesses(Path directory) {
        File[] files = directory.toFile().listFiles((dir, name) -> name.endsWith(".pid"));
        if (files == null || files.length == 0) {
            return true;
        } 
        
        boolean hasNoRunningProcesses = true;
        for (File pidFile : files) {
            if (isRunningProcess(pidFile)) {
                hasNoRunningProcesses = false;
            }
        }
        
        return hasNoRunningProcesses;
    }


    /**
     * Verify if the process is running
     * 
     * @param pidFile the pid file
     * @return true if the pid file is valid and the process is still running 
     */
    public boolean isRunningProcess(File pidFile) {
        Long pid = readPidFile(pidFile.toPath());
        return (pid != null && ProcessBuilderUtil.getInstance().isProcessRunning(pid));
    }

    
    /**
     * Prepare the temp path and the script file
     * 
     * @param basePath the base path
     * @param filename the filanem
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @return the temp script file
     * @throws IOException In case of an I/O issue
     */
    public Path prepareTempPathAndScript(Path basePath, String filename, ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport) throws IOException {
        
        File tempFile = createTempFile(basePath, filename + systemCommandExecuterPlatformSupport.getScriptFileExtension());
        tempFile.setExecutable(true);
        
        // create the script file
        createScriptFile(systemCommandExecuterPlatformSupport, tempFile.toPath());
        return tempFile.toPath();
    }

    
    /**
     * Prepare the temp path and the script file
     * 
     * @param basePath the base path
     * @param filename the filename
     * @return the temp file
     * @throws IOException In case of an I/O issue
     */
    public File createTempFile(Path basePath, String filename) throws IOException {
        basePath.toFile().mkdirs();
        
        // create script file on temp folder
        File file = new File(basePath.toFile(), filename);
        file.createNewFile();

        return file;
    }


    /**
     * Create the script file
     * 
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @param file the file
     * @throws IOException In case of an I/O issue
     */
    public void createScriptFile(ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport, Path file)
            throws IOException {
        if (systemCommandExecuterPlatformSupport.getScriptFileHeader() != null && !systemCommandExecuterPlatformSupport.getScriptFileHeader().isBlank()) {
            systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getScriptFileHeader() + systemCommandExecuterPlatformSupport.getEndOfLine());
        }

        if (systemCommandExecuterPlatformSupport.getScriptFileComment() != null && !systemCommandExecuterPlatformSupport.getScriptFileComment().isBlank()) {
            final String comment = systemCommandExecuterPlatformSupport.getScriptFileComment() + SystemCommand.SPACE;
            final String line = prepareString(systemCommandExecuterPlatformSupport.getScriptFileComment(), 80);
            systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getEndOfLine()
                                                               + line + systemCommandExecuterPlatformSupport.getEndOfLine()
                                                               + comment + "Temporary script for batch execution." + systemCommandExecuterPlatformSupport.getEndOfLine()
                                                               + comment + "Powered by toolarium" + systemCommandExecuterPlatformSupport.getEndOfLine()
                                                               + line + systemCommandExecuterPlatformSupport.getEndOfLine());
        }
    }


    /**
     * Close the script file
     * 
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @param file the file
     * @throws IOException In case of an I/O issue
     */
    public void closeScriptFile(ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport, Path file)
            throws IOException {
        if (systemCommandExecuterPlatformSupport.getScriptFileFooter() != null && !systemCommandExecuterPlatformSupport.getScriptFileFooter().isBlank()) {
            systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getScriptFileFooter() + systemCommandExecuterPlatformSupport.getEndOfLine());
        }

        if (systemCommandExecuterPlatformSupport.getScriptFileComment() != null && !systemCommandExecuterPlatformSupport.getScriptFileComment().isBlank()) {
            final String comment = systemCommandExecuterPlatformSupport.getScriptFileComment() + SystemCommand.SPACE;
            final String line = prepareString(systemCommandExecuterPlatformSupport.getScriptFileComment(), 80);
            systemCommandExecuterPlatformSupport.writeToFile(file, systemCommandExecuterPlatformSupport.getEndOfLine() + line + systemCommandExecuterPlatformSupport.getEndOfLine()
                                                                    + comment + "EOF" + systemCommandExecuterPlatformSupport.getEndOfLine()
                                                                    + line + systemCommandExecuterPlatformSupport.getEndOfLine());
        }
    }

    
    /**
     * Prepare command lit
     *
     * @param list the list
     * @return the prepared command
     */
    public String prepareCommandList(List<String> list) {
        StringBuilder cmd = new StringBuilder();

        if (list != null) {
            for (String c : list) {
                if (!c.isEmpty()) {
                    if (cmd.length() > 0) {
                        cmd.append(SystemCommand.SPACE);
                    }
                    cmd.append(c);
                }
            }
        }
        
        return cmd.toString();
    }


    /**
     * Prepare string
     *
     * @param ch the fill string
     * @param length the max length
     * @return the prepared string
     */
    public String prepareString(String ch, int length) {
        if (ch == null || ch.length() >= length) {
            return "";
        }
        
        StringBuilder content = new StringBuilder();
        while (length > 0 && content.length() < length) {
            content.append(ch);
        }
        
        return content.toString().substring(0, length);
    }

    
    /**
     * Select invalid process directories 
     *
     * @param basePath the base path
     * @param newFolderThreshold the new folder threshold
     * @param lockFolderThreshold the lock folder threshold
     * @return the file list
     * @throws IOException In case of an I/O error
     */
    public List<Path> selectInvalidProcessDirectories(Path basePath, long newFolderThreshold, long lockFolderThreshold) throws IOException {
        List<Path> list = new ArrayList<>();
        
        Files.find(basePath, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isDirectory()).forEach(directory -> {
            if (!directory.toString().equals(basePath.toString()) // ignore base directory
                && hasReachedThresholdValue(OSUtil.getInstance().getCreationTimestamp(directory), newFolderThreshold)) {
                //if (LOG.isDebugEnabled()) {
                //    LOG.debug("Check directory [" + directory + "]...");
                //}
          
                final String parentName = directory.toString().replace("\\", "/");
                String id = prepareIdFromName(parentName);
                Path processLockFile = Paths.get(parentName, ScriptUtil.PROCESS_LOCK_FILENAME);
                if (processLockFile.toFile().exists()) {
                    if (hasReachedThresholdValue(OSUtil.getInstance().getCreationTimestamp(processLockFile), lockFolderThreshold) 
                        && ScriptUtil.getInstance().hasNoRunningProcesses(directory)) {
                        LOG.debug("Process ended by cleanup (id:" + id + ", script:" + directory + ", lock timeouted)");
                        list.add(directory);
                    } else {
                        //if (LOG.isDebugEnabled()) {
                        //    LOG.debug("Ignore folder (id:" + id + ")" + parentName);
                        //}
                    }
                } else if (ScriptUtil.getInstance().hasNoRunningProcesses(directory)) {
                    LOG.debug("Process ended by cleanup (id:" + id + ", script:" + directory + ")");
                    list.add(directory);
                }
            }
        });
        
        return list;
    }


    /**
     * Verify if the threshold is reached
     *
     * @param timestamp the timestamp
     * @param threshold the threshold
     * @return true if the threshold has reached
     */
    public boolean hasReachedThresholdValue(long timestamp, long threshold) {
        return (System.currentTimeMillis() - timestamp) >= threshold;
    }


    /**
     * Prepare id from name
     * 
     * @param directory the directory
     * @return the id
     */
    public String prepareIdFromName(final String directory) {
        final String parentName = directory.replace("\\", "/");

        String id = null;
        int idx = parentName.lastIndexOf('/');
        if (idx > 0 && idx < parentName.length()) {
            id = parentName.substring(idx + 1);
        }
        return id;
    }
}

