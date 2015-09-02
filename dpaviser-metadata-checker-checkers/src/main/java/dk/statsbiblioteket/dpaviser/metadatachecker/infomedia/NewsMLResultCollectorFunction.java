package dk.statsbiblioteket.dpaviser.metadatachecker.infomedia;

import dk.statsbiblioteket.dpaviser.metadatachecker.NameInputStreamResultCollectorFunction;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.newspaper.metadatachecker.caches.DocumentCache;
import dk.statsbiblioteket.util.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;

public class NewsMLResultCollectorFunction implements NameInputStreamResultCollectorFunction {

    public static final String NEWSML_XSD = "/NewsML_1.2-infomedia.xsd";

    private final DocumentCache documentCache;
    private final ResultCollector resultCollector;
    private final DocumentBuilderFactory documentBuilderFactory;
    private String name;
    private String version;
    private ErrorHandler failingErrorHandler = new ErrorHandler() {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    };

    public NewsMLResultCollectorFunction(ResultCollector resultCollector, DocumentCache documentCache,
                                         String name, String version) {
        this.resultCollector = resultCollector;
        this.documentCache = documentCache;
        this.name = name;
        this.version = version;

        InputStream schemaInputStream = getClass().getResourceAsStream(NEWSML_XSD);
        if (schemaInputStream == null) {
            throw new RuntimeException("Resource could not be read: " + NEWSML_XSD);
        }

        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            documentBuilderFactory.setSchema(createSchema(schemaInputStream));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Could not create schema", e);
        }

    }

    private Schema createSchema(InputStream schemaInputStream) throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setErrorHandler(failingErrorHandler);

        // schema asks for xml.xsd.  Provide a resource resolver that knows how to find things in the classpath.

        // http://stackoverflow.com/a/2342859/53897
        schemaFactory.setResourceResolver(new LSResourceResolver() {
            @Override
            public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
                // note: in this sample, the XSD's are expected to be in the root of the classpath
                InputStream resourceAsStream = //
                        this.getClass().getClassLoader().getResourceAsStream(systemId);
                return new ClasspathResourceInput(publicId, systemId, resourceAsStream);
            }
        });

        StreamSource s = new StreamSource(schemaInputStream);
        Schema schema = schemaFactory.newSchema(s);
        return schema;
    }

    @Override
    public ResultCollector apply(String name, InputStream inputStream) {
        ResultCollector resultCollector = new ResultCollector(name, version, null);
        try {
            DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
            db.setErrorHandler(failingErrorHandler);
            Document document = db.parse(inputStream);  // parsing is set up to validate
        } catch (Exception e) {
            resultCollector.addFailure(
                    name,
                    "exception",
                    getClass().getSimpleName(),
                    "Error verifying PDF: " + e.toString(),
                    Strings.getStackTrace(e));
        }
        return resultCollector;
    }
}
