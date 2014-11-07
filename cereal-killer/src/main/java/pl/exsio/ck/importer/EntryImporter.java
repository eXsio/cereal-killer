package pl.exsio.ck.importer;

import java.io.File;

/**
 *
 * @author exsio
 */
public interface EntryImporter {

    void importFile(File file, boolean updateEnabled);

}
