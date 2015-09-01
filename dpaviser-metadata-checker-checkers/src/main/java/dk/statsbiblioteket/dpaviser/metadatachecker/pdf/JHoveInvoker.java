package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;


import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class JHoveInvoker {



    public static void main(String[] args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(Arrays.<String>asList("jhove", "-h", "xml", "-m", "pdf-hul",
                "/home/tra/Skrivebord/pdf-sample.pdf"));
        Process process = pb.start();

        InputStream inputStream = process.getInputStream();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);

        process.waitFor();

        // ---

        printDocument(document, System.out);

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
