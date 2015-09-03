package dk.statsbiblioteket.dpaviser.metadatachecker.helper;

import dk.statsbiblioteket.dpaviser.metadatachecker.helpers.CommandPipe;
import dk.statsbiblioteket.util.console.ProcessRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;


public class JHoveCommandPipe implements CommandPipe {
    private final List<String> command;

    public JHoveCommandPipe(String dir) {
        this.command = asList(dir + "/jhove", "-h", "xml", "-m", "pdf-hul", "-l", "OFF");
    }

    public JHoveCommandPipe() {
        this(System.getProperty("user.home") + "/jhove-beta");
    }

    @Override
    public InputStream apply(InputStream is) {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(getClass().getSimpleName(), ".tmp");
            Files.copy(is, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            List<String> actualCommand = new ArrayList<>(command);
            actualCommand.add(tmpFile.getAbsolutePath());

            ProcessRunner processRunner = new ProcessRunner(actualCommand);
            processRunner.setErrorCollectionByteSize(-1);
            processRunner.setOutputCollectionByteSize(-1);
            processRunner.run();
            String processError = processRunner.getProcessErrorAsString();
            if (processError.isEmpty() == false) {
                System.err.println(processError);
            }
            return processRunner.getProcessOutput();
        } catch (IOException e) {
            throw new RuntimeException("cannot create tmpfile");
        } finally {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
    }
}
