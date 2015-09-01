package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;

import dk.statsbiblioteket.dpaviser.metadatachecker.NameInputStreamValidator;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import org.apache.ws.commons.util.NamespaceContextImpl;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class PDFValidator implements NameInputStreamValidator {
    String encoding;
    private ResultCollector resultCollector;

    XPathExpression statusXpath;

    public PDFValidator(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
        // Stolen from IteratorForFedora3.java
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xPath = xpathFactory.newXPath();

        NamespaceContextImpl context = new NamespaceContextImpl();
        context.startPrefixMapping("jhove", "http://hul.harvard.edu/ois/xml/ns/jhove");
        xPath.setNamespaceContext(context);

        try {
            statusXpath = xPath.compile("/jhove:jhove/jhove:repInfo/jhove:status/text()");
        } catch (XPathExpressionException e) {
            throw new RuntimeException("statusXpath", e);
        }
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
        List<String> command = Arrays.<String>asList("jhove", "-h", "xml", "-m", "pdf-hul");
        Document document;
        document = new JHoveInvoker().domFromJHove(command, inputStream);
        JHoveInvoker.printDocument(document, System.out);

        String status = statusXpath.evaluate(document);
        System.out.println(status);
        return status.equals("Well-formed");
    }
}
