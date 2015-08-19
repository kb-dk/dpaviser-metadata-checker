package dk.statsbiblioteket.dpaviser.metadatachecker;

import dk.statsbiblioteket.dpaviser.metadatachecker.jpylyzer.JpylyzingEventHandler;
import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.EventHandlerFactory;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.TreeEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.AltoMixCrossCheckEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.AltoXPathEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.AttributeSpec;
import dk.statsbiblioteket.newspaper.metadatachecker.ChecksumCheckEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.MixFilmCrossCheckEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.SchemaValidatorEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.SchematronValidatorEventHandler;
import dk.statsbiblioteket.newspaper.metadatachecker.caches.DocumentCache;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A factory for checks to do on metadata. */
public class MetadataChecksFactory
        implements EventHandlerFactory {

    /** This enum contains all the checkers that can be disabled. */
    public enum Checks{
        ALTO_XPATH,ALTO_MIX,CHECKSUM,EDITION_MODS,MIX_FILM,MODS_XPATH,SCHEMATRON,SCHEMA_VALIDATOR,MIX_XML,FILM_XML,JPYLYZER;
    }
    /** The result collector to collect errors in. */
    private ResultCollector resultCollector;
    private boolean atNinestars = false;
    private String batchFolder;
    private String jpylyzerPath;
    private Document batchXmlStructure;
    private final Set<Checks> disabledChecks;

    /**
     * Initialise the MetadataChecksFactory with a result collector to collect errors in.
     * @param resultCollector The result collector to collect errors in.
     * @param batch           a batch object representing the batch being analysed.
     * @param disabledChecks  a set of enums detailing the checks to be disabled
     */
    public MetadataChecksFactory(ResultCollector resultCollector, Batch batch,
                                 Document batchXmlStructure, Set<Checks> disabledChecks) {
        this.resultCollector = resultCollector;
        this.batchXmlStructure = batchXmlStructure;
        if (disabledChecks == null){
            disabledChecks = new HashSet<>();
        }
        this.disabledChecks = disabledChecks;

        //FIXME Introduce checks on batch context
    }

    /**
     * Construct a metadata checks factory that is usable for ninestars
     * @param resultCollector     the result collector to collect errors in
     * @param atNinestars         should be true, sets the framework to run in the ninestars context
     * @param batchFolder         the folder where the batches lie
     * @param jpylyzerPath        the path to the jpylyzer executable. If null, jpylyzer will be used from the PATH
     * @param batch               a batch object representing the batch being analysed.
     * @param disabledChecks      a set of enums detailing the checks to be disabled
     */
    public MetadataChecksFactory(ResultCollector resultCollector, boolean atNinestars, String batchFolder, String jpylyzerPath,
                                 Batch batch, Document batchXmlStructure, Set<Checks> disabledChecks) {
        this(resultCollector, batch,batchXmlStructure, disabledChecks);
        this.atNinestars = atNinestars;
        this.batchFolder = batchFolder;
        this.jpylyzerPath = jpylyzerPath;
        this.batchXmlStructure = batchXmlStructure;

    }



    /**
     * Add all metadata checking event handlers.
     *
     * @return The list of metadata checking event handlers.
     */
    @Override
    public List<TreeEventHandler> createEventHandlers() {
        ArrayList<TreeEventHandler> treeEventHandlers = new ArrayList<>();
        DocumentCache documentCache = new DocumentCache();

        Map<String, AttributeSpec> attributeConfigs = getAttributeValidationConfig();

        if (atNinestars) {
            if (!disabledChecks.contains(Checks.CHECKSUM)) {
                treeEventHandlers.add(new ChecksumCheckEventHandler(resultCollector));
            }

            if (!disabledChecks.contains(Checks.JPYLYZER)) {
                //This thing adds virtual jpylyzer.xml nodes
                treeEventHandlers.add(new JpylyzingEventHandler(resultCollector, batchFolder, jpylyzerPath));
            }
        }
        if (!disabledChecks.contains(Checks.SCHEMA_VALIDATOR)) {
            treeEventHandlers.add(new SchemaValidatorEventHandler(resultCollector, documentCache, attributeConfigs));
        }
        if (!disabledChecks.contains(Checks.SCHEMATRON)) {
            treeEventHandlers.add(new SchematronValidatorEventHandler(resultCollector, documentCache, attributeConfigs));
        }
//        if (!disabledChecks.contains(Checks.ALTO_XPATH)) {
//            treeEventHandlers.add(new AltoXPathEventHandler(resultCollector, documentCache));
//        }
//        if (!disabledChecks.contains(Checks.ALTO_MIX)) {
//            treeEventHandlers.add(new AltoMixCrossCheckEventHandler(resultCollector, documentCache));
//        }
//        if (!disabledChecks.contains(Checks.MIX_FILM)) {
//            treeEventHandlers.add(new MixFilmCrossCheckEventHandler(resultCollector, documentCache));
//        }

        return treeEventHandlers;
    }

    public static Map<String, AttributeSpec> getAttributeValidationConfig() {
        Map<String, AttributeSpec> attributeConfigs = new HashMap<>();
        attributeConfigs.put(".xml",new AttributeSpec(".xml", "NewsML_1.2-infomedia.xsd", "","","metadata"));
        return attributeConfigs;
    }
}