package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;

import dk.statsbiblioteket.dpaviser.metadatachecker.NameInputStreamValidator;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.util.Strings;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PDFValidator implements NameInputStreamValidator {
    private ResultCollector resultCollector;

    public PDFValidator(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    @Override
    public boolean test(String name, InputStream inputStream) {
        File temp = null;

        http://softwarecave.org/2014/02/05/create-temporary-files-and-directories-using-java-nio2/

        try {
            Path tempPath = Files.createTempFile(null, ".pdf");
            Files.copy(inputStream, tempPath, StandardCopyOption.REPLACE_EXISTING);
            temp = tempPath.toFile();

            // ----  Not even close to PDF/A. We are happy if we can parse it with PDF-box.

            // https://pdfbox.apache.org/1.8/cookbook/pdfavalidation.html
            PreflightParser parser = new PreflightParser(temp);

            PreflightDocument document = null;
            try {
                parser.parse();

                document = parser.getPreflightDocument();
                // document.validate();  // These won't.
            } catch (SyntaxValidationException e) {
                resultCollector.addFailure(
                        name,
                        "preflight failed",
                        getClass().getSimpleName(),
                        "Error testing pdf: " + e.toString(),
                        Strings.getStackTrace(e)
                );
                return false;
            } finally {
                if (document != null) {
                    document.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("testing PDF", e);
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }
        return true;
    }
}
