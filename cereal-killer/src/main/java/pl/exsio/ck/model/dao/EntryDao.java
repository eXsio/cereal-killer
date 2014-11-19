package pl.exsio.ck.model.dao;

import java.util.ArrayList;
import java.util.Collection;
import pl.exsio.ck.model.Entry;

/**
 *
 * @author exsio
 */
public interface EntryDao {

    String SORT_DESC = "desc";

    String SORT_ASC = "asc";

    Collection<Entry> save(Collection<Entry> entries, boolean updateExisting);

    void connect(String url);

    void close();

    ArrayList<Object[]> fetchTableRows(int limit, int offset, String query, String orderBy, String orderDir, String[] serials);

    int count(String query, String[] serials);

    String[] matchSerials(String[] serials);
}
