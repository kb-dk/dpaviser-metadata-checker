package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;

import com.google.common.base.Throwables;
import dk.statsbiblioteket.dpaviser.metadatachecker.NameInputStreamValidator;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.util.Strings;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;

import javax.activation.FileDataSource;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PDFValidator implements NameInputStreamValidator {
    private ResultCollector resultCollector;

    public PDFValidator(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    @Override
    public boolean test(String name, InputStream inputStream) {
        File temp = null;
        try {
            temp = File.createTempFile(name, ".pdf");
            Files.copy(inputStream, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);

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
                        name,
                        "preflight failed",
                        getClass().getSimpleName(),
                        "Error validating pdf: " + e.toString(),
                        Strings.getStackTrace(e)
                );
                return false;
            } finally {
                if (document != null) {
                    document.close();
                }
            }
        } catch (Exception e) {
            Throwables.propagate(e);
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }
        return true;
    }
}
