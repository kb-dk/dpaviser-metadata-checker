package dk.statsbiblioteket.dpaviser.metadatachecker;

import java.io.InputStream;
import java.util.function.BiPredicate;

/**
 * interface _out_ of TreeEventHandler implementations to do actual validation
 * as well as allowing for easier testing of functionality.
 */

public interface NameInputStreamValidator extends BiPredicate<String, InputStream> {

}
