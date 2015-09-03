package dk.statsbiblioteket.dpaviser.metadatachecker.helper;

import dk.statsbiblioteket.dpaviser.metadatachecker.helpers.CommandPipe;
import dk.statsbiblioteket.util.console.ProcessRunner;

import java.io.InputStream;
import java.util.List;

import static java.util.Arrays.asList;


public class JHoveCommandPipe implements CommandPipe {
    private final List<String> command;

    public JHoveCommandPipe(String dir) {
        this.command = asList(dir + "/jhove", "-h", "xml", "-m", "pdf-hul", "-l", "OFF");
    }

    @Override
    public InputStream apply(InputStream is) {
        ProcessRunner processRunner = new ProcessRunner(command);
        processRunner.setInputStream(is);
        processRunner.setErrorCollectionByteSize(-1);
        processRunner.setOutputCollectionByteSize(-1);
        processRunner.run();
        String processError = processRunner.getProcessErrorAsString();
        if (processError.isEmpty() == false) {
            System.err.println(processError);
        }
        return processRunner.getProcessOutput();
    }
}
