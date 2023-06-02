/*
 * ScriptUtil.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.process.util;

import com.github.toolarium.system.command.dto.ISystemCommandGroup;
import com.github.toolarium.system.command.dto.SystemCommand;
import com.github.toolarium.system.command.executer.ISystemCommandExecuterPlatformSupport;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Script util
 * 
 * @author patrick
 */
public final class ScriptUtil {
    
    /** Defines the temp sub folder */
    public static final String TOOLARIUM_SYSTEM_COMMAND_TEMP_SUBFOLDER = "toolarium-system-command";

    /** TEMP Environment variable */
    public static final String TEMPs = "TOOLARIUM_TEMP";
    
    private static final Logger LOG = LoggerFactory.getLogger(ScriptUtil.class);
    private Path baseTempPath;

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
     * Get the base temp path
     *
     * @return the base temp path
     */
    public Path getBaseTempPath() {
        if (baseTempPath == null) {
            baseTempPath = Path.of(System.getProperty("java.io.tmpdir").trim() + "/" + TOOLARIUM_SYSTEM_COMMAND_TEMP_SUBFOLDER);
            if (!baseTempPath.toFile().exists()) {
                try {
                    LOG.warn("Create temp path [" + baseTempPath + "].");
                    Files.createDirectories(getBaseTempPath());
                } catch (IOException e) {
                    LOG.warn("Could not create temp path [" + baseTempPath + "]: " + e.getMessage(), e);
                }
            }
        }
        
        return baseTempPath;
    }

    
    /**
     * Prepare the temp path and the script file
     * 
     * @param systemCommandGroup the system command group
     * @return the temp path
     */
    public Path prepareTempPath(ISystemCommandGroup systemCommandGroup) {
        return  Paths.get(ScriptUtil.getInstance().getBaseTempPath() + "/" + systemCommandGroup.getId());
    }

    
    /**
     * Prepare the temp path and the script file
     * 
     * @param systemCommandGroup the system command group
     * @param systemCommandExecuterPlatformSupport the system command executer platform support
     * @return the temp script file
     * @throws IOException In case of an I/O issue
     */
    public Path prepareTempPathAndScript(ISystemCommandGroup systemCommandGroup, ISystemCommandExecuterPlatformSupport systemCommandExecuterPlatformSupport) throws IOException {
        
        File tempFile = createTempFile(systemCommandGroup, "run" + systemCommandExecuterPlatformSupport.getScriptFileExtension());
        tempFile.setExecutable(true);
        
        // create the script file
        createScriptFile(systemCommandExecuterPlatformSupport, tempFile.toPath());
        return tempFile.toPath();
    }

    
    /**
     * Prepare the temp path and the script file
     * 
     * @param systemCommandGroup the system command group
     * @param filename the filename
     * @return the temp file
     * @throws IOException In case of an I/O issue
     */
    public File createTempFile(ISystemCommandGroup systemCommandGroup, String filename) throws IOException {
        Path tempPath = prepareTempPath(systemCommandGroup);
        tempPath.toFile().mkdirs();
        
        // create script file on temp folder
        File file = new File(tempPath.toFile(), filename);
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
}
