package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;

import dk.statsbiblioteket.dpaviser.metadatachecker.NameInputStreamValidator;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;

import java.io.InputStream;

public class PDFValidator implements NameInputStreamValidator {
    String encoding;
    private ResultCollector resultCollector;

    public PDFValidator(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    @Override
    public boolean test(String name, InputStream inputStream) {
        try {
            return test0(name, inputStream);
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
    }

    public boolean test0(String name, InputStream inputStream) throws Exception {
        return false;
    }
}
