package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;


import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.AttributeParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.DefaultTreeEventHandler;
import dk.statsbiblioteket.util.Strings;

import java.util.Properties;

public class PDFEventHandler extends DefaultTreeEventHandler {
    private final ResultCollector resultCollector;
    private final PDFResultCollectorFunction pdfValidator;
    private Properties properties;

    public PDFEventHandler(Properties properties, ResultCollector resultCollector) {
        this.properties = properties;
        this.resultCollector = resultCollector;
        this.pdfValidator = new PDFResultCollectorFunction(d -> false);
    }


    @Override
    public void handleAttribute(AttributeParsingEvent event) {
        if (event.getName().endsWith(".pdf/contents")) { // FIXME:  Artifact of newspaper pipeline.
            try {
                pdfValidator.apply(event.getName(), event.getData()).mergeInto(resultCollector);
            } catch (Exception e) {
                resultCollector.addFailure(
                        event.getName(),
                        "exception",
                        getClass().getSimpleName(),
                        "Error verifying PDF: " + e.toString(),
                        Strings.getStackTrace(e)
                );
            }
        }
    }
}
