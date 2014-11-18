package pl.exsio.ck.comparator;

import java.io.File;

/**
 *
 * @author exsio
 */
public interface EntryComparator {

    ComparisonResult compareFile(File file);
}
