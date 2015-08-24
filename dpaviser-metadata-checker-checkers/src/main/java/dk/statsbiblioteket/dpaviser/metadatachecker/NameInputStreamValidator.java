package dk.statsbiblioteket.dpaviser.metadatachecker;

import java.io.InputStream;

/**
 * interface _out_ of TreeEventHandler implementations to do actual validation
 * as well as allowing for easier testing of functionality.
 */

public interface NameInputStreamValidator {
    boolean doValidate(String name, InputStream inputStream) throws Exception;
}
