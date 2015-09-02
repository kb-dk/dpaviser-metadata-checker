package dk.statsbiblioteket.dpaviser.metadatachecker.pdf;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;

public class PDFResultCollectorFunctionTest {
    @Test
    public void testNullInputStream() throws Exception {
        try {
            ResultCollector result = new PDFResultCollectorFunction().apply("name", null);
            Assert.fail("resource not available");
        } catch (Exception e) {
            Assert.assertEquals("inputStream == null", e.getMessage());
        }
    }

    @Test
    public void testSingleAlwaysFailCheck() throws Exception {
        try (InputStream inputStream = PDFResultCollectorFunctionTest.class.getResourceAsStream("/BMA20150831X11#0002.pdf")) {
            ResultCollector result = new PDFResultCollectorFunction(d -> false).apply("name", inputStream);
            Assert.assertFalse(result.isSuccess(), "not success");
        }
    }

    @Test
    public void testNoCheckSoShouldPass() throws Exception {
        try (InputStream inputStream = PDFResultCollectorFunctionTest.class.getResourceAsStream("/BMA20150831X11#0002.pdf")) {
            ResultCollector result = new PDFResultCollectorFunction().apply("name", inputStream);
            System.out.println(result.toReport());
            System.out.println();
            Assert.assertTrue(result.isSuccess(), "success");
        }
    }
}
