package dk.statsbiblioteket.dpaviser.metadatachecker.pdf.helpers;

import dk.statsbiblioteket.dpaviser.metadatachecker.helpers.CommandPipe;

import java.io.InputStream;

/**
 * Do not process the InputStream given but just unconditionally return the resource
 * whose name was passed in the constructor.
 */
public class ResourceReplacerCommandPipe implements CommandPipe {

    private String resourceName;

    public ResourceReplacerCommandPipe(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public InputStream apply(InputStream inputStream) {
        return this.getClass().getResourceAsStream(resourceName);
    }
}
