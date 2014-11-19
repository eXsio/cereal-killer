package pl.exsio.ck.serialtable.presenter;

import java.awt.Container;
import java.util.Collection;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import pl.exsio.ck.serialtable.view.AbstractSerialTablePanel;

public class SerialTablePresenterImpl implements SerialTablePresenter {

    private AbstractSerialTablePanel view;

    private TableRowSorter<TableModel> sorter;

    @Override
    public void setView(AbstractSerialTablePanel view) {
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
    public void showSerials(final String[] serials) {

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                JTable table = view.getEntryTable();
                DefaultTableModel tm = this.createTableModel();
                this.fillTableData(tm);
                table.setModel(tm);
                sorter = new TableRowSorter<TableModel>(tm);
                table.setRowSorter(sorter);
                return null;
            }

            private void fillTableData(DefaultTableModel tm) {
                for (String s : serials) {
                    tm.addRow(new Object[]{s});
                }
                tm.setRowCount(serials.length);
            }

            private DefaultTableModel createTableModel() {
                DefaultTableModel tm = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                setTableHeaders(tm);
                return tm;
            }

            private void setTableHeaders(DefaultTableModel tm) {
                tm.setColumnIdentifiers(new String[]{
                    "Nr seryjny"
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
