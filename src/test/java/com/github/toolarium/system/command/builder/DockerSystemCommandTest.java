/*
 * DockerSystemCommandTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.github.toolarium.system.command.AbstractProcessTest;
import com.github.toolarium.system.command.SystemCommandExecuterFactory;
import com.github.toolarium.system.command.process.IAsynchronousProcess;
import com.github.toolarium.system.command.process.ISynchronousProcess;
import com.github.toolarium.system.command.process.stream.ProcessInputStreamSource;
import com.github.toolarium.system.command.process.stream.output.ProcessBufferOutputStream;
import com.github.toolarium.system.command.process.stream.util.ProcessStreamUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;


/**
 * Docker system command test
 * 
 * @author patrick
 */
public class DockerSystemCommandTest extends AbstractProcessTest {
    private static final String TOOLARIUM_TOOLARIUM_ICAP_CALMAV_DOCKER = "toolarium/toolarium-icap-calmav-docker:0.0.1";

    /**
     * Shows the usage of the start and stop of a docker container
     * 
     * @throws InterruptedException in case of a thread interruption
     */
    @Test
    public void usageDockerStartStop() throws InterruptedException {
        // docker run --rm --name icap-server -p 1344:1344 toolarium/toolarium-icap-calmav-docker:0.0.1
        ProcessBufferOutputStream output = new ProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
        final IAsynchronousProcess startDockerProcess = SystemCommandExecuterFactory.builder().docker()
                .run(TOOLARIUM_TOOLARIUM_ICAP_CALMAV_DOCKER)
                .remove(true)
                .name("icap-server")
                .port(1344)
            .build()
            .runAsynchronous(ProcessInputStreamSource.INHERIT, output, errOutput);

        // wait until it has started
        long maxTimeout = System.currentTimeMillis() + (10 * 1000);
        String startMessage = "INFO: Starting up C-ICAP service.";
        while (!output.startsWith(startMessage)) {
            Thread.sleep(1000);
            if (maxTimeout <= System.currentTimeMillis()) {
                fail();
            }
        }
        
        // stop: docker stop icap-server
        final ISynchronousProcess stopDockerProcess = SystemCommandExecuterFactory.builder().docker()
                .stop("icap-server")
                .build()
                .runSynchronous();
        assertNotNull(stopDockerProcess);
        assertEquals("icap-server" + NL, stopDockerProcess.getOutput());
        assertEquals("", stopDockerProcess.getErrorOutput());
        //assertEquals(0, startDockerProcess.getExitValue());
        
        startDockerProcess.waitFor();
        assertNotNull(startDockerProcess);
        assertNotNull(startDockerProcess.getExitValue());
        assertEquals(startMessage + NL, ProcessStreamUtil.getInstance().removeCR(output.toString()));
        //assertEquals("" + NL, ProcessStreamUtil.getInstance().removeCR(errOutput.toString()));
        //assertEquals(0, startDockerProcess.getExitValue());
    }

    
    /**
     * Shows the usage
     * 
     * @throws InterruptedException in case of a thread interruption
     */
    //@Test
    public void usageDocker() throws InterruptedException {
        //final String param1 = "-param1";
        //docker run -it --rm alpine /bin/ash
        // docker run --rm --name icap-server -p 1344:1344 toolarium/toolarium-icap-calmav-docker:0.0.1
        ProcessInputStreamSource inputBuffer = ProcessInputStreamSource.BUFFER;
        inputBuffer.setBuffer("exit");
        
        ProcessBufferOutputStream output = null; //new ProcessBufferOutputStream();
        ProcessBufferOutputStream errOutput = null; // new ProcessBufferOutputStream();
        IAsynchronousProcess startDockerProcess = SystemCommandExecuterFactory.builder().docker()
                .run("alpine")
                .interactive(true)
                .remove(true)
                .parameter("/bin/ash")
            .build()
            .runAsynchronous(ProcessInputStreamSource.INHERIT, output, errOutput);
        
        Thread.sleep(10 * 1000);
        startDockerProcess.waitFor();
        assertNotNull(startDockerProcess);
        assertNotNull(startDockerProcess.getExitValue());
        assertEquals(0, startDockerProcess.getExitValue());
        //assertEquals("", ProcessStreamUtil.getInstance().removeCR(output.toString()));
        //assertEquals(TestMain.STD_ERR_TEST + NL, ProcessStreamUtil.getInstance().removeCR(errOutput.toString()));
        //assertNotNull(myAsyncProcess.getTotalCpuDuration());
    }

    
    /**
     * Shows the usage of the start and stop of a docker container
     * 
     * @throws InterruptedException in case of a thread interruption
     * @throws IOException In case of an I/O error
     */
    @Test
    public void usageDockerImages() throws InterruptedException, IOException {
        ISynchronousProcess dockerImagesProcess = SystemCommandExecuterFactory.builder().docker()
                .images()
                .build()
                .runSynchronous();
        assertNotNull(dockerImagesProcess);
        assertTrue(dockerImagesProcess.getOutput().startsWith("REPOSITORY"));
        assertEquals("", dockerImagesProcess.getErrorOutput());

        dockerImagesProcess = SystemCommandExecuterFactory.builder().docker()
                .images(TOOLARIUM_TOOLARIUM_ICAP_CALMAV_DOCKER)
                .build()
                .runSynchronous();
        assertNotNull(dockerImagesProcess);
        assertTrue(dockerImagesProcess.getOutput().startsWith("REPOSITORY"));

        BufferedReader reader = new BufferedReader(new StringReader(dockerImagesProcess.getOutput()));
        assertTrue(reader.readLine().startsWith("REPOSITORY"));

        // toolarium/toolarium-icap-calmav-docker   0.0.1     30993438a894   20 months ago   481MB
        String[] line = reader.readLine().split(" ");
        assertEquals(TOOLARIUM_TOOLARIUM_ICAP_CALMAV_DOCKER, line[0] + ":" + line[3]);

        assertEquals("", dockerImagesProcess.getErrorOutput());
        
        //assertEquals("icap-server" + NL, dockerImagesProcess.getOutput());
        //assertEquals(0, startDockerProcess.getExitValue());
        
    }

    

}
