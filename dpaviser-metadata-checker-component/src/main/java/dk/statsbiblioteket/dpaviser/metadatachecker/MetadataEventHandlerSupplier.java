package dk.statsbiblioteket.dpaviser.metadatachecker;

import dk.statsbiblioteket.dpaviser.metadatachecker.helper.JHoveCommandPipe;
import dk.statsbiblioteket.dpaviser.metadatachecker.pdf.PDFEventHandler;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.TreeEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.AttributeSpec;
import dk.statsbiblioteket.newspaper.metadatachecker.SchemaValidatorEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.SchematronValidatorEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.caches.DocumentCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<String, AttributeSpec> attributeConfigs;


    public MetadataEventHandlerSupplier(Properties properties, DocumentCache documentCache, ResultCollector resultCollector) {
        this.properties = properties;
        this.documentCache = documentCache;
        this.resultCollector = resultCollector;
        attributeConfigs  = new HashMap<>();
        attributeConfigs.put(".xml",new AttributeSpec(".xml","NewsML_1.2-infomedia.xsd","NewsML_1.2-infomedia.sch","NewsML: ","metadata"));
    }

    @Override
    public List<TreeEventHandler> get() {

        return asList(
                new PDFEventHandler(properties, new JHoveCommandPipe(), resultCollector),
                //new NewsMLEventHandler(properties, documentCache, resultCollector),
                new SchemaValidatorEventHandler(resultCollector,documentCache,attributeConfigs),
                new SchematronValidatorEventHandler(resultCollector,documentCache,attributeConfigs)
        );
    }
}
