package dk.statsbiblioteket.dpaviser.metadatachecker.helpers;

import java.io.InputStream;
import java.util.function.Function;

public interface CommandPipe extends Function<InputStream,InputStream> {
}
