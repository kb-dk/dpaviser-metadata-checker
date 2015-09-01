package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;

import dk.statsbiblioteket.dpaviser.metadatachecker.NameInputStreamValidator;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.OutputHandler;
import edu.harvard.hul.ois.jhove.module.PdfModule;

import java.io.InputStream;
import java.util.Collections;

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
        App app = new App("dpaviser", "${project.version}", new int[]{2015, 8, 24}, "usage...", "rights...");
        // Condensed from Jhove.java
        JhoveBase je = new JhoveBase();
        //je.setLogLevel("SEVERE");
        je.setLogLevel("INFO");
        je.init("/home/tra/git/dpaviser-aggregator/jhove/jhove-installer/src/main/config/jhove.conf", null); // no config file.

        PdfModule module = new PdfModule();
        module.init(null);

        module.setDefaultParams(Collections.EMPTY_LIST);
        module.resetParams();

        OutputHandler handler = je.getHandler("xml");

        OutputHandler aboutHandler = null; // debugging help not used by us.

        String[] dirFileOrUri = {"/home/tra/Skrivebord/infomed/DRS/2015/06/01/DRS20150601X11#0001.pdf"};
        String outputFile = null; // Output file _name_.  null -> System.out.
        String tempDir = null;

        /**********************************************************
         * Invoke the JHOVE engine.
         **********************************************************/

        je.setEncoding("utf-8");
        je.setTempDirectory(tempDir); // use default
        je.setBufferSize(131072);
        je.setChecksumFlag(false);
        je.setShowRawFlag(false);
        je.setSignatureFlag(false);
        je.dispatch(app, module, null, handler, outputFile,
                dirFileOrUri);
        return true;
    }
//
//    File temp = null;
//
//        http://softwarecave.org/2014/02/05/create-temporary-files-and-directories-using-java-nio2/
//
//        try {
//            Path tempPath = Files.createTempFile(null, ".pdf");
//            Files.copy(inputStream, tempPath, StandardCopyOption.REPLACE_EXISTING);
//            temp = tempPath.toFile();
//
//            // ----  Not even close to PDF/A. We are happy if we can parse it with PDF-box.
//
//            // https://pdfbox.apache.org/1.8/cookbook/pdfavalidation.html
//            PreflightParser parser = new PreflightParser(temp);
//
//            PreflightDocument document = null;
//            try {
//                parser.parse();
//
//                document = parser.getPreflightDocument();
//                // document.validate();  // These won't.
//            } catch (SyntaxValidationException e) {
//                resultCollector.addFailure(
//                        name,
//                        "preflight failed",
//                        getClass().getSimpleName(),
//                        "Error testing pdf: " + e.toString(),
//                        Strings.getStackTrace(e)
//                );
//                return false;
//            } finally {
//                if (document != null) {
//                    document.close();
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("testing PDF", e);
//        } finally {
//            if (temp != null) {
//                temp.delete();
//            }
//        }
//        return true;
//    }
}
