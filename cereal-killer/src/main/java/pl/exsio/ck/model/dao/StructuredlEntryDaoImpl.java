package pl.exsio.ck.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.main.app.App;
import pl.exsio.ck.model.Entries;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.EntryImpl;
import pl.exsio.ck.progress.presenter.ProgressPresenter;
import pl.exsio.ck.util.ArrayUtil;

public final class StructuredlEntryDaoImpl implements EntryDao {

    public static final String DRIVER = "org.sqlite.JDBC";
    
    public static final String DB_URL = "jdbc:sqlite:database.db";

    private Connection conn;

    private LogPresenter log;

    private ProgressPresenter progress;

    public StructuredlEntryDaoImpl(LogPresenter log) {
        this(log, DB_URL);
        this.setUp();
    }

    public StructuredlEntryDaoImpl(LogPresenter log, String dbUrl) {
        this.log = log;
        this.connect(dbUrl);
        this.setUp();
    }

    @Override
    public void connect(String url) {
        try {
            Class.forName(StructuredlEntryDaoImpl.DRIVER);
        } catch (ClassNotFoundException ex) {
            this.log.log("Brak sterownika JDBC");
            this.log.log(ExceptionUtils.getMessage(ex));
        }

        try {
            conn = DriverManager.getConnection(url);
            this.log.log("ustanowiono połączenie z bazą danych (SQLite), url: " + url);
        } catch (SQLException ex) {
            this.log.log("Problem z otwarciem polaczenia");
            this.log.log(ExceptionUtils.getMessage(ex));
        }
    }

