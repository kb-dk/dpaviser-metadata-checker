package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;

import dk.statsbiblioteket.dpaviser.metadatachecker.NameInputStreamResultCollectorFunction;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.util.Strings;
import dk.statsbiblioteket.util.console.ProcessRunner;
import org.apache.ws.commons.util.NamespaceContextImpl;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public class PDFResultCollectorFunction implements NameInputStreamResultCollectorFunction {
    private static final java.lang.String VERSION = "1.0";
    /**
     * May be used for generating XPath with jhove namespace defined (as jhove)
     */
    public final XPath JHoveAwareXPath;
    protected String encoding;
    protected XPathExpression statusXpath;
    protected DocumentBuilderFactory factory;
    private Predicate<Document>[] checks;

    public PDFResultCollectorFunction(Predicate<Document>... checks) {
        this.checks = checkNotNull(checks);

        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        // Stolen from IteratorForFedora3.java
        XPathFactory xpathFactory = XPathFactory.newInstance();
        JHoveAwareXPath = xpathFactory.newXPath();

        NamespaceContextImpl context = new NamespaceContextImpl();
        context.startPrefixMapping("jhove", "http://hul.harvard.edu/ois/xml/ns/jhove");
        JHoveAwareXPath.setNamespaceContext(context);

        try {
            statusXpath = JHoveAwareXPath.compile("/jhove:jhove/jhove:repInfo/jhove:status/text()");
        } catch (XPathExpressionException e) {
            throw new RuntimeException("statusXpath", e);
        }
    }


    protected ResultCollector apply0(String name, InputStream inputStream, ResultCollector resultCollector) throws Exception {
       List<String> command = Arrays.<String>asList("jhove", "-h", "xml", "-m", "pdf-hul");
        Document document;
        ProcessRunner processRunner = new ProcessRunner(command);
        processRunner.setInputStream(inputStream);
        processRunner.setErrorCollectionByteSize(-1);
        processRunner.setOutputCollectionByteSize(-1);
        processRunner.run();

        System.err.println(processRunner.getProcessErrorAsString());

        inputStream.close();

        DocumentBuilder builder = factory.newDocumentBuilder();

        document = builder.parse(processRunner.getProcessOutput());

        // Now test it.

        for (int i = 0; i < checks.length; i++) {
            try {
                if (checks[i].test(document) == false) {
                    resultCollector.addFailure(
                            name,
                            "check #" + i + " failed",
                            checks[i].getClass().getSimpleName(),
                            "Error validating PDF");
                }
            } catch (Exception e) {
                resultCollector.addFailure(
                        name,
                        "test #" + i + " threw exception",
                        checks[i].getClass().getSimpleName(),
                        "Error validating PDF",
                        Strings.getStackTrace(e)
                );
            }
        }
//
//        String status = statusXpath.evaluate(document);
//        System.out.println(">>" + status + "<<");
        return resultCollector;
    }

    @Override
    public ResultCollector apply(String name, InputStream inputStream) {
        if (inputStream == null) {
            throw new NullPointerException("inputStream == null");
        }
        ResultCollector resultCollector = new ResultCollector(name, VERSION, null);
        try {
            apply0(name, inputStream, resultCollector);
        } catch (Exception e) {
            resultCollector.addFailure(
                    name,
                    "exception",
                    getClass().getSimpleName(),
                    "Error verifying PDF: " + e.toString(),
                    Strings.getStackTrace(e)
            );
        }
        return resultCollector;
    }
}
