/*
 * ISystemCommandExecuterPlatformSupport.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


/**
 * Defines the system command executer platform support.
 *  
 * @author patrick
 */
public interface ISystemCommandExecuterPlatformSupport {
    
    
    /**
     * Get the start shell command
     * 
     * @param systemCommand the system command
     * @return the shell command
     */
    List<String> getShellCommand(ISystemCommand systemCommand);

    
    /**
     * Get the script file extension
     *
     * @return the script file extension
     */
    String getScriptFileExtension();
    
    
    /**
     * Get the script file header
     *
     * @return the script file header
     */
    String getScriptFileHeader();

    
    /**
     * Get the script file footer
     *
     * @return the script file footer
     */
    String getScriptFileFooter();

    
    /**
     * Get the script file comment
     *
     * @return the script file comment
     */
    String getScriptFileComment();

    
    /**
     * Get the environment set command
     *
     * @return the environment set command
     */
    String getEnvironmentSetCommand();


    /**
     * Get the environment unset command
     *
     * @return the environment unset command
     */
    String getEnvironmentUnsetCommand();

    
    /**
     * Get the environment change directory command
     *
     * @return the environment change directory command
     */
    String getEnvironmentChangeDirectoryCommand();

    
    /**
     * Get the environment assign command
     *
     * @return the environment assign command
     */
    String getEnvironmentAssignCommand();

    
    /**
     * Get the environment assign command end
     * 
     * @return the assign command
     */
    String getEnvironmentAssignCommandEnd();


    /**
     * Get the end of line
     *
     * @return the end of line
     */
    String getEndOfLine();
    
    
    /**
     * Get the substitute user command
     *
     * @param username the username to use
     * @return the substitute user command 
     */
    List<String> getSudo(String username);
    
    
    /**
     * Write content to the temporary file
     *
     * @param file the current temporary file
     * @param content the content to write
     * @throws IOException In case of write issues
     */
    void writeToFile(Path file, String content) throws IOException;
}
