package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;


import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.AttributeParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.DefaultTreeEventHandler;
import dk.statsbiblioteket.util.Strings;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;

import javax.activation.FileDataSource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class PDFEventHandler extends DefaultTreeEventHandler {
    private final ResultCollector resultCollector;
    private Properties properties;

    public PDFEventHandler(Properties properties, ResultCollector resultCollector) {
        this.properties = properties;
        this.resultCollector = resultCollector;
    }

    @Override
    public void handleAttribute(AttributeParsingEvent event) {
        if (event.getName().endsWith(".pdf/contents")) {
            try {
                doValidate(event);
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

    protected void doValidate(AttributeParsingEvent event) throws Exception {
        File temp = File.createTempFile(event.getName(), ".pdf");
        temp.deleteOnExit(); // FIXME:  Proper handling of temp files.
        Files.copy(event.getData(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // ----  Not even close to PDF/A. We are happy if we can parse it with PDF-box.

        // https://pdfbox.apache.org/1.8/cookbook/pdfavalidation.html
        FileDataSource fds = new FileDataSource(temp);
        PreflightParser parser = new PreflightParser(fds);

            PreflightDocument document = null;
        try {
            parser.parse();

            document = parser.getPreflightDocument();
            // document.validate();  // These won't.
        } catch (SyntaxValidationException e) {
            resultCollector.addFailure(
                    event.getName(),
                    "preflight failed",
                    getClass().getSimpleName(),
                    "Error validating pdf: " + e.toString(),
                    Strings.getStackTrace(e)
            );
        } finally {
            if (document != null) {
                document.close();
            }
        }

        // ----

        temp.delete();

    }
}
