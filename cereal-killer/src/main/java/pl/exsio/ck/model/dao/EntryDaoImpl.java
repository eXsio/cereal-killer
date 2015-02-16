/* 
 * The MIT License
 *
 * Copyright 2015 exsio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.exsio.ck.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.model.Entries;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.EntryImpl;
import pl.exsio.ck.progress.presenter.ProgressHelper;
import pl.exsio.ck.progress.presenter.ProgressPresenter;

/**
 *
 * @author exsio
 */
public final class EntryDaoImpl implements EntryDao {

    public static final String DRIVER = "org.hsqldb.jdbcDriver";
    public static final String DB_URL = "jdbc:hsqldb:database.db";

    protected Connection conn;

    protected LogPresenter log;

    protected final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public EntryDaoImpl(LogPresenter log) {
        this(log, DB_URL);
    }

    public EntryDaoImpl(LogPresenter log, String dbUrl) {
        this.log = log;
        this.connect(dbUrl);
        this.setUp();
    }

    @Override
    public void connect(String url) {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            this.log.log("Brak sterownika JDBC");
            this.log.logThrowable(ex);

        }

        try {
            this.conn = DriverManager.getConnection(url, "sa", "");
            this.conn.setAutoCommit(false);
            this.log.log("ustanowiono połączenie z bazą danych (HSQLDB), url: " + url);
        } catch (SQLException ex) {
            this.log.log("Problem z otwarciem polaczenia");
            this.log.logThrowable(ex);

        }
    }

    @Override
    public void save(Collection<Entry> entries, boolean updateExisting) {
        try {
            this.saveEntryCollection(entries, updateExisting);
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas zapisywania wpisów: " + entries);
            this.log.logThrowable(ex);

        }
    }

    @Override
    public void saveSerials(String[] serials, Entry dataPattern, boolean updateExisting) {
        try {
            ProgressPresenter progress = ProgressHelper.showProgressBar("pracuję...", true);
            this.saveSerialsGroup(serials, dataPattern, updateExisting);
            this.conn.commit();
            System.gc();
            ProgressHelper.hideProgressBar(progress);
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas zapisywania numerów seryjnych: " + Arrays.toString(serials));
            this.log.logThrowable(ex);
        }
    }

    protected void saveEntryCollection(Collection<Entry> entries, boolean updateExising) throws SQLException {
        ProgressPresenter progress = ProgressHelper.showProgressBar("pracuję...", true);
        Map<String, List<Entry>> saveMap = this.createSaveMap(entries);
        for (String digest : saveMap.keySet()) {
            List<Entry> entriesGroup = saveMap.get(digest);
            String[] serials = Entries.getSerials(entriesGroup);
            Entry entry = entriesGroup.iterator().next();
            this.saveSerialsGroup(serials, entry, updateExising);
        }
        this.conn.commit();
        System.gc();
        ProgressHelper.hideProgressBar(progress);
    }

    private void saveSerialsGroup(String[] serials, Entry dataPattern, boolean updateExising) throws SQLException {

        List<String> existingSerials = Arrays.asList(this.matchSerials(serials));
        List<String> serialsToInsert = new ArrayList<>();
        List<String> serialsToUpdate = new ArrayList<>();
        for (String serial : serials) {
            if (!existingSerials.contains(serial)) {
                serialsToInsert.add(serial);
            } else {
                serialsToUpdate.add(serial);
            }
        }
        int entryId = this.obtainEntryId(dataPattern);
        this.performInserts(serialsToInsert, entryId);
        if (updateExising) {
            this.performUpdates(serialsToUpdate, entryId);
        }
    }

    protected void performUpdates(List<String> serialsToUpdate, int entryId) throws RuntimeException, SQLException {
        if (!serialsToUpdate.isEmpty()) {
            PreparedStatement pstmt = this.getStatement("update serials set entry_id = ? where serial_no = ?");
            for (String serial : serialsToUpdate) {
                pstmt.setInt(1, entryId);
                pstmt.setString(2, serial);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    protected void performInserts(List<String> serialsToInsert, int entryId) throws SQLException, RuntimeException {
        if (!serialsToInsert.isEmpty()) {
            PreparedStatement pstmt = this.getStatement("insert into serials (serial_no, entry_id) values (?, ?)");
            for (String serial : serialsToInsert) {
                pstmt.setString(1, serial);
                pstmt.setInt(2, entryId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    protected int obtainEntryId(Entry entry) throws RuntimeException, SQLException {
        int entryId;
        PreparedStatement pstmt = this.getStatement("select id from entries where digest = ?");
        String digest = entry.getDigest();
        if (digest == null) {
            throw new RuntimeException("Digest of an entry cannot be null");
        }
        pstmt.setString(1, digest);
        ResultSet result = pstmt.executeQuery();
        if (result.next()) {
            entryId = result.getInt("id");
        } else {
            pstmt = this.conn.prepareStatement("insert into entries (supplier, buy_invoice_no, recipient, supply_date, sell_date, sell_invoice_no, digest) values(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, entry.getSupplier());
            pstmt.setString(2, entry.getBuyInvoiceNo());
            pstmt.setString(3, entry.getRecipient());
            pstmt.setDate(4, new Date(entry.getSupplyDate().getTime()));
            pstmt.setDate(5, new Date(entry.getSellDate().getTime()));
            pstmt.setString(6, entry.getSellInvoiceNo());
            pstmt.setString(7, digest);
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new RuntimeException("no keys generated");
                }
                entryId = generatedKeys.getInt(1);
            }
        }
        return entryId;
    }

    @Override
    public String[] matchSerials(String[] serials) {
        try {
            List<String> existingSerials = new ArrayList<>();
            PreparedStatement pstmt = this.getStatement("select serial_no from serials where serial_no in(UNNEST(?))");
            pstmt.setObject(1, serials);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                existingSerials.add(result.getString("serial_no"));
            }

            return existingSerials.toArray(new String[existingSerials.size()]);
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas znajdowania numerów seryjnych");
            this.log.logThrowable(ex);
            return null;
        }
    }

    protected Map<String, List<Entry>> createSaveMap(Collection<Entry> entries) {
        Map<String, List<Entry>> saveMap = new LinkedHashMap<>();
        entries.stream().forEach((Entry e) -> {
            String digest = e.getDigest();
            List<Entry> entriesGroup = null;
            if (!saveMap.containsKey(digest)) {
                entriesGroup = new ArrayList<>();
                saveMap.put(digest, entriesGroup);
            } else {
                entriesGroup = saveMap.get(digest);
            }
            entriesGroup.add(e);
        });
        return saveMap;
    }

    @Override
    public ArrayList<Object[]> fetchTableRows(int limit, int offset, String query, String orderBy, String orderDir, String[] serials) {
        try {

            String q = this.buildTableQuery(query, serials, orderBy, orderDir, limit, offset);
            PreparedStatement pstmt = this.getStatement(q);
            this.bindTableQueryParameters(query, pstmt, serials);

            ResultSet result = pstmt.executeQuery();
            ArrayList<Object[]> fetchedRows = new ArrayList<>();

            while (result.next()) {
                fetchedRows.add(new Object[]{
                    result.getString("serial_no"),
                    result.getString("supplier"),
                    sdf.format(result.getDate("supply_date")),
                    result.getString("buy_invoice_no"),
                    result.getString("recipient"),
                    sdf.format(result.getDate("sell_date")),
                    result.getString("sell_invoice_no"),
                    result.getInt("id")
                });
            }
            return fetchedRows;
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas wyszukania wpisów");
            this.log.logThrowable(ex);
            return null;
        }
    }

    private void bindTableQueryParameters(String query, PreparedStatement pstmt, String[] serials) throws SQLException {
        if (query != null) {
            this.bindTableSearchQueryToStatement(pstmt, query);
            if (serials != null) {
                pstmt.setObject(8, serials);
            }
        } else {
            if (serials != null) {
                pstmt.setObject(1, serials);

            }
        }
    }

    private void bindTableSearchQueryToStatement(PreparedStatement pstmt, String query) throws SQLException {
        query = "%" + query + "%";
        pstmt.setString(1, query);
        pstmt.setString(2, query);
        pstmt.setString(3, query);
        pstmt.setString(4, query);
        pstmt.setString(5, query);
        pstmt.setString(6, query);
        pstmt.setString(7, query);
    }

    private String buildTableQuery(String query, String[] serials, String orderBy, String orderDir, int limit, int offset) {
        if (orderBy == null) {
            orderBy = "id";
        }
        if (orderDir == null) {
            orderDir = SORT_DESC;
        }

        String q = "select "
                + "serials.id as id, "
                + "entries.recipient as recipient, "
                + "entries.buy_invoice_no as buy_invoice_no, "
                + "entries.sell_date as sell_date, "
                + "entries.sell_invoice_no as sell_invoice_no, "
                + "serials.serial_no as serial_no, "
                + "entries.supplier as supplier, "
                + "entries.supply_date as supply_date "
                + "from serials inner join entries on serials.entry_id = entries.id";
        q = this.addTableSearchQuery(query, q);
        q = this.addTableSerialsFilterQuery(serials, query, q);
        q += " order by " + orderBy + " " + orderDir + " limit " + limit + " offset " + offset;
        return q;
    }

    private String addTableSerialsFilterQuery(String[] serials, String query, String q) {
        if (serials != null) {
            if (query != null) {
                q += " and ";
            } else {
                q += " where ";
            }
            q += "serials.serial_no in(unnest(?))";

        }
        return q;
    }

    private String addTableSearchQuery(String query, String q) {
        if (query != null) {
            q += " where (entries.recipient like ? or "
                    + "entries.buy_invoice_no like ? or "
                    + "entries.sell_invoice_no like ? or "
                    + "entries.sell_date like ? or "
                    + "entries.supplier like ? or "
                    + "entries.supply_date like ? or "
                    + "serials.serial_no like ?)";
        }
        return q;
    }

    @Override
    public int count(String query, String[] serials) {

        try {
            String q = "select count(*) "
                    + "from serials inner join entries on serials.entry_id = entries.id";
            q = this.addTableSearchQuery(query, q);
            q = this.addTableSerialsFilterQuery(serials, query, q);
            PreparedStatement pstmt = this.getStatement(q);
            bindTableQueryParameters(query, pstmt, serials);
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas wyszukania wpisów");
            this.log.logThrowable(ex);
            return -1;
        }
    }

    @Override
    public void close() {
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (SQLException ex) {
                this.log.log("Problem z zamknięciem połączenia");
                this.log.logThrowable(ex);

            }
        }
    }

    protected Entry getEntry(ResultSet rs) throws SQLException {
        Collection<Entry> entries = this.getEntries(rs);
        if (!entries.isEmpty()) {
            return entries.iterator().next();
        } else {
            return null;
        }
    }

    protected Collection<Entry> getEntries(ResultSet rs) throws SQLException {
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

    protected PreparedStatement getStatement(String sql) {
        try {
            return this.conn.prepareStatement(sql);
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas przetwarzania zapytania: " + sql);
            this.log.logThrowable(ex);
            return null;
        }
    }

    protected void setUp() {
        try {

            List<String> setupQueries = new LinkedList() {
                {
                    add("CREATE TABLE IF NOT EXISTS entries ("
                            + "id INTEGER IDENTITY, "
                            + "supplier varchar(255) NOT NULL, "
                            + "buy_invoice_no varchar(255) NOT NULL, "
                            + "recipient varchar(255) NOT NULL, "
                            + "supply_date datetime NOT NULL, "
                            + "sell_date datetime NOT NULL, "
                            + "sell_invoice_no varchar(255) NOT NULL, "
                            + "digest varchar(255) NOT NULL,"
                            + "UNIQUE(supplier, buy_invoice_no, recipient, "
                            + "supply_date, sell_date, sell_invoice_no),"
                            + "UNIQUE(digest))");

                    add("CREATE TABLE IF NOT EXISTS serials ("
                            + "id INTEGER IDENTITY, "
                            + "serial_no varchar(255) NOT NULL, "
                            + "entry_id INTEGER NOT NULL, "
                            + "FOREIGN KEY(entry_id) REFERENCES entries(id),"
                            + "UNIQUE(serial_no),"
                            + "UNIQUE(serial_no, entry_id))");

                    add("CREATE TABLE IF NOT EXISTS settings ("
                            + "id INTEGER IDENTITY, "
                            + "s_name varchar(255) NOT NULL, "
                            + "s_value varchar(255) NOT NULL)");

                    add("INSERT INTO settings(s_name, s_value) "
                            + "values ('index_created','false')");

                }
            };

            for (String query : setupQueries) {
                PreparedStatement pstmt = this.conn.prepareStatement(query);
                pstmt.execute();
            }
            this.conn.commit();
            this.checkAndCreateIndex();
        } catch (SQLException ex) {
            this.log.log("Problem z utworzeniem tabel");
            this.log.logThrowable(ex);

        }
    }

    private void checkAndCreateIndex() {

        try {
            PreparedStatement pstmt = this.conn.prepareStatement("select s_value from settings where s_name = 'index_created'");
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                Boolean indexCreated = Boolean.valueOf(result.getString("s_value"));
                if (!indexCreated) {
                    this.log.log("zakładam index");
                    pstmt = this.conn.prepareStatement("CREATE INDEX serials_index ON serials(serial_no)");
                    pstmt.execute();
                    pstmt = this.conn.prepareStatement("UPDATE settings set s_value = 'true' where s_name='index_created'");
                    pstmt.execute();
                    this.conn.commit();
                    this.log.log("indeks został utworzony");
                } else {
                    this.log.log("weryfikacja indexu zakończona powodzeniem");
                }
            }
        } catch (SQLException ex) {
            this.log.log("Problem z założeniem indexu");
            this.log.logThrowable(ex);

        }
    }

}
