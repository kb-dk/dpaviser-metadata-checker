package dk.statsbiblioteket.dpaviser.metadatachecker;

import dk.statsbiblioteket.dpaviser.metadatachecker.mockers.MockupIteratorSuper;
import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

/** Test Metadata checker */
public class MetadataCheckerComponentTest {

    @Test
    /**
     * Test checking metadata on a "bad" batch.
     */
    public void testdoWorkOnItemBad() throws Exception {


        MetadataCheckerComponent metadataCheckerComponent = new MockupIteratorSuper(
                System.getProperties());
        
        Batch batch = new Batch("400022028240");
        batch.setRoundTripNumber(1);
        
        TestResultCollector result = new TestResultCollector(
                metadataCheckerComponent.getComponentName(), metadataCheckerComponent.getComponentVersion());

        metadataCheckerComponent.doWorkOnItem(batch, result);

        // Assert errors
        Assert.assertFalse(result.isSuccess(), result.toReport() + "\n");
        Assert.assertTrue(result.failures.contains(
                "2E: Failure validating XML data: Line 25 Column 21: Content is not allowed in trailing section."),
                          result.toReport());
        Assert.assertTrue(result.failures.contains(
                "2D: Failure validating XML data: Line 1 Column 53: cvc-elt.1: Cannot find the declaration of element 'mods:mods'."),
                          result.toReport());
        Assert.assertTrue(result.failures.contains(
                "2J: Failure validating XML data: Line 2 Column 180: cvc-elt.1: Cannot find the declaration of element 'altox'."),
                          result.toReport());
        Assert.assertTrue(result.failures.contains(
                "2C: Failure validating XML data: Line 9 Column 16: The end-tag for element type \"mods:part\" must end with a '>' delimiter."),
                          result.toReport());
        Assert.assertTrue(result.failures.contains(
                "2K: Failure validating XML data: Line 63 Column 52: cvc-datatype-valid.1.2.1: 'hest' is not a valid value for 'integer'."),
                          result.toReport());
    }

    @Test
    /**
     * Test checking on a "good" batch.
     */
    public void testdoWorkOnItemGood() throws Exception {
        Properties properties = new Properties(System.getProperties());
        properties.setProperty("atNinestars","true");
        properties.setProperty("jpylyzerPath",getJpylyzerPath());
        MetadataCheckerComponent metadataCheckerComponent = new MockupIteratorSuper(properties);
        TestResultCollector result = new TestResultCollector(
                metadataCheckerComponent.getComponentName(), metadataCheckerComponent.getComponentVersion());
        Batch batch = new Batch("400022028241");
        batch.setRoundTripNumber(1);
        metadataCheckerComponent.doWorkOnItem(batch, result);
        Assert.assertTrue(result.isSuccess(), result.toReport() + "\n");
    }


    private String getJpylyzerPath() {
          return "src/main/extras/jpylyzer-1.10.1/jpylyzer.py";
      }
}