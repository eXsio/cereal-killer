package pl.exsio.ck.model.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.EntryImpl;

/**
 *
 * @author exsio
 */
public class EntryDaoImplTest {

    private EntryDao dao;

    public EntryDaoImplTest() {
    }

    @Before
    public void setUp() {
        this.dao = new EntryDaoImpl(getMockLogPresenter(), "jdbc:sqlite:test_database.db");
    }

    @After
    public void tearDown() {
        this.dao.close();
        File f = new File("test_database.db");
        f.delete();
    }

    /**
     * Test of save method, of class EntryDaoImpl.
     */
    @Test
    public void testSave_Entry() {
        Entry e = this.getEntry(0);
        this.dao.save(e, false);
        assertNotNull(e.getId());
    }

    /**
     * Test of save method, of class EntryDaoImpl.
     */
    @Test
    public void testSave_Collection() {

        Collection<Entry> entries = this.getEntries(10);
        entries = this.dao.save(entries, false);
        assertEquals(entries.size(), 10);
        for (Entry e : entries) {
            assertNotNull(e.getId());
        }
    }

    /**
     * Test of clear method, of class EntryDaoImpl.
     */
    @Test
    public void testClear() {

        Entry e = this.getEntry(0);
        this.dao.save(e, false);
        assertNotNull(e.getId());
        this.dao.clear();
        assertNull(this.dao.findOneBySerialNo("0"));
    }

    /**
     * Test of findOne method, of class EntryDaoImpl.
     */
    @Test
    public void testFindOne() {

        Entry e = this.getEntry(0);
        this.dao.save(e, false);
        assertNotNull(e.getId());
        Entry foundEntry = this.dao.findOne(e.getId());
        assertNotNull(foundEntry);
        assertEquals(foundEntry.getSerialNo(), "0");
    }

    /**
     * Test of findOneBySerialNo method, of class EntryDaoImpl.
     */
    @Test
    public void testFindOneBySerialNo() {

        Entry e = this.getEntry(0);
        this.dao.save(e, false);
        assertNotNull(e.getId());
        Entry foundEntry = this.dao.findOneBySerialNo("0");
        assertNotNull(foundEntry);
        assertEquals(foundEntry.getSerialNo(), "0");
    }

    /**
     * Test of findBySerialNos method, of class EntryDaoImpl.
     */
    @Test
    public void testFindBySerialNos() {

        this.testSave_Collection();
        Collection<Entry> entries = this.dao.findBySerialNos(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
        assertEquals(entries.size(), 10);
        for (Entry e : entries) {
            assertNotNull(e.getId());
            assertNotNull(e.getSerialNo());
        }
    }

    @Test
    public void testFindAll() {

        this.testSave_Collection();
        Collection<Entry> entries = this.dao.findAll();
        assertEquals(entries.size(), 10);
        for (Entry e : entries) {
            assertNotNull(e.getId());
            assertNotNull(e.getSerialNo());
        }
    }

    private Entry getEntry(int index) {
        Entry e = new EntryImpl();
        e.setSerialNo("" + index);
        e.setRecipient("Recipient " + index);
        e.setSellDate(new Date());
        e.setSellInvoiceNo("Sell Invoice " + index);
        e.setBuyInvoiceNo("Buy Invoice " + index);
        e.setSupplier("Supplier " + index);
        e.setSupplyDate(new Date());
        return e;
    }

    private Collection<Entry> getEntries(int count) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entries.add(this.getEntry(i));
        }
        return entries;
    }

    private LogPresenter getMockLogPresenter() {
        LogPresenter mock = mock(LogPresenter.class);
        return mock;
    }

}
