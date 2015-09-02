package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;

import dk.statsbiblioteket.dpaviser.metadatachecker.helpers.CommandPipe;
import dk.statsbiblioteket.dpaviser.metadatachecker.pdf.helpers.ResourceReplacerCommandPipe;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import org.apache.ws.commons.util.NamespaceContextImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

public class PDFResultCollectorFunctionTest {

    CommandPipe pregeneratedJHoveOutput = new ResourceReplacerCommandPipe("/BMA20150831X11#0002.xml");

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullInputStream() throws Exception {
        CommandPipe commandPipe = new ResourceReplacerCommandPipe("/non-existing");
        ResultCollector result = new PDFResultCollectorFunction(commandPipe).apply("name", null);
        Assert.fail("should not succede when resource isn't available");
    }

    @Test
    public void testSingleAlwaysFailCheck() throws Exception {
        ResultCollector result = getResultCollectorFor("name", d -> false);
        Assert.assertFalse(result.isSuccess(), "not success");
    }


    @Test
    public void testNoCheckSoShouldPass() throws Exception {
        ResultCollector result = getResultCollectorFor("name");
        Assert.assertTrue(result.isSuccess(), "success");
    }


    @Test
    public void testSimpleJHoveChecks() throws Exception {
        // XPath set up stolen from IteratorForFedora3.java
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xp = xpathFactory.newXPath();

        NamespaceContextImpl context = new NamespaceContextImpl();
        context.startPrefixMapping("j", "http://hul.harvard.edu/ois/xml/ns/jhove");
        xp.setNamespaceContext(context);

        ResultCollector result = getResultCollectorFor("name",
                d -> xpathCheck(xp, "/j:jhove/j:repInfo/j:version/text()", d, "1.4"),
                d -> xpathCheck(xp, "/j:jhove/j:repInfo/j:status/text()", d, "Well-Formed and valid"),
                d -> xpathCheck(xp, "count(//j:property/j:name[text() = 'Font'])", d, "3")
        );
        Assert.assertTrue(result.isSuccess(), result.toReport());  // If unsuccessful, use the report as message.
    }

    /**
     * Helper routine to avoid having try-catch logic and multiple lines (as assertEquals does not return true if not
     * failing) in the lambda expressions,
     *
     * @param xp             XPath ready to compile
     * @param expression     XPath expression to evaluate
     * @param d              Document to evaluate <i>on</i>.
     * @param expectedResult expected result - compared to the actual result with Assert.assertEquals so the test will
     *                       fail if not as expected.
     * @return
     */
    protected boolean xpathCheck(XPath xp, String expression, Object d, String expectedResult) {
        try {
            String actualResult = xp.compile(expression).evaluate(d);
            Assert.assertEquals(actualResult, expectedResult);
            return expectedResult.equals(actualResult);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("cannot compile: " + expression, e);
        }
    }

    protected ResultCollector getResultCollectorFor(String name, Predicate... predicates) throws IOException {
        try (InputStream is = new ByteArrayInputStream(new byte[0])) {
            return new PDFResultCollectorFunction(pregeneratedJHoveOutput, predicates).apply(name, is);
        }
    }
}
