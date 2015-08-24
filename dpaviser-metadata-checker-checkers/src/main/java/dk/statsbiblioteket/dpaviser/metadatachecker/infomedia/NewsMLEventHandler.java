package dk.statsbiblioteket.dpaviser.metadatachecker.infomedia;


import dk.statsbiblioteket.dpaviser.metadatachecker.NameInputStreamValidator;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.AttributeParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.DefaultTreeEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.caches.DocumentCache;
import dk.statsbiblioteket.util.Strings;

import java.io.InputStream;
import java.util.Properties;

public class NewsMLEventHandler extends DefaultTreeEventHandler {
    private final ResultCollector resultCollector;
    private final DocumentCache documentCache;
    private final NameInputStreamValidator validator;
    private Properties properties;



    public NewsMLEventHandler(Properties properties, DocumentCache documentCache, ResultCollector resultCollector) {
        this.properties = properties;

        this.documentCache = documentCache;
        this.resultCollector = resultCollector;
        this.validator= new NewsMLValidator(resultCollector, documentCache);
    }


    @Override
    public void handleAttribute(AttributeParsingEvent event) {
        if (event.getName().endsWith(".xml")) {
            try {
                doValidate(event);
            } catch (Exception e) {
                resultCollector.addFailure(
                        event.getName(),
                        "exception",
                        getClass().getSimpleName(),
                        "Error validating XML: " + e.toString(),
                        Strings.getStackTrace(e)
                );
            }
        }
    }

    protected void doValidate(AttributeParsingEvent event) throws Exception {
        // Validate against the appropriate schema.
        InputStream inputStream = event.getData();
        validator.doValidate(event.getName(), inputStream);
        // additional DOM checks?...
    }
}
