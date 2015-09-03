package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;

import dk.statsbiblioteket.dpaviser.metadatachecker.NameInputStreamResultCollectorFunction;
import dk.statsbiblioteket.dpaviser.metadatachecker.helpers.CommandPipe;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.util.Strings;
import dk.statsbiblioteket.util.xml.DOM;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public class PDFResultCollectorFunction implements NameInputStreamResultCollectorFunction {
    private static final java.lang.String VERSION = "1.0";
    /**
     * May be used for generating XPath with jhove namespace defined (as jhove)
     */
    private Predicate<Document>[] checks;
    private CommandPipe commandPipe;

    public PDFResultCollectorFunction(CommandPipe commandPipe, Predicate<Document>... checks) {
        this.commandPipe = commandPipe;
        this.checks = checkNotNull(checks);

    }


    protected ResultCollector apply0(String name, InputStream inputStream, ResultCollector resultCollector) throws Exception {

        InputStream pipeResult = commandPipe.apply(inputStream);
        //ProcessRunner processRunner = getProcessRunnerForInputStream(JHOVE_PDF_INVOCATION_COMMAND_LIST, inputStream);
        inputStream.close();
        Document document = DOM.streamToDOM(pipeResult);

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
            throw new NullPointerException("inputStream");
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
