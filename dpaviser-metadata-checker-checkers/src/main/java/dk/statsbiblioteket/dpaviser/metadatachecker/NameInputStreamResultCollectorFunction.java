package dk.statsbiblioteket.dpaviser.metadatachecker;

import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;

import java.io.InputStream;
import java.util.function.BiFunction;

/**
 * interface _out_ of TreeEventHandler implementations to do actual validation
 * as well as allowing for easier testing of functionality.
 */

public interface NameInputStreamResultCollectorFunction extends BiFunction<String, InputStream, ResultCollector> {

}
