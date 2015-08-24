package dk.statsbiblioteket.dpaviser.metadatachecker.infomedia;

import org.w3c.dom.ls.LSInput;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/** ClasspathResourceInput is needed as xml.xsd is xs:import'ed, and reliable information on
 * how to expand this inside the XSD itself was not easily locatable.
 *
 * This class allows using an InputStream as an LSInput.
 *
 * Solution stolen from http://stackoverflow.com/q/2342808/53897
 *
 */
public class ClasspathResourceInput implements LSInput {

    private String publicId;

    private String systemId;
    private BufferedInputStream inputStream;

    public ClasspathResourceInput(String publicId, String sysId, InputStream input) {
        this.publicId = publicId;
        this.systemId = sysId;
        this.inputStream = new BufferedInputStream(input);
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getBaseURI() {
        return null;
    }

    public void setBaseURI(String baseURI) {
    }

    public InputStream getByteStream() {
        return null;
    }

    public void setByteStream(InputStream byteStream) {
    }

    public boolean getCertifiedText() {
        return false;
    }

    public void setCertifiedText(boolean certifiedText) {
    }

    public Reader getCharacterStream() {
        return null;
    }

    public void setCharacterStream(Reader characterStream) {
    }

    public String getEncoding() {
        return null;
    }

    public void setEncoding(String encoding) {
    }

    public String getStringData() {
        synchronized (inputStream) {
            try {
                byte[] input = new byte[inputStream.available()];
                inputStream.read(input);
                String contents = new String(input);
                return contents;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Exception " + e);
                return null;
            }
        }
    }

    public void setStringData(String stringData) {
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public BufferedInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(BufferedInputStream inputStream) {
        this.inputStream = inputStream;
    }
}