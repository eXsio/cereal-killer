package pl.exsio.ck.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.model.Entry;

public final class EntryDaoImpl implements EntryDao {

    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_URL = "jdbc:sqlite:database.db";

    private Connection conn;

    private LogPresenter log;

    public EntryDaoImpl(LogPresenter log) {
        this(log, DB_URL);
        this.setUp();
    }

    public EntryDaoImpl(LogPresenter log, String dbUrl) {
        this.log = log;
        this.connect(dbUrl);
        this.setUp();
    }

    @Override
    public void connect(String url) {
        try {
            Class.forName(EntryDaoImpl.DRIVER);
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
        try {
            Entry existing = this.findOneBySerialNo(entry.getSerialNo());
            if (existing == null) {
                return this.insertNewEntry(entry);
            } else {
                if (updateExisting) {
                    return this.updateEntry(entry);
                } else {
                    throw new SQLException("There already is an entry with serial no: " + entry.getSerialNo());
                }
            }
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas zapisywania wpisu: " + entry);
            this.log.log(ExceptionUtils.getMessage(ex));
            return null;
        }
    }

    private Entry insertNewEntry(Entry entry) throws SQLException {
        PreparedStatement pstmt = this.getStatement("insert into entries (serial_no, supplier, buy_invoice_no, recipient, supply_date, sell_date, sell_invoice_no, imported_at) values(?,?,?,?,?,?,?,?)");
        pstmt.setString(1, entry.getSerialNo());
        pstmt.setString(2, entry.getSupplier());
        pstmt.setString(3, entry.getBuyInvoiceNo());
        pstmt.setString(4, entry.getRecipient());
        pstmt.setDate(5, new Date(entry.getSupplyDate().getTime()));
        pstmt.setDate(6, new Date(entry.getSellDate().getTime()));
        pstmt.setString(7, entry.getSellInvoiceNo());
        pstmt.setDate(8, new Date(new java.util.Date().getTime()));
        int affectedRows = pstmt.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating entry failed, no rows affected.");
        }

        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                entry.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating entry failed, no ID obtained.");
            }
        }
        return entry;
    }

    private Entry updateEntry(Entry entry) throws SQLException {
        PreparedStatement pstmt = this.getStatement("update entries set supplier = ?, buy_invoice_no = ?, recipient = ?, supply_date = ?, sell_date = ?, sell_invoice_no = ?, imported_at = ? where serial_no = ?");

        pstmt.setString(1, entry.getSupplier());
        pstmt.setString(2, entry.getBuyInvoiceNo());
        pstmt.setString(3, entry.getRecipient());
        pstmt.setDate(4, new Date(entry.getSupplyDate().getTime()));
        pstmt.setDate(5, new Date(entry.getSellDate().getTime()));
        pstmt.setString(6, entry.getSellInvoiceNo());
        pstmt.setDate(7, new Date(new java.util.Date().getTime()));
        pstmt.setString(8, entry.getSerialNo());
        int affectedRows = pstmt.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Updating entry failed, no rows affected.");
        }

        return entry;
    }

    @Override
    public Collection<Entry> save(Collection<Entry> entries, boolean updateExisting) {
        LinkedHashSet<Entry> saved = new LinkedHashSet<>();
        for (Entry e : entries) {
            Entry savedEntry = this.save(e, updateExisting);
            if (savedEntry != null) {
                saved.add(savedEntry);
            }
        }
        return saved;
    }

    @Override
    public void clear() {
        try {
            PreparedStatement pstmt = this.getStatement("delete from entries");
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas czyszczenia tabeli");
            this.log.log(ExceptionUtils.getMessage(ex));
        }
    }

    @Override
    public Entry findOne(int id) {
        try {
            PreparedStatement pstmt = this.getStatement("select * from entries where id = ?");
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
            PreparedStatement pstmt = this.getStatement("select * from entries where serial_no = ?");
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
            PreparedStatement pstmt = this.getStatement("select * from entries");
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
            String s = Arrays.toString(serialNos);
            StringBuilder sb = new StringBuilder("select * from entries where serial_no in(");
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
            Entry e = new Entry();
            e.setId(rs.getInt("id"));
            e.setRecipient(rs.getString("recipient"));
            e.setBuyInvoiceNo(rs.getString("buy_invoice_no"));
            e.setSellDate(rs.getDate("sell_date"));
            e.setSellInvoiceNo(rs.getString("sell_invoice_no"));
            e.setImportedAt(rs.getDate("imported_at"));
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
            return this.conn.prepareStatement(sql);
        } catch (SQLException ex) {
            this.log.log("wystąpił błąd podczas przetwarzania zapytania: " + sql);
            this.log.log(ExceptionUtils.getMessage(ex));
            return null;
        }
    }

    private void setUp() {
        try {
            String createEntryTable = "CREATE TABLE IF NOT EXISTS entries (id INTEGER PRIMARY KEY AUTOINCREMENT, serial_no varchar(255) UNIQUE, supplier varchar(255), buy_invoice_no varchar(255), recipient varchar(255), supply_date datetime, sell_date datetime, sell_invoice_no varchar(255), imported_at datetime)";
            PreparedStatement pstmt = this.conn.prepareStatement(createEntryTable);
            pstmt.execute();
        } catch (SQLException ex) {
            this.log.log("Problem z utworzeniem tabeli");
            this.log.log(ExceptionUtils.getMessage(ex));
        }
    }

}
