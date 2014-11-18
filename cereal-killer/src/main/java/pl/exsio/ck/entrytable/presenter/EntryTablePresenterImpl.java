package pl.exsio.ck.entrytable.presenter;

import java.awt.Container;
import java.text.SimpleDateFormat;
import java.util.Collection;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import pl.exsio.ck.entrytable.view.AbstractEntryTablePanel;
import pl.exsio.ck.model.Entry;

public class EntryTablePresenterImpl implements EntryTablePresenter {

    private AbstractEntryTablePanel view;

    private final SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    private final SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private TableRowSorter<TableModel> sorter;

    @Override
    public void setView(AbstractEntryTablePanel view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void filter(String filterStr) {
        if (this.sorter != null) {
            if (filterStr.trim().length() == 0) {
                this.sorter.setRowFilter(null);
            } else {
                this.sorter.setRowFilter(RowFilter.regexFilter(filterStr.trim()));
            }
        }
    }

    @Override
    public void showEntries(final Collection<Entry> entries) {

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                JTable table = view.getEntryTable();
                DefaultTableModel tm = this.getTableModel();
                this.fillTableData(tm);
                table.setModel(tm);
                this.formatColumns(table);
                sorter = new TableRowSorter<TableModel>(tm);
                table.setRowSorter(sorter);
                return null;
            }

            private void formatColumns(JTable table) {
                TableColumnModel tcm = table.getColumnModel();
                tcm.getColumn(8).setPreferredWidth(15);
                tcm.getColumn(9).setPreferredWidth(15);
            }

            private void fillTableData(DefaultTableModel tm) {
                for (Entry e : entries) {
                    tm.addRow(new Object[]{e.getSerialNo(), e.getSupplier(),
                        date.format(e.getSupplyDate()), e.getBuyInvoiceNo(),
                        e.getRecipient(), date.format(e.getSellDate()),
                        e.getSellInvoiceNo(), datetime.format(e.getImportedAt()), e.getId()});

                }
                tm.setRowCount(entries.size());
            }

            private DefaultTableModel getTableModel() {
                DefaultTableModel tm = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }

                    @Override
                    public Class getColumnClass(int col) {
                        if (col == 8 || col == 9) {
                            return Integer.class;
                        } else {
                            return String.class;
                        }
                    }

                    @Override
                    public Object getValueAt(int row, int col) {
                        if (col == this.getColumnCount() - 1) {
                            return row + 1;
                        } else {
                            return super.getValueAt(row, col);
                        }
                    }
                };
                this.setTableHeaders(tm);

                return tm;
            }

            private void setTableHeaders(DefaultTableModel tm) {
                tm.setColumnIdentifiers(new String[]{
                    "Nr seryjny", "Dostawca", "Data dostawy",
                    "Nr faktury zakupu", "Odbiorca", "Data sprzedaży",
                    "Nr faktury sprzedaży", "Data importu", "Id", "L.p"
                });
            }

        };
        worker.execute();

    }

    @Override

    public Container getView() {
        return this.view;
    }

}
