package dk.statsbiblioteket.dpaviser.metadatachecker;

import dk.statsbiblioteket.dpaviser.metadatachecker.pdf.PDFEventHandler;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.TreeEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.caches.DocumentCache;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Provides the complete set of structure checkers for the batch structure.
 */
public class BatchMetadataEventHandlerSupplier implements Supplier<List<TreeEventHandler>> {

    private Properties properties;
    final private DocumentCache documentCache;
    private final ResultCollector resultCollector;


    public BatchMetadataEventHandlerSupplier(Properties properties, DocumentCache documentCache, ResultCollector resultCollector) {
        this.properties = properties;
        this.documentCache = documentCache;
        this.resultCollector = resultCollector;
    }

    @Override
    public List<TreeEventHandler> get() {
        return Arrays.<TreeEventHandler>asList(
                new PDFEventHandler(properties, resultCollector)
        );
    }
}