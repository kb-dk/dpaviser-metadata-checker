package dk.statsbiblioteket.dpaviser.metadatachecker;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.CallResult;
import dk.statsbiblioteket.medieplatform.autonomous.NewspaperBatchAutonomousComponentUtils;
import dk.statsbiblioteket.medieplatform.autonomous.RunnableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * This component checks metadata for validity.
 */
public class MetadataCheckerExecutable {


    private static Logger log = LoggerFactory.getLogger(MetadataCheckerExecutable.class);

    /**
     * The class must have a main method, so it can be started as a command line tool
     *
     * @param args the arguments.
     * @throws Exception
     * @see NewspaperBatchAutonomousComponentUtils#parseArgs(String[])
     */
    public static void main(String... args) throws Exception {
        System.exit(doMain(args));
    }

    private static int doMain(String[] args) throws IOException {
        log.info("Starting with args {}", new Object[]{args});

        //Parse the args to a properties construct
        Properties properties = NewspaperBatchAutonomousComponentUtils.parseArgs(args);

        RunnableComponent<Batch> component = new MetadataCheckerComponent(properties);
        CallResult result = NewspaperBatchAutonomousComponentUtils.startAutonomousComponent(properties, component);
        log.info(result.toString());
        return result.containsFailures();
    }
}