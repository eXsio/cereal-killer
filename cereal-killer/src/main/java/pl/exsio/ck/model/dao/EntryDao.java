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

    Entry save(Entry entry, boolean updateExisting);

    Collection<Entry> save(Collection<Entry> entries, boolean updateExisting);

    void clear();

    Entry findOne(int id);

    Collection<Entry> findAll();

    Entry findOneBySerialNo(String serialNo);

    Collection<Entry> findBySerialNos(String[] serialNos);

    void connect(String url);

    void close();

    ArrayList<Object[]> fetchTableRows(int limit, int offset, String query, String orderBy, String orderDir, String[] serials);

    int countAll();

    int count(String query, String[] serials);

    String[] findExistingSerialsBy(String[] serials);
}
