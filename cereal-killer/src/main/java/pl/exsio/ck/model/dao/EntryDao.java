
package pl.exsio.ck.model.dao;

import java.util.Collection;
import pl.exsio.ck.model.Entry;

/**
 *
 * @author exsio
 */
public interface EntryDao {

    Entry save(Entry entry, boolean updateExisting);

    Collection<Entry> save(Collection<Entry> entries, boolean updateExisting);

    void clear();

    Entry findOne(int id);

    Collection<Entry> findAll();

    Entry findOneBySerialNo(String serialNo);

    Collection<Entry> findBySerialNos(String[] serialNos);

    void connect(String url);

    void close();
}
