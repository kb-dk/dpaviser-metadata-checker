package dk.statsbiblioteket.dpaviser.metadatachecker;

import dk.statsbiblioteket.dpaviser.metadatachecker.helper.JHoveCommandPipe;
import dk.statsbiblioteket.dpaviser.metadatachecker.infomedia.NewsMLEventHandler;
import dk.statsbiblioteket.dpaviser.metadatachecker.pdf.PDFEventHandler;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.TreeEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.caches.DocumentCache;

import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

/**
 * Provides the complete set of structure checkers for the batch structure.
 */
public class MetadataEventHandlerSupplier implements Supplier<List<TreeEventHandler>> {

    final private DocumentCache documentCache;
    private final ResultCollector resultCollector;
    private Properties properties;


    public MetadataEventHandlerSupplier(Properties properties, DocumentCache documentCache, ResultCollector resultCollector) {
        this.properties = properties;
        this.documentCache = documentCache;
        this.resultCollector = resultCollector;
    }

    @Override
    public List<TreeEventHandler> get() {

        return asList(
                new PDFEventHandler(properties, new JHoveCommandPipe(), resultCollector),
                new NewsMLEventHandler(properties, documentCache, resultCollector)
        );
    }
}
