package pl.exsio.ck.model.reader;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import pl.exsio.ck.model.Entry;

/**
 *
 * @author exsio
 */
public interface EntryReader {

    Collection<Entry> readEntries(File file, String progressName, boolean serialsOnly);

    Map<String, String> getAcceptedFormats();
}
