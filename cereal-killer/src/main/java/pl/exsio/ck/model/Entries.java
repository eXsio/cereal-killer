package pl.exsio.ck.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author sdymi_000
 */
public class Entries {
    
    public static final int LOOKUP_PAGE_SIZE = 200;

    public static String[] getSerials(Collection<Entry> entries) {
        List<String> serials = new ArrayList<>();
        for (Entry e : entries) {
            serials.add(e.getSerialNo());
        }
        return serials.toArray(new String[serials.size()]);
    }
}