    @Override
    public Entry save(Entry entry, boolean updateExisting) {
        Collection<Entry> entries = this.save(Arrays.asList(new Entry[]{entry}), updateExisting);
        if (entries != null && !entries.isEmpty()) {
            return entries.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public Collection<Entry> save(Collection<Entry> entries, boolean updateExisting) {
        try {
            return this.saveCollection(entries, updateExisting);
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas zapisywania wpisów: " + entries);
            this.log.log(ExceptionUtils.getMessage(ex));
            return null;
        }
    }

    private Collection<Entry> saveCollection(Collection<Entry> entries, boolean updateExising) throws SQLException {
        this.showProgressBar("pracuję...");
        Map<String, List<Entry>> saveMap = this.createSaveMap(entries);
        for (String digest : saveMap.keySet()) {
            List<Entry> entriesGroup = saveMap.get(digest);
            String[] serials = Entries.getSerials(entriesGroup);
            List<String> existingSerials = this.getExistingSerialsFrom(serials);
            List<String> serialsToInsert = new ArrayList<>();
            List<String> serialsToUpdate = new ArrayList<>();
            for (String serial : serials) {
                if (!existingSerials.contains(serial)) {
                    serialsToInsert.add(serial);
                } else {
                    serialsToUpdate.add(serial);
                }
            }
            Entry entry = entriesGroup.iterator().next();
            this.performInserts(serialsToInsert, digest, entry);
            if (updateExising) {
                this.performUpdates(serialsToUpdate, digest, entry);
            }

        }
        this.conn.commit();
        this.hideProgressBar();
        return entries;
    }

    private void performUpdates(List<String> serialsToUpdate, String digest, Entry entry) throws RuntimeException, SQLException {
        int entryId;
        if (!serialsToUpdate.isEmpty()) {
            entryId = this.obtainEntryIdForDigest(digest, entry);
            PreparedStatement pstmt = this.getStatement("update serials set entry_id = ? where serial_no = ?");
            for (String serial : serialsToUpdate) {
                pstmt.setInt(1, entryId);
                pstmt.setString(2, serial);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void performInserts(List<String> serialsToInsert, String digest, Entry entry) throws SQLException, RuntimeException {
        int entryId;
        if (!serialsToInsert.isEmpty()) {
            entryId = this.obtainEntryIdForDigest(digest, entry);
            PreparedStatement pstmt = this.getStatement("insert into serials (serial_no, entry_id) values (?, ?)");
            for (String serial : serialsToInsert) {
                pstmt.setString(1, serial);
                pstmt.setInt(2, entryId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private int obtainEntryIdForDigest(String digest, Entry entry) throws RuntimeException, SQLException {
        int entryId;
        PreparedStatement pstmt = this.getStatement("select id from entries where digest = ?");
        pstmt.setString(1, digest);
        ResultSet result = pstmt.executeQuery();
        if (result.next()) {
            entryId = result.getInt("id");
        } else {
            pstmt = this.getStatement("insert into entries (supplier, buy_invoice_no, recipient, supply_date, sell_date, sell_invoice_no, digest) values(?,?,?,?,?,?,?)");
            pstmt.setString(1, entry.getSupplier());
            pstmt.setString(2, entry.getBuyInvoiceNo());
            pstmt.setString(3, entry.getRecipient());
            pstmt.setDate(4, new Date(entry.getSupplyDate().getTime()));
            pstmt.setDate(5, new Date(entry.getSellDate().getTime()));
            pstmt.setString(6, entry.getSellInvoiceNo());
            pstmt.setString(7, digest);
            pstmt.execute();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new RuntimeException("no keys generated");
                }
                entryId = generatedKeys.getInt(1);
            }
        }
        return entryId;
    }

    private List<String> getExistingSerialsFrom(String[] serials) throws SQLException {
        List<String> existingSerials = new ArrayList<>();
        for (String[] chunk : ArrayUtil.splitArray(serials, Entries.LOOKUP_PAGE_SIZE)) {
            StringBuilder sb = new StringBuilder("select serial_no from serials where serial_no in(");
            for (int i = 0; i < chunk.length; i++) {
                sb.append("?");
                if (i < chunk.length - 1) {
                    sb.append(",");
                }
            }
            PreparedStatement pstmt = this.getStatement(sb.append(")").toString());
            for (int i = 0; i < chunk.length; i++) {
                pstmt.setString(i + 1, chunk[i]);
            }
            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                existingSerials.add(result.getString("serial_no"));
            }
        }
        return existingSerials;
    }

    private Map<String, List<Entry>> createSaveMap(Collection<Entry> entries) {
        Map<String, List<Entry>> saveMap = new LinkedHashMap<>();
        for (Entry e : entries) {
            String digest = e.getDigest();
            List<Entry> entriesGroup = null;
            if (!saveMap.containsKey(digest)) {
                entriesGroup = new ArrayList<>();
                saveMap.put(digest, entriesGroup);
            } else {
                entriesGroup = saveMap.get(digest);
            }
            entriesGroup.add(e);
        }
        return saveMap;
    }

    @Override
    public void clear() {
        try {
            PreparedStatement pstmt = this.getStatement("delete from serials");
            pstmt.executeUpdate();
            pstmt = this.getStatement("delete from entries");
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas czyszczenia tabeli");
            this.log.log(ExceptionUtils.getMessage(ex));
        }
    }

    @Override
    public Entry findOne(int id) {
        try {
            PreparedStatement pstmt = this.getStatement("select "
                    + "serials.id as id, "
                    + "entries.recipient as recipient, "
                    + "entries.buy_invoice_no as buy_invoice_no, "
                    + "entries.sell_date as sell_date, "
                    + "entries.sell_invoice_no as sell_invoice_no, "
                    + "serials.serial_no as serial_no, "
                    + "entries.supplier as supplier, "
                    + "entries.supply_date as supply_date "
                    + "from serials inner join entries on serials.entry_id = entries.id where serials.id = ?");
            pstmt.setInt(1, id);
            return this.getEntry(pstmt.executeQuery());
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas wyszukania wpisu: " + id);
            this.log.log(ExceptionUtils.getMessage(ex));
            return null;
        }
    }

    @Override
    public Entry findOneBySerialNo(String serialNo) {
        try {
            PreparedStatement pstmt = this.getStatement("select "
                    + "serials.id as id, "
                    + "entries.recipient as recipient, "
                    + "entries.buy_invoice_no as buy_invoice_no, "
                    + "entries.sell_date as sell_date, "
                    + "entries.sell_invoice_no as sell_invoice_no, "
                    + "serials.serial_no as serial_no, "
                    + "entries.supplier as supplier, "
                    + "entries.supply_date as supply_date "
                    + "from serials inner join entries on serials.entry_id = entries.id where serials.serial_no = ?");
            pstmt.setString(1, serialNo);
            return this.getEntry(pstmt.executeQuery());
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas wyszukania wpisu: " + serialNo);
            this.log.log(ExceptionUtils.getMessage(ex));
            return null;
        }
    }

    @Override
    public Collection<Entry> findAll() {
        try {
            PreparedStatement pstmt = this.getStatement("select "
                    + "serials.id as id, "
                    + "entries.recipient as recipient, "
                    + "entries.buy_invoice_no as buy_invoice_no, "
                    + "entries.sell_date as sell_date, "
                    + "entries.sell_invoice_no as sell_invoice_no, "
                    + "serials.serial_no as serial_no, "
                    + "entries.supplier as supplier, "
                    + "entries.supply_date as supply_date "
                    + "from serials inner join entries on serials.entry_id = entries.id");
            return this.getEntries(pstmt.executeQuery());
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas wyszukania wpisów");
            this.log.log(ExceptionUtils.getMessage(ex));
            return null;
        }
    }

    @Override
    public Collection<Entry> findBySerialNos(String[] serialNos) {
        try {
            StringBuilder sb = new StringBuilder("select "
                    + "serials.id as id, "
                    + "entries.recipient as recipient, "
                    + "entries.buy_invoice_no as buy_invoice_no, "
                    + "entries.sell_date as sell_date, "
                    + "entries.sell_invoice_no as sell_invoice_no, "
                    + "serials.serial_no as serial_no, "
                    + "entries.supplier as supplier, "
                    + "entries.supply_date as supply_date "
                    + "from serials inner join entries on serials.entry_id = entries.id where serials.serial_no in(");
            for (int i = 0; i < serialNos.length; i++) {
                sb.append("?");
                if (i < serialNos.length - 1) {
                    sb.append(",");
                }
            }
            PreparedStatement pstmt = this.getStatement(sb.append(")").toString());
            for (int i = 0; i < serialNos.length; i++) {
                pstmt.setString(i + 1, serialNos[i]);
            }
            return this.getEntries(pstmt.executeQuery());
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas wyszukania wpisów: " + Arrays.toString(serialNos));
            this.log.log(ExceptionUtils.getMessage(ex));
            return null;
        }
    }

    @Override
    public void close() {
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (SQLException ex) {
                this.log.log("Problem z zamknięciem połączenia");
                this.log.log(ExceptionUtils.getMessage(ex));
            }
        }
    }

    private Entry getEntry(ResultSet rs) throws SQLException {

        Collection<Entry> entries = this.getEntries(rs);
        if (!entries.isEmpty()) {
            return entries.iterator().next();
        } else {
            return null;
        }
    }

    private Collection<Entry> getEntries(ResultSet rs) throws SQLException {
        LinkedHashSet<Entry> entries = new LinkedHashSet<>();
        while (rs.next()) {
            Entry e = new EntryImpl();
            e.setId(rs.getInt("id"));
            e.setRecipient(rs.getString("recipient"));
            e.setBuyInvoiceNo(rs.getString("buy_invoice_no"));
            e.setSellDate(rs.getDate("sell_date"));
            e.setSellInvoiceNo(rs.getString("sell_invoice_no"));
            e.setSerialNo(rs.getString("serial_no"));
            e.setSupplier(rs.getString("supplier"));
            e.setSupplyDate(rs.getDate("supply_date"));
            entries.add(e);
        }
        rs.close();
        return entries;
    }

    private PreparedStatement getStatement(String sql) {
        try {
            this.conn.setAutoCommit(false);
            return this.conn.prepareStatement(sql);
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas przetwarzania zapytania: " + sql);
            this.log.log(ExceptionUtils.getMessage(ex));
            return null;
        }
    }

    private void setUp() {
        try {

            List<String> setupQueries = new LinkedList() {
                {
                    add("CREATE TABLE IF NOT EXISTS entries ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "supplier varchar(255) NOT NULL, "
                            + "buy_invoice_no varchar(255) NOT NULL, "
                            + "recipient varchar(255) NOT NULL, "
                            + "supply_date datetime NOT NULL, "
                            + "sell_date datetime NOT NULL, "
                            + "sell_invoice_no varchar(255) NOT NULL, "
                            + "digest varchar(255) NOT NULL)");

                    add("CREATE TABLE IF NOT EXISTS serials ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "serial_no varchar(255) NOT NULL, "
                            + "entry_id INTEGER NOT NULL, "
                            + "FOREIGN KEY(entry_id) REFERENCES entry(id))");

                    add("CREATE UNIQUE INDEX IF NOT EXISTS unique_serial on serials (serial_no)");
                    add("CREATE UNIQUE INDEX IF NOT EXISTS serials_index on serials (serial_no, entry_id)");
                    add("CREATE UNIQUE INDEX IF NOT EXISTS digest_index on entries (digest)");
                    add("CREATE UNIQUE INDEX  IF NOT EXISTS entries_index on entries(supplier, "
                            + "buy_invoice_no, recipient, supply_date, sell_date, sell_invoice_no)");
                }
            };

            for (String query : setupQueries) {
                PreparedStatement pstmt = this.conn.prepareStatement(query);
                pstmt.execute();
            }

        } catch (SQLException ex) {
            this.log.log("Problem z utworzeniem tabel");
            this.log.log(ExceptionUtils.getMessage(ex));
        }
    }

    private void showProgressBar(final String progressName) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (progress != null) {
                    progress.hide();
                }
                progress = (ProgressPresenter) App.getContext().getBean("progressPresenter");
                progress.setProgressName(progressName);
                progress.show(true);
            }
        }).start();

    }

    private void hideProgressBar() {
        this.progress.hide();
    }

}
