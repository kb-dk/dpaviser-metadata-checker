package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;


import com.google.common.io.ByteStreams;
import org.apache.ws.commons.util.NamespaceContextImpl;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JHoveInvoker {


    public static void main(String[] args) throws Exception {
        List<String> command = Arrays.<String>asList("jhove", "-h", "xml", "-m", "pdf-hul",
                "/home/tra/Skrivebord/pdf-sample.pdf");

        byte[] outputBytes = invokeCommand(command);

        // inputstream copied to memory

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(outputBytes));

        // ---

        printDocument(document, System.out);

        // Stolen from IteratorForFedora3.java
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xPath = xpathFactory.newXPath();

        NamespaceContextImpl context = new NamespaceContextImpl();
        context.startPrefixMapping("jhove", "http://hul.harvard.edu/ois/xml/ns/jhove");
        xPath.setNamespaceContext(context);

        XPathExpression statusXpath = xPath.compile("/jhove:jhove/jhove:repInfo/jhove:status/text()");

        String status = statusXpath.evaluate(document);
        System.out.println(status);


    }

    public static byte[] invokeCommand(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        InputStream inputStream = process.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteStreams.copy(inputStream, baos);
        process.waitFor(60, TimeUnit.SECONDS);
        return baos.toByteArray();
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        // http://stackoverflow.com/a/2325407/53897
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }
}
