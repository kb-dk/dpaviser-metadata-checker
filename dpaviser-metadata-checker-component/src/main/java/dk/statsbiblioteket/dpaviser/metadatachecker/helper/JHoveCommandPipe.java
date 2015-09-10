package dk.statsbiblioteket.dpaviser.metadatachecker.helper;

import dk.statsbiblioteket.dpaviser.metadatachecker.helpers.CommandPipe;
import dk.statsbiblioteket.util.console.ProcessRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;


public class JHoveCommandPipe implements CommandPipe {
    private final List<String> command;
    private Map<String, String> environmentVariables;

    public JHoveCommandPipe() {
        this(System.getProperty("user.home") + System.getProperty("file.separator") + "jhove-beta", new HashMap<>());
    }

    public JHoveCommandPipe(String dir) {
        List<String> l = new ArrayList<String>();
        l.addAll(System.getProperty("os.name").startsWith("Windows")
                ? asList("cmd", "/c", dir + System.getProperty("file.separator") + "jhove.bat", "-c", dir + "/../conf/jhove.conf")
                : asList(dir + System.getProperty("file.separator") + "jhove", "-c", dir + "/../conf/jhove.conf"));

        l.addAll(asList("-h", "xml", "-m", "pdf-hul", "-l", "OFF"));
        this.command = l;
    }

    public JHoveCommandPipe(String dir, Map<String,String> environmentVariables) {
        this(dir);
        this.environmentVariables = environmentVariables;
    }

    @Override
    public InputStream apply(InputStream is) {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(getClass().getSimpleName(), ".tmp");
            Files.copy(is, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            List<String> actualCommand = new ArrayList<>(command);
            actualCommand.add(tmpFile.getAbsolutePath());

            System.out.println(actualCommand);
            ProcessRunner processRunner = new ProcessRunner(actualCommand);
            processRunner.setEnviroment(environmentVariables);
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
