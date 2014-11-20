package pl.exsio.ck.comparator;

import pl.exsio.ck.comparator.result.ComparisonResult;
import java.io.File;

/**
 *
 * @author exsio
 */
public interface EntryComparator {

    ComparisonResult compareFile(File file);
}
